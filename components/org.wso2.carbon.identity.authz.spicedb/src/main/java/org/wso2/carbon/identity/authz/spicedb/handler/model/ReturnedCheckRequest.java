/*
 * Copyright (c) 2025, WSO2 LLC. (http://www.wso2.com).
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.identity.authz.spicedb.handler.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.wso2.carbon.identity.authz.spicedb.constants.SpiceDbModelConstants;

import java.util.Collections;
import java.util.Map;

/**
 * The {@code ReturnedCheckRequest} class represents a request that has been returned in a bulk check response. Result
 * of each request in a bulk check contains the request that was sent.
 */
public class ReturnedCheckRequest {

    @SerializedName(SpiceDbModelConstants.RESOURCE)
    @Expose
    private Resource resource;
    @SerializedName(SpiceDbModelConstants.SUBJECT)
    @Expose
    private Subject subject;
    @SerializedName(SpiceDbModelConstants.PERMISSION)
    @Expose
    private String permission;
    @SerializedName(SpiceDbModelConstants.CONTEXT)
    @Expose
    private Map<String, Object> context;

    public Resource getResourceItem() {

        return resource;
    }

    public Subject getSubjectItem() {

        return new Subject(subject);
    }

    public String getPermission() {

        return permission;
    }

    public Map<String, Object> getContext() {

        return this.context != null ? Collections.unmodifiableMap(context) : null;
    }
}
