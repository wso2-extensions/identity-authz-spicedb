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
import org.wso2.carbon.identity.authorization.framework.model.AccessEvaluationRequest;
import org.wso2.carbon.identity.authz.spicedb.constants.SpiceDbModelConstants;

import java.util.HashMap;

/**
 * The {@code CheckPermissionRequest} class represents the request body of a permission check request sent to SpiceDB.
 */
@SuppressFBWarnings(value = "URF_UNREAD_FIELD",
        justification = "All fields are accessed via Gson serialization/deserialization")
public class CheckPermissionRequest {

    @SerializedName(SpiceDbModelConstants.RESOURCE)
    @Expose
    private Resource resource;
    @SerializedName(SpiceDbModelConstants.PERMISSION)
    @Expose
    private String permission;
    @SerializedName(SpiceDbModelConstants.SUBJECT)
    @Expose
    private Subject subject;
    @SerializedName(SpiceDbModelConstants.CONTEXT)
    @Expose
    private HashMap<String, Object> context;
    @SerializedName(SpiceDbModelConstants.WITH_TRACING)
    @Expose
    private boolean withTracing;

    public CheckPermissionRequest(AccessEvaluationRequest accessEvaluationRequest) throws IllegalArgumentException {

        if (accessEvaluationRequest.getResource() == null ||
                accessEvaluationRequest.getActionObject() == null ||
                accessEvaluationRequest.getSubject() == null) {
            throw new IllegalArgumentException("Invalid request. Resource, action, and subject " +
                    "must be provided for permission check.");
        }
        if (accessEvaluationRequest.getResource().getResourceId() == null ||
                accessEvaluationRequest.getSubject().getSubjectId() == null) {
            throw new IllegalArgumentException("Invalid request. Resource Id and subject Id " +
                    "must be provided for permission check.");
        }
        this.resource = new Resource(accessEvaluationRequest.getResource().getResourceType(),
                accessEvaluationRequest.getResource().getResourceId());
        this.permission = accessEvaluationRequest.getActionObject().getAction();
        this.subject = new Subject(accessEvaluationRequest.getSubject().getSubjectType(),
                accessEvaluationRequest.getSubject().getSubjectId());
        if (accessEvaluationRequest.getContext() != null) {
            if (accessEvaluationRequest.getContext().containsKey(SpiceDbModelConstants.CONTEXT)) {
                if (!(accessEvaluationRequest.getContext()
                        .get(SpiceDbModelConstants.CONTEXT) instanceof HashMap)) {
                    throw new IllegalArgumentException("Invalid request. Context must be a map.");
                }
                this.context = new HashMap<>();
                this.context.putAll((HashMap<String, Object>) accessEvaluationRequest.getContext()
                        .get(SpiceDbModelConstants.CONTEXT));
            }
            if (accessEvaluationRequest.getContext().containsKey(SpiceDbModelConstants.WITH_TRACING)) {
                this.withTracing = (boolean) accessEvaluationRequest.getContext()
                        .get(SpiceDbModelConstants.WITH_TRACING);
            }
        }
    }

    public void setWithTracing (boolean withTracing) {

        this.withTracing = withTracing;
    }

    public void setContext(HashMap<String, Object> context) {

        this.context = new HashMap<>();
        this.context.putAll(context);
    }

    public boolean isWithTracing() {

        return withTracing;
    }
}
