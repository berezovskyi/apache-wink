/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *  
 *   http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *  
 *******************************************************************************/

package org.apache.wink.server.internal.registry;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.wink.common.DynamicResource;
import org.apache.wink.common.internal.lifecycle.LifecycleManagersRegistry;
import org.apache.wink.common.internal.lifecycle.ObjectFactory;
import org.apache.wink.common.internal.registry.metadata.ClassMetadata;
import org.apache.wink.common.internal.registry.metadata.ResourceMetadataCollector;
import org.apache.wink.common.internal.runtime.RuntimeContext;
import org.apache.wink.common.internal.uritemplate.UriTemplateProcessor;

public class ResourceRecordFactory {

    private static final Logger logger = LoggerFactory.getLogger(ResourceRecordFactory.class);

    private final LifecycleManagersRegistry lifecycleManagerRegistry;
    private final Map<Class<?>,ResourceRecord> cacheByClass;

    private Lock readersLock;
    private Lock writersLock;

    public ResourceRecordFactory(LifecycleManagersRegistry lifecycleManagerRegistry) {
        if (lifecycleManagerRegistry == null) {
            throw new NullPointerException("lifecycleManagerRegistry");
        }
        this.lifecycleManagerRegistry = lifecycleManagerRegistry;
        this.cacheByClass = new HashMap<Class<?>,ResourceRecord>();
        ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
        readersLock = readWriteLock.readLock();
        writersLock = readWriteLock.writeLock();
    }

    /**
     * Gets a resource record from a cache of records for the specified resource class. If there is
     * no record in the cache, then a new record is created
     * 
     * @param cls
     *            the resource class to get the record for
     * @return ResourceRecord for the resource class
     */
    public ResourceRecord getResourceRecord(Class<?> cls) {
        readersLock.lock();
        try {
            ResourceRecord record = cacheByClass.get(cls);
            if (record == null) {
                ObjectFactory<?> of = lifecycleManagerRegistry.getObjectFactory(cls);
                readersLock.unlock();
                try {
                    record = createStaticResourceRecord(cls, of);
                } finally {
                    readersLock.lock();
                }
            }
            return record;
        } finally {
            readersLock.unlock();
        }
    }

    /**
     * Gets a root resource record from a cache of records for the specified resource instance. This
     * is a shortcut for {@code getResourceRecord(instance, true)}
     * 
     * @param instance
     *            the resource instance to get the record for
     * @return ResourceRecord for the resource instance
     */
    public ResourceRecord getResourceRecord(Object instance) {
        return getResourceRecord(instance, true);
    }

    /**
     * Gets a resource record from a cache of records for the specified resource instance. If there
     * is no record in the cache, or if the instance is a dynamic resource, then a new record is
     * created
     * 
     * @param instance
     *            the resource instance to get the record for
     * @param isRootResource
     *            specifies whether the instance is a root resource (true) or sub-resource (false)
     * @return ResourceRecord for the resource instance
     */
    public ResourceRecord getResourceRecord(Object instance, boolean isRootResource) {
        Class<? extends Object> cls = instance.getClass();
        ResourceRecord record = null;
        readersLock.lock();
        try {
            // if this is a root resource
            if (isRootResource) {
                if (ResourceMetadataCollector.isStaticResource(cls)) {
                    // if this is a static resource, and use cache
                    record = cacheByClass.get(cls);
                    if (record == null) {
                        ObjectFactory<?> of = lifecycleManagerRegistry.getObjectFactory(instance);
                        readersLock.unlock();
                        try {
                            record = createStaticResourceRecord(cls, of);
                        } finally {
                            readersLock.lock();
                        }
                    }
                } else if (ResourceMetadataCollector.isDynamicResource(cls)) {
                    // if this is a dynamic resource, don't use cache
                    ObjectFactory<?> of = lifecycleManagerRegistry.getObjectFactory(instance);
                    readersLock.unlock();
                    try {
                        record = createDynamicResourceRecord((DynamicResource)instance, of);
                    } finally {
                        readersLock.lock();
                    }
                } else {
                    throw new IllegalArgumentException(String.format(
                            "Root resource %s instance is an invalid resource", instance.getClass()
                                    .getCanonicalName()));
                }
            } else {
                // if this is a sub-resource, don't use cache, and don't use the life-cycle manager
                ObjectFactory<?> of = new InstanceObjectFactory<Object>(instance);
                readersLock.unlock();
                try {
                    record = createSubResourceRecord(instance, of);
                } finally {
                    readersLock.lock();
                }
            }
            return record;
        } finally {
            readersLock.unlock();
        }
    }

    private ResourceRecord createStaticResourceRecord(Class<? extends Object> cls,
            ObjectFactory<?> of) {
        ClassMetadata metadata = createMetadata(cls);
        UriTemplateProcessor processor = createUriTemplateProcessor(metadata);
        ResourceRecord record = new ResourceRecord(metadata, of, processor);
        writersLock.lock();
        try {
            // double check so as not to put the same resource twice
            if (cacheByClass.get(cls) == null) {
                cacheByClass.put(cls, record);
            }
        } finally {
            writersLock.unlock();
        }
        return record;
    }

    private ResourceRecord createDynamicResourceRecord(DynamicResource instance, ObjectFactory<?> of) {
        Class<? extends Object> cls = instance.getClass();
        ClassMetadata metadata = createMetadata(cls);
        metadata = fixInstanceMetadata(metadata, instance);
        UriTemplateProcessor processor = createUriTemplateProcessor(metadata);
        return new ResourceRecord(metadata, of, processor);
    }

    private ResourceRecord createSubResourceRecord(Object instance, ObjectFactory<?> of) {
        Class<? extends Object> cls = instance.getClass();
        ClassMetadata metadata = createMetadata(cls);
        return new ResourceRecord(metadata, of, null);
    }

    private ClassMetadata createMetadata(Class<? extends Object> cls) {
        return ResourceMetadataCollector.collectMetadata(cls);
    }

    private UriTemplateProcessor createUriTemplateProcessor(ClassMetadata metadata) {
        // create the resource path using the parents paths
        StringBuilder path = new StringBuilder();
        // Recursively append parent paths
        appendPathWithParent(metadata, path);
        // create the processor
        return UriTemplateProcessor.newNormalizedInstance(path.toString());
    }

    private void appendPathWithParent(ClassMetadata metadata, StringBuilder pathStr) {
        ResourceRecord parentRecord = getParent(metadata);
        if (parentRecord != null) {
            ClassMetadata parentMetadata = parentRecord.getMetadata();
            appendPathWithParent(parentMetadata, pathStr);
        }
        String path = UriTemplateProcessor.normalizeUri(metadata.getPath());
        if (!path.endsWith("/")) {
            pathStr.append("/");
        }
        pathStr.append(path);
    }

    private ResourceRecord getParent(ClassMetadata metadata) {
        Class<?> parent = metadata.getParent();
        Object parentInstance = metadata.getParentInstance();
        ResourceRecord parentRecord = null;
        if (parent != null) {
            parentRecord = getResourceRecord(parent);
        } else if (parentInstance != null) {
            parentRecord = getResourceRecord(parentInstance);
        }
        return parentRecord;
    }

    /**
     * Fixed the metadata to reflect the information stored on the instance of the dynamic resource.
     * 
     * @param classMetadata
     * @param instance
     * @return
     */
    private ClassMetadata fixInstanceMetadata(ClassMetadata classMetadata,
            DynamicResource dynamicResource) {
        String[] dispatchedPath = dynamicResource.getDispatchedPath();
        if (dispatchedPath != null) {
            classMetadata.addPaths(Arrays.asList(dispatchedPath));
            if (logger.isDebugEnabled()) {
                logger.debug("Adding dispatched path from instance: {}", Arrays
                        .toString(dispatchedPath));
            }
        }

        Object[] parents = dynamicResource.getParents();
        if (parents != null) {
            classMetadata.getParentInstances().addAll(Arrays.asList(parents));
            if (logger.isDebugEnabled()) {
                logger.debug("Adding parent beans from instance: {}", Arrays.toString(parents));
            }
        }

        String workspaceTitle = dynamicResource.getWorkspaceTitle();
        if (workspaceTitle != null) {
            classMetadata.setWorkspaceName(workspaceTitle);
        }

        String collectionTitle = dynamicResource.getCollectionTitle();
        if (collectionTitle != null) {
            classMetadata.setCollectionTitle(collectionTitle);
        }
        return classMetadata;
    }

    private static class InstanceObjectFactory<T> implements ObjectFactory<T> {

        private final T object;

        public InstanceObjectFactory(T object) {
            this.object = object;
        }

        public T getInstance(RuntimeContext context) {
            return object;
        }

        @SuppressWarnings("unchecked")
        public Class<T> getInstanceClass() {
            return (Class<T>)object.getClass();
        }

        @Override
        public String toString() {
            return String.format("InstanceObjectFactory: %s", getInstanceClass());
        }
    }

}
