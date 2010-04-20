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
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-456 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.07.20 at 10:55:05 AM IST 
//
package org.apache.wink.common.model.rss;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.apache.wink.common.model.synd.SyndLink;

/**
 * <p>
 * Java class for "enclosure" element of <a
 * href="http://www.rssboard.org/rss-specification">RSS 2.0 Specification</a>.
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name=&quot;rssEnclosure&quot;&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base=&quot;{http://www.w3.org/2001/XMLSchema}anyType&quot;&gt;
 *       &lt;attribute name=&quot;url&quot; use=&quot;required&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; /&gt;
 *       &lt;attribute name=&quot;length&quot; use=&quot;required&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; /&gt;
 *       &lt;attribute name=&quot;type&quot; use=&quot;required&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * <h5>&lt;enclosure&gt; sub-element of &lt;item&gt;</h5>
 * <p>
 * &lt;enclosure&gt; is an optional sub-element of &lt;item&gt;.
 * </p>
 * <p>
 * It has three required attributes. url says where the enclosure is located,
 * length says how big it is in bytes, and type says what its type is, a
 * standard MIME type.
 * </p>
 * <p>
 * The url must be an http url.
 * </p>
 * <p class="example">&lt;enclosure
 * url="http://www.scripting.com/mp3s/weatherReportSuite.mp3" length="12216320"
 * type="audio/mpeg" /&gt;</p>
 * <p>
 * A use-case narrative for this element is <a
 * href="http://www.rssboard.org/rss-enclosures-use-case">here</a>.
 * </p>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "rssEnclosure")
public class RssEnclosure {

    @XmlAttribute(required = true)
    protected String url;
    @XmlAttribute(required = true)
    protected String length;
    @XmlAttribute(required = true)
    protected String type;

    /**
     * Creates an RssEnclosure object
     */
    public RssEnclosure() {
    }

    /**
     * Creates an RssEnclosure object out of a SyndLink object. Used for mapping
     * Syndication Object Model into RSS.
     * 
     * @param syndLink the SyndLink object which has to be mapped into an
     *            RssEnclosure object.
     */
    public RssEnclosure(SyndLink syndLink) {
        setType(syndLink.getType());
        setUrl(syndLink.getHref());
        setLength(syndLink.getLength());
    }

    /**
     * Maps an RssEnclosure object into a SyndLink object. Used for mapping RSS
     * into Syndication Object Model.
     * 
     * @param syndLink the SyndLink object into which the given RssEnclosure
     *            object has to be mapped into
     * @return the SyndLink object into which the given RssEnclosure object has
     *         been mapped into
     */
    public SyndLink toSynd(SyndLink syndLink) {
        if (syndLink == null) {
            return syndLink;
        }
        syndLink.setRel("enclosure");
        syndLink.setType(getType());
        syndLink.setLength(getLength());
        syndLink.setHref(getUrl());
        return syndLink;
    }

    /**
     * Gets the value of the url property.
     * 
     * @return possible object is {@link String }
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the value of the url property.
     * 
     * @param value allowed object is {@link String }
     */
    public void setUrl(String value) {
        this.url = value;
    }

    /**
     * Gets the value of the length property.
     * 
     * @return possible object is {@link String }
     */
    public String getLength() {
        return length;
    }

    /**
     * Sets the value of the length property.
     * 
     * @param value allowed object is {@link String }
     */
    public void setLength(String value) {
        this.length = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return possible object is {@link String }
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value allowed object is {@link String }
     */
    public void setType(String value) {
        this.type = value;
    }

}
