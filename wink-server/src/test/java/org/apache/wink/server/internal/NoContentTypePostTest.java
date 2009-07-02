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

package org.apache.wink.server.internal;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.wink.common.http.HttpStatus;
import org.apache.wink.server.internal.servlet.MockServletInvocationTest;
import org.apache.wink.test.mock.MockRequestConstructor;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * Test response on POST without Content-Type HTTP header in request.
 */
public class NoContentTypePostTest extends MockServletInvocationTest {

    @Override
    protected Object[] getSingletons() {
        return new Object[] { new PostResource() };
    }

    @Path("t")
    public static class PostResource {

        @POST
        @Consumes(MediaType.APPLICATION_ATOM_XML)
        @Produces(MediaType.APPLICATION_ATOM_XML)
        public String doPost() {
            return "POSTED";
        }

    }

    public void testPostWithoutContentType() throws Exception {
        MockHttpServletRequest request = MockRequestConstructor.constructMockRequest("POST", "t",
            "*/*");
        MockHttpServletResponse response = invoke(request);
        assertEquals("status code", HttpStatus.UNSUPPORTED_MEDIA_TYPE.getCode(),
            response.getStatus());
    }

}
