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

package org.wso2.carbon.identity.authz.spicedb.handler.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.wso2.carbon.identity.authz.spicedb.constants.SpiceDbConstants;
import org.wso2.carbon.identity.authz.spicedb.handler.model.ErrorResponse;
import org.wso2.carbon.identity.authz.spicedb.handler.model.PermissionBulkCheckRequest;
import org.wso2.carbon.identity.authz.spicedb.handler.model.PermissionBulkCheckResponse;
import org.wso2.carbon.identity.authz.spicedb.handler.model.PermissionCheckRequest;
import org.wso2.carbon.identity.authz.spicedb.handler.model.PermissionCheckResponse;
import org.wso2.carbon.identity.authz.spicedb.handler.spicedb.SpiceDbPermissionRequestsHandler;
import org.wso2.carbon.identity.authz.spicedb.handler.util.SpiceDbResponseInterface;
import org.wso2.carbon.identity.oauth2.fga.FGAuthorizationInterface;

import java.util.ArrayList;
import java.util.Objects;


/**
 * This class processes the data received from spiceDB for authorization purposes.
 */
public class SpiceDbAuthorizationHandler implements FGAuthorizationInterface {

    private static final Log LOG = LogFactory.getLog(SpiceDbAuthorizationHandler.class);
    private static final SpiceDbPermissionRequestsHandler SPICEDB_PERMISSION_REQUESTS_HANDLER =
            new SpiceDbPermissionRequestsHandler();

    @Override
    public ArrayList<String> getFGAuthorizedScopes (String userID, ArrayList<String> requiredScopes) {

        ArrayList<String> authorizedScopes = new ArrayList<>();
        if (requiredScopes.size() == 1) {
            String scope = requiredScopes.get(0);
            SpiceDbResponseInterface checkResponse = SPICEDB_PERMISSION_REQUESTS_HANDLER.checkAuthorization(
                    createCheckRequest(userID, scope));
            if (checkResponse instanceof PermissionCheckResponse) {
                PermissionCheckResponse permissionCheckResponse = (PermissionCheckResponse) checkResponse;
                boolean authorized = getAuthorization(permissionCheckResponse);
                if (authorized) {
                    authorizedScopes.add(scope);
                }
            } else if (checkResponse instanceof ErrorResponse) {
                ErrorResponse errorResponse = (ErrorResponse) checkResponse;
                handleErrorResponse(errorResponse);
            }
        } else {
            ArrayList<PermissionCheckRequest> items = new ArrayList<>();
            for (String scope : requiredScopes) {
                items.add(createCheckRequest(userID, scope));
            }
            PermissionBulkCheckRequest bulkCheckRequest = new PermissionBulkCheckRequest(items);
            SpiceDbResponseInterface bulkCheckResponse = SPICEDB_PERMISSION_REQUESTS_HANDLER
                    .bulkCheckAuthorization(bulkCheckRequest);
            if (bulkCheckResponse instanceof PermissionBulkCheckResponse) {
                PermissionBulkCheckResponse permissionBulkCheckResponse =
                        (PermissionBulkCheckResponse) bulkCheckResponse;
                authorizedScopes.addAll(getBulkAuthorization(permissionBulkCheckResponse, requiredScopes));
            } else if (bulkCheckResponse instanceof ErrorResponse) {
                ErrorResponse errorResponse = (ErrorResponse) bulkCheckResponse;
                handleErrorResponse(errorResponse);
            }
        }
        return authorizedScopes;
    }

    public PermissionCheckRequest createCheckRequest (String userId, String scope) {

        String[] data = scope.split("_");
        String permission = data[1];
        String resourceType = data[2];
        String resourceId = data[3];

        return new PermissionCheckRequest(resourceType, resourceId,
                permission, "user", userId, null);
    }

    public boolean getAuthorization (PermissionCheckResponse checkResponse) {

        if (Objects.equals(checkResponse.getPermissionship(), SpiceDbConstants.HAS_PERMISSION)) {
            return true;
        } else if (Objects.equals(checkResponse.getPermissionship(),
                SpiceDbConstants.CONDITIONAL_PERMISSION)) {
            LOG.warn("Could not fully authorize due to lack of information. Missing Required Context: " +
                    checkResponse.getPartialCaveatInfo().get("missingRequiredContext"));
            return false;
        }
        return false;
    }

    public ArrayList<String> getBulkAuthorization (PermissionBulkCheckResponse bulkCheckResponse,
                                                   ArrayList<String> requiredScopes) {

        ArrayList<String> authorizedScopes = new ArrayList<>();
        for (String scope : requiredScopes) {
            if (bulkCheckResponse.getPairs().containsKey(scope.split("_")[3])) {
                JSONObject item = bulkCheckResponse.getPairs().get(scope.split("_")[3]);
                String permissionship = item.getString("permissionship");
                if (permissionship.equals(SpiceDbConstants.HAS_PERMISSION)) {
                    authorizedScopes.add(scope);
                } else if (permissionship.equals(SpiceDbConstants.CONDITIONAL_PERMISSION)) {
                    LOG.warn("Could not fully authorize due to lack of information. Missing Required Context: " +
                            item.get("missingRequiredContext"));
                }
            }

        }
        return authorizedScopes;
    }

    public void handleErrorResponse (ErrorResponse errorResponse) {
        LOG.error("Could not authorize." + errorResponse.getMessage());
        if (LOG.isDebugEnabled()) {
            LOG.error("Could not authorize." + errorResponse.getMessage() + "Further Details: " +
                    errorResponse.getDetails());
        }
    }
}
