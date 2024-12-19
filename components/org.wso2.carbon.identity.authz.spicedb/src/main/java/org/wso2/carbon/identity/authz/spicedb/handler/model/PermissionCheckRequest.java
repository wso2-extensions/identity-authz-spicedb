/*
 * Copyright (c) 2024, WSO2 LLC. (http://www.wso2.com).
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

import org.json.JSONObject;
import org.wso2.carbon.identity.authz.spicedb.handler.util.SpiceDbRequestInterface;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * This class is a model class to create permission requests to SpiceDB.
 */
public class PermissionCheckRequest implements SpiceDbRequestInterface {

    private String resourceObjectType;
    private String resourceObjectId;
    private String permission;
    private String subjectObjectType;
    private String subjectObjectId;
    private String optionalRelation;
    private HashMap<String, Object> context;
    private boolean withTracing;

    public PermissionCheckRequest (String resourceObjectType, String resourceObjectId, String permission,
                                  String subjectObjectType, String subjectObjectId, HashMap<String, Object> context) {

        this.resourceObjectType = resourceObjectType;
        this.resourceObjectId = resourceObjectId;
        this.permission = permission;
        this.subjectObjectType = subjectObjectType;
        this.subjectObjectId = subjectObjectId;
        this.context = context;
    }

    public void setOptionalRelation (String optionalRelation) {

        this.optionalRelation = optionalRelation;
    }

    public void setWithTracing (boolean withTracing) {

        this.withTracing = withTracing;
    }

    @Override
    public JSONObject parseToJSON() {

        JSONObject jsonObject = new JSONObject();
        HashMap<String, Object> resource = new LinkedHashMap<>();
        resource.put("objectType", this.resourceObjectType);
        resource.put("objectId", this.resourceObjectId);
        jsonObject.put("resource", resource);

        jsonObject.put("permission", this.permission);

        HashMap<String, Object> subject = new HashMap<>();
        HashMap<String, Object> subjectObject = new HashMap<>();
        subjectObject.put("objectType", this.subjectObjectType);
        subjectObject.put("objectId", this.subjectObjectId);
        subject.put("object", subjectObject);
        subject.put("optionalRelation", optionalRelation);
        jsonObject.put("subject", subject);

        jsonObject.put("context", this.context);
        jsonObject.put("withTracing", withTracing);
        return jsonObject;
    }

}
