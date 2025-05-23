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
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.wso2.carbon.identity.authorization.framework.model.SearchResourcesRequest;
import org.wso2.carbon.identity.authz.spicedb.constants.SpiceDbModelConstants;

import java.util.Map;

/**
 * The {@code LookupResourcesRequest} class represents the request body of a lookup resources request sent to SpiceDB.
 */
@SuppressFBWarnings(value = "URF_UNREAD_FIELD",
        justification = "All fields are accessed via Gson serialization")
public class LookupResourcesRequest {

    @SerializedName(SpiceDbModelConstants.RESOURCE_OBJECT_TYPE)
    @Expose
    private String resourceType;
    @SerializedName(SpiceDbModelConstants.PERMISSION)
    @Expose
    private String permission;
    @SerializedName(SpiceDbModelConstants.SUBJECT)
    @Expose
    private Subject subject;
    @SerializedName(SpiceDbModelConstants.OPTIONAL_LIMIT)
    @Expose
    private long optionalLimit;
    @SerializedName(SpiceDbModelConstants.OPTIONAL_CURSOR)
    @Expose
    private String optionalCursor;
    @SerializedName(SpiceDbModelConstants.CONTEXT)
    @Expose
    private Map<String, Object> context;

    public LookupResourcesRequest(SearchResourcesRequest searchResourcesRequest) {

        if (searchResourcesRequest.getResource() == null ||
                searchResourcesRequest.getAction() == null ||
                searchResourcesRequest.getSubject() == null) {
            throw new IllegalArgumentException("Invalid request. Resource with type, action, and subject " +
                    "must be provided to lookup resources.");
        }
        if (searchResourcesRequest.getSubject().getSubjectId() == null) {
            throw new IllegalArgumentException("Invalid request. Subject id must be provided to lookup resources.");
        }
        this.resourceType = searchResourcesRequest.getResource().getResourceType();
        this.permission = searchResourcesRequest.getAction().getAction();
        this.subject = new Subject(searchResourcesRequest.getSubject().getSubjectType(),
                searchResourcesRequest.getSubject().getSubjectId());
    }

    public void setContext(Map<String, Object> context) {

        this.context = context;
    }

    public void setOptionalRelation (String optionalRelation) {

        this.subject.setOptionalRelation(optionalRelation);
    }

    public void setOptionalLimit (Long optionalLimit) {

        this.optionalLimit = optionalLimit;
    }

    public void setOptionalCursor (String optionalCursor) {

        this.optionalCursor = optionalCursor;
    }

    public Map<String, Object> getContext() {

        return context;
    }
}
