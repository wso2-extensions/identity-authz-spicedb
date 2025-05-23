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
import org.wso2.carbon.identity.authz.spicedb.constants.SpiceDbModelConstants;

/**
 * The {@code OptionalSchemaFilter} class represents an optional filter object in a reflection request.
 */
@SuppressFBWarnings(value = "URF_UNREAD_FIELD",
        justification = "All fields are accessed via Gson serialization")
public class OptionalSchemaFilter {

    @SerializedName(SpiceDbModelConstants.OPTIONAL_DEFINITION_NAME_FILTER)
    @Expose
    private String optionalDefinitionNameFilter;
    @SerializedName(SpiceDbModelConstants.OPTIONAL_RELATION_NAME_FILTER)
    @Expose
    private String optionalRelationNameFilter;
    @SerializedName(SpiceDbModelConstants.OPTIONAL_CAVEAT_NAME_FILTER)
    @Expose
    private String optionalCaveatNameFilter;
    @SerializedName(SpiceDbModelConstants.OPTIONAL_PERMISSION_NAME_FILTER)
    @Expose
    private String optionalPermissionNameFilter;

    public void setOptionalDefinitionNameFilter(String optionalDefinitionNameFilter) {

        this.optionalDefinitionNameFilter = optionalDefinitionNameFilter;
    }

    public void setOptionalRelationNameFilter(String optionalRelationNameFilter) {

        this.optionalRelationNameFilter = optionalRelationNameFilter;
    }

    public void setOptionalCaveatNameFilter(String optionalCaveatNameFilter) {

        this.optionalCaveatNameFilter = optionalCaveatNameFilter;
    }

    public void setOptionalPermissionNameFilter(String optionalPermissionNameFilter) {

        this.optionalPermissionNameFilter = optionalPermissionNameFilter;
    }
}
