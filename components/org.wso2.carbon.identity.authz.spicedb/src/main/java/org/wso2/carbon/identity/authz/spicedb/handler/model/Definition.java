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
import org.wso2.carbon.identity.authz.spicedb.constants.SpiceDbModelConstants;

import java.util.ArrayList;

/**
 * The {@code Definition} class represents a definition object returned in a reflection response. This definition
 * refers to a specific definition that is used to define the structure of an entity in SpiceDB.
 */
public class Definition {

    @SerializedName(SpiceDbModelConstants.DEFINITION_NAME)
    @Expose
    private String definitionName;
    @SerializedName(SpiceDbModelConstants.DEFINITION_COMMENT)
    @Expose
    private String definitionComment;
    @SerializedName(SpiceDbModelConstants.DEFINITION_RELATIONS)
    @Expose
    private ArrayList<Object> relations;
    @SerializedName(SpiceDbModelConstants.DEFINITION_PERMISSIONS)
    @Expose
    private ArrayList<Permission> permissions;
    private ArrayList<String> permissionNames;

    public String getDefinitionName() {

        return definitionName;
    }

    public String getDefinitionComment() {

        return definitionComment;
    }

    public ArrayList<Object> getRelations() {

        return relations;
    }

    public ArrayList<Permission> getPermissions() {

        return permissions;
    }

    public ArrayList<String> getPermissionNames() {

        permissionNames = new ArrayList<>();
        if (this.permissions != null) {
            for (Permission permission : this.permissions) {
                permissionNames.add(permission.getPermissionName());
            }
        }
        return permissionNames;
    }
}
