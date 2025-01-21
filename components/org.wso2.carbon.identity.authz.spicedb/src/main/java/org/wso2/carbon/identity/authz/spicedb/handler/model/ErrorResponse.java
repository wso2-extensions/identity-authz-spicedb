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

import org.json.JSONArray;
import org.json.JSONObject;
import org.wso2.carbon.identity.authz.spicedb.handler.util.SpiceDbResponse;

/**
 * This class is a model class to receive Error responses from SpiceDB.
 */
public class ErrorResponse implements SpiceDbResponse {

    private int code;
    private String message;
    private JSONArray details;

    private static final String CODE = "code";
    private static final String MESSAGE = "message";
    private static final String DETAILS = "details";

    public ErrorResponse(JSONObject response) {

        this.code = response.getInt(CODE);
        this.message = response.getString(MESSAGE);
        if (response.has(DETAILS)) {
            this.details = response.getJSONArray(DETAILS);
        }
    }

    public String getMessage() {

        return message;
    }

    public JSONArray getDetails() {

        return details;
    }

    public int getCode() {

        return code;
    }
}
