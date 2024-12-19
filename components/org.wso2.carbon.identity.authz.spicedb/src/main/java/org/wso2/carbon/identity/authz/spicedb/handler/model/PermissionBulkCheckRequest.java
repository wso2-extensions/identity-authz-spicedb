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

import java.util.ArrayList;

/**
 * This class is a model class to create Permission bulk check requests to SpiceDB.
 */
public class PermissionBulkCheckRequest implements SpiceDbRequestInterface {

    private ArrayList<PermissionCheckRequest> items;

    public PermissionBulkCheckRequest (ArrayList<PermissionCheckRequest> items) {

        this.items = items;
    }
    @Override
    public JSONObject parseToJSON() {

        JSONObject jsonObject = new JSONObject();
        JSONObject[] itemList = new JSONObject[items.size()];
        for (int i = 0; i < items.size(); i++) {
            itemList[i] = items.get(i).parseToJSON();
        }
        jsonObject.put("items", itemList);
        return jsonObject;
    }
}
