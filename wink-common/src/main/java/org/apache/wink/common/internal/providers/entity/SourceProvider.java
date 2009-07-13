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
 

package org.apache.wink.common.internal.providers.entity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.wink.common.internal.utils.MediaTypeUtils;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public abstract class SourceProvider implements MessageBodyWriter<Source> {

    private static TransformerFactory transformerFactory;
    private static DocumentBuilderFactory documentBuilderFactory;
    
    static {
        transformerFactory = TransformerFactory.newInstance();
        documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
    }

    @Provider
    @Consumes({MediaType.TEXT_XML, MediaType.APPLICATION_XML, MediaType.WILDCARD})
    @Produces({MediaType.TEXT_XML, MediaType.APPLICATION_XML, MediaType.WILDCARD})
    public static class StreamSourceProvider extends SourceProvider implements MessageBodyReader<Source> {

        public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            return (type.isAssignableFrom(StreamSource.class) && super.isReadable(mediaType));
        }

        public StreamSource readFrom(Class<Source> type, Type genericType, Annotation[] annotations,
                MediaType mediaType, MultivaluedMap<String,String> httpHeaders, InputStream entityStream)
                throws IOException, WebApplicationException {
            return new StreamSource(entityStream);
        }
    }
    
    @Provider
    @Consumes({MediaType.TEXT_XML, MediaType.APPLICATION_XML, MediaType.WILDCARD})
    @Produces({MediaType.TEXT_XML, MediaType.APPLICATION_XML, MediaType.WILDCARD})
    public static class SAXSourceProvider extends SourceProvider implements MessageBodyReader<SAXSource> {

        public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            return (SAXSource.class == type && super.isReadable(mediaType));
        }

        public SAXSource readFrom(Class<SAXSource> type, Type genericType, Annotation[] annotations,
                MediaType mediaType, MultivaluedMap<String,String> httpHeaders, InputStream entityStream)
                throws IOException, WebApplicationException {
            return new SAXSource(new InputSource(entityStream));
        }
    }
    
    @Provider
    @Consumes({MediaType.TEXT_XML, MediaType.APPLICATION_XML, MediaType.WILDCARD})
    @Produces({MediaType.TEXT_XML, MediaType.APPLICATION_XML, MediaType.WILDCARD})
    public static class DOMSourceProvider extends SourceProvider implements MessageBodyReader<DOMSource> {

        public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            return (DOMSource.class == type && super.isReadable(mediaType));
        }

        public DOMSource readFrom(Class<DOMSource> type, Type genericType, Annotation[] annotations,
                MediaType mediaType, MultivaluedMap<String,String> httpHeaders, InputStream entityStream)
                throws IOException, WebApplicationException {
            try {
                return new DOMSource(documentBuilderFactory.newDocumentBuilder().parse(entityStream));
            } catch (SAXException e) {
                throw asIOExcpetion(e);
            } catch (ParserConfigurationException e) {
                throw asIOExcpetion(e);
            }
        }
    }

    protected boolean isReadable(MediaType mediaType) {
        return MediaTypeUtils.isXmlType(mediaType);
    }

    public long getSize(Source t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return (Source.class.isAssignableFrom(type) && MediaTypeUtils.isXmlType(mediaType));
    }

    public void writeTo(Source t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String,Object> httpHeaders, OutputStream entityStream) throws IOException,
            WebApplicationException {
        StreamResult sr = new StreamResult(entityStream);
        Transformer transformer;
        try {
            transformer = transformerFactory.newTransformer();
            transformer.transform(t, sr);
        } catch (TransformerException e) {
            throw asIOExcpetion(e);
        }
    }

    private static IOException asIOExcpetion(Exception e) throws IOException {
        IOException exception = new IOException();
        exception.initCause(e);
        return exception;
    }

}
