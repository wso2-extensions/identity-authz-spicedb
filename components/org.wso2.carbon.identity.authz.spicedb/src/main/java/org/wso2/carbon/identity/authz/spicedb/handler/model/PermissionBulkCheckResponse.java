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

import org.json.JSONArray;
import org.json.JSONObject;
import org.wso2.carbon.identity.authz.spicedb.handler.util.SpiceDbResponseInterface;

import java.util.HashMap;

/**
 * This class is a model class to receive permission bulk check responses from SpiceDB.
 */
public class PermissionBulkCheckResponse implements SpiceDbResponseInterface {

    private String token;
    private HashMap<String, JSONObject> pairs;

    private static final String CHECKED_AT_TOKEN = "checkedAt";
    private static final String RESULTS = "pairs";
    private static final String RESOURCE = "resource";
    private static final String RESULT_ITEM = "item";
    private static final String RESOURCE_ID = "objectId";

    public PermissionBulkCheckResponse (JSONObject object) {

        if (object.has(CHECKED_AT_TOKEN)) {
            this.token = object.getString(CHECKED_AT_TOKEN);
            this.pairs = new HashMap<>();
            JSONArray pairsArray = object.getJSONArray(RESULTS);
            for (int i = 0; i < pairsArray.length(); i++) {
                JSONObject resource = pairsArray.getJSONObject(i).getJSONObject(RESOURCE);
                JSONObject item = pairsArray.getJSONObject(i).getJSONObject(RESULT_ITEM);
                pairs.put(resource.getString(RESOURCE_ID), item);
            }
        }
    }

    public String getToken() {

        return token;
    }

    public HashMap<String, JSONObject> getPairs() {

        return pairs;
    }
}
