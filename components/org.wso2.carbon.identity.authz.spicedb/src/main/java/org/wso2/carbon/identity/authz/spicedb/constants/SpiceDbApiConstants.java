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

package org.wso2.carbon.identity.authz.spicedb.constants;

import org.wso2.carbon.identity.core.util.IdentityUtil;

/**
 * The {@code SpiceDbApiConstants} class contains the constants used in the SpiceDB authorization engine.
 */
public class SpiceDbApiConstants {

    //Authorization engine name.
    public static final String AUTHORIZATION_ENGINE_NAME = "SpiceDB";

    //General api requirements.
    public static final String BASE_URL = IdentityUtil.getProperty("FGAEngineConfig.BasePath");
     //http request header values
    public static final String CONTENT_TYPE = "application/json";
    public static final String PRE_SHARED_KEY = IdentityUtil.getProperty("FGAEngineConfig.Authentication.PreSharedKey");

    //Api endpoints.
    public static final String PERMISSION_CHECK = "v1/permissions/check";
    public static final String PERMISSIONS_BULKCHECK = "v1/permissions/checkbulk";
    public static final String LOOKUP_RESOURCES = "v1/permissions/resources";
    public static final String LOOKUP_SUBJECTS = "v1/permissions/subjects";
    public static final String RELATIONSHIPS_READ = "v1/relationships/read";
     //Experimental api endpoints.
    public static final String REFLECT_SCHEMA = "v1/experimental/reflectschema";
}
