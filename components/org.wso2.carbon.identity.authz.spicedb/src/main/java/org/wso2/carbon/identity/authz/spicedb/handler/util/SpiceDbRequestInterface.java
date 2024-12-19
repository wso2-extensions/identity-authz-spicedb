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

package org.wso2.carbon.identity.authz.spicedb.handler.util;

import org.json.JSONObject;

/**
 * This interface acts as the generic request model for spiceDB.
 * All the request models implement this interface.
 */
public interface SpiceDbRequestInterface {

    /**
     * Parse the entity into a JSON object.
     *
     * @return A JSON object with the directory object or relation attributes parsed as a JSON.
     */
    JSONObject parseToJSON ();
}
