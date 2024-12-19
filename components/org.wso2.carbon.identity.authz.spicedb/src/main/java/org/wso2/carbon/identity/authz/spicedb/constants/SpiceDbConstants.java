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

package org.wso2.carbon.identity.authz.spicedb.constants;

/**
 * This class contains all the constant values used throughout this implementation.
 */
public class SpiceDbConstants {

    //General api requirements.
    public static final String BASE_URL = "http://localhost:8443/";
    public static final String PRE_SHARED_KEY = "";

    //Api endpoints.
    public static final String PERMISSION_CHECK = "v1/permissions/check";
    public static final String PERMISSIONS_BULKCHECK = "v1/permissions/checkbulk";
    public static final String PERMISSIONS_EXPAND = "v1/permissions/expand";
    public static final String LOOKUP_RESOURCES = "v1/permissions/resources";
    public static final String LOOKUP_SUBJECTS = "v1/permissions/subjects";

    public static final String RELATIONSHIPS_READ = "v1/relationships/read";
    public static final String RELATIONSHIPS_WRITE = "v1/relationships/write";
    public static final String RELATIONSHIPS_DELETE = "v1/relationships/delete";
    public static final String RELATIONSHIPS_BULKIMPORT = "v1/relationships/importbulk";
    public static final String RELATIONSHIPS_BULKEXPORT = "v1/relationships/exportbulk";

    public static final String SCHEMA_READ = "v1/schema/read";
    public static final String SCHEMA_WRITE = "v1/schema/write";

    public static final String WATCH_SERVICE = "v1/watch";

    //Permission response values.
    public static final String HAS_PERMISSION = "PERMISSIONSHIP_HAS_PERMISSION";
    public static final String NO_PERMISSION = "PERMISSIONSHIP_NO_PERMISSION";
    public static final String CONDITIONAL_PERMISSION = "PERMISSIONSHIP_CONDITIONAL_PERMISSION";

}
