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

import java.util.Map;

/**
 * The {@code LookupResourcesResultBody} class holds all the fields in a single element in a results stream returned
 * from a lookup resources request.
 */
public class LookupResourcesResultBody {

    @SerializedName(SpiceDbModelConstants.LOOKED_AT)
    @Expose
    private ZedToken lookedAt;
    @SerializedName(SpiceDbModelConstants.RESOURCE_OBJECT_ID)
    @Expose
    private String resourceId;
    @SerializedName(SpiceDbModelConstants.PERMISSION_RESULT)
    @Expose
    private String permissionship;
    @SerializedName(SpiceDbModelConstants.PARTIAL_CAVEAT_INFO)
    @Expose
    private Map<String, Object> partialCaveatInfo;
    @SerializedName(SpiceDbModelConstants.AFTER_RESULT_CURSOR)
    @Expose
    private Map<String, Object> afterResultCursor;

    public String lookedAt() {

        return lookedAt.getToken();
    }

    public String getResourceId() {

        return resourceId;
    }

    public String getPermissionship() {

        return permissionship;
    }

    public Map<String, Object> getPartialCaveatInfo() {

        return partialCaveatInfo;
    }

    public Map<String, Object> getAfterResultCursor() {

        return afterResultCursor;
    }
}
