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
//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.1.1-b02-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2008.09.15 at 12:57:27 PM IDT 
//


package org.apache.wink.common.model.app;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessOrder;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorOrder;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.wink.common.RestConstants;
import org.apache.wink.common.RestException;
import org.apache.wink.common.internal.model.NamespacePrefixMapperProvider;
import org.apache.wink.common.internal.utils.JAXBUtils;
import org.apache.wink.common.model.JAXBNamespacePrefixMapper;
import org.apache.wink.common.model.atom.AtomCommonAttributes;
import org.apache.wink.common.model.atom.AtomJAXBUtils;
import org.w3c.dom.Element;


/**
 * The "app:service" Element Per RFC5023
 * 
 * <pre>
 * The root of a Service Document is the "app:service" element.
 * 
 * The app:service element is the container for service information associated with one or more
 * Workspaces. An app:service element MUST contain one or more app:workspace elements.
 * 
 * namespace app = "http://www.w3.org/2007/app" 
 * start = appService
 * 
 * appService =
 *     element app:service {
 *        appCommonAttributes,
 *        ( appWorkspace+
 *          & extensionElement* )
 *     }
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlAccessorOrder(XmlAccessOrder.UNDEFINED)
@XmlType(name = "appService", propOrder = {
    "workspace",
    "any"
})
public class AppService
    extends AtomCommonAttributes
    implements NamespacePrefixMapperProvider
{

    @XmlElement(required = true)
    protected List<AppWorkspace> workspace;
    @XmlAnyElement
    protected List<Element> any;

    // ============================
    @XmlTransient
    private static final JAXBContext context;

    static {
        try {
            context = JAXBContext.newInstance(AppService.class.getPackage().getName());
        } catch (JAXBException e) {
            throw new RestException("Failed to create JAXBContext for AppService", e);
        }
    }

    public static Marshaller getMarshaller() {
        return JAXBUtils.createMarshaller(context);
    }
    
    public static Unmarshaller getUnmarshaller() {
        return JAXBUtils.createUnmarshaller(context);
    }

    /**
     * Convenience method for creating an AppService from xml
     * @param reader input reader
     * @return AppService instance from the input
     */
    public static AppService unmarshal(Reader reader) {
        try {
            return (AppService)AtomJAXBUtils.unmarshal(AppService.getUnmarshaller(), reader);
        } catch (IOException e) {
            throw new RestException(e);
        }
    }

    public JAXBNamespacePrefixMapper getNamespacePrefixMapper() {
        JAXBNamespacePrefixMapper mapper = new JAXBNamespacePrefixMapper(RestConstants.NAMESPACE_APP);
        mapper.omitNamespace(RestConstants.NAMESPACE_OPENSEARCH);
        return mapper;
    }
    // ============================

    /**
     * Gets the workspaces
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the workspace property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getWorkspace().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AppWorkspace }
     * 
     * 
     */
    public List<AppWorkspace> getWorkspace() {
        if (workspace == null) {
            workspace = new ArrayList<AppWorkspace>();
        }
        return this.workspace;
    }
    
    public AppWorkspace getWorkspace(String title) {
        if (title == null) {
            return null;
        }
        
        List<AppWorkspace> workspaces = getWorkspace();
        for (AppWorkspace workspace : workspaces) {
            if (title.equals(workspace.getTitle().getValue())) {
                return workspace;
            }
        }
        return null;
    }

    /**
     * Gets extension elements
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the any property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAny().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Element }
     * 
     * 
     */
    public List<Element> getAny() {
        if (any == null) {
            any = new ArrayList<Element>();
        }
        return this.any;
    }

}
