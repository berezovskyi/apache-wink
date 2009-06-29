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
package org.apache.wink.server.internal.handlers;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.RuntimeDelegate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wink.server.handlers.AbstractHandler;
import org.apache.wink.server.handlers.MessageContext;


public class PopulateErrorResponseHandler extends AbstractHandler {

    private static final RuntimeDelegate RUNTIME_DELEGATE = RuntimeDelegate.getInstance();
    private static final Log             logger           = LogFactory.getLog(PopulateErrorResponseHandler.class);

    @SuppressWarnings("unchecked")
    public void handleResponse(MessageContext context) throws Throwable {
        Object result = context.getResponseEntity();
        if (result instanceof WebApplicationException) {
            handleWebApplicationException(context, (WebApplicationException) result);
        } else {
            Throwable exception = (Throwable) result;
            ExceptionMapper<Throwable> provider = (ExceptionMapper<Throwable>) findProvider(
                    context, exception);
            if (provider != null) {
                context.setResponseEntity(executeProvider(exception, provider));
            } else {
                throw exception;
            }
        }
    }

    private Response executeProvider(Throwable exception, ExceptionMapper<Throwable> provider) {
        try {
            return provider.toResponse(exception);
        } catch (Throwable e) {
            logger.error("Exception occured while executing toResponse of the ExceptionMapper", e);
            return RUNTIME_DELEGATE.createResponseBuilder().status(500).build();
        }
    }

    private ExceptionMapper<? extends Throwable> findProvider(MessageContext msgContext,
        Throwable result) {
        return msgContext.getProviders().getExceptionMapper(result.getClass());
    }

    @SuppressWarnings("unchecked")
    private void handleWebApplicationException(MessageContext msgContext,
        WebApplicationException exception) {
        ExceptionMapper<WebApplicationException> provider = null;
        if (exception.getResponse().getEntity() == null) {
            provider = (ExceptionMapper<WebApplicationException>) findProvider(msgContext,
                exception);
        }
        if (provider != null) {
            msgContext.setResponseEntity(provider.toResponse(exception));
        } else {
            msgContext.setResponseEntity(exception.getResponse());
        }
    }

}
