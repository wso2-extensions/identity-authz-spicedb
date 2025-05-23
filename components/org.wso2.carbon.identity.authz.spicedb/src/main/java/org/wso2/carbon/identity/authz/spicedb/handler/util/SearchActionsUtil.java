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

package org.wso2.carbon.identity.authz.spicedb.handler.util;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.wso2.carbon.identity.authorization.framework.model.AccessEvaluationRequest;
import org.wso2.carbon.identity.authorization.framework.model.AccessEvaluationResponse;
import org.wso2.carbon.identity.authorization.framework.model.AuthorizationAction;
import org.wso2.carbon.identity.authorization.framework.model.AuthorizationResource;
import org.wso2.carbon.identity.authorization.framework.model.AuthorizationSubject;
import org.wso2.carbon.identity.authorization.framework.model.BulkAccessEvaluationRequest;
import org.wso2.carbon.identity.authorization.framework.model.BulkAccessEvaluationResponse;
import org.wso2.carbon.identity.authorization.framework.model.SearchActionsResponse;
import org.wso2.carbon.identity.authz.spicedb.constants.SpiceDbApiConstants;
import org.wso2.carbon.identity.authz.spicedb.handler.exception.SpicedbEvaluationException;
import org.wso2.carbon.identity.authz.spicedb.handler.model.Definition;
import org.wso2.carbon.identity.authz.spicedb.handler.model.OptionalSchemaFilter;
import org.wso2.carbon.identity.authz.spicedb.handler.model.ReflectSchemaRequest;
import org.wso2.carbon.identity.authz.spicedb.handler.model.ReflectSchemaResponse;
import org.wso2.carbon.identity.authz.spicedb.handler.model.SpiceDbErrorResponse;
import org.wso2.carbon.identity.authz.spicedb.handler.spicedb.SpicedbPermissionRequestService;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * The {@code SearchActionsUtil} class provides utility methods for handling search actions requests since spiceDB does
 * not support search actions. This class uses Schema Reflection endpoint and Bulk Check endpoint to retrieve
 * the action details from SpiceDB.
 */
public class SearchActionsUtil {

    /**
     * This method retrieves all the permissions any subject can perform on the resource using the Reflect Schema
     * endpoint in SpiceDB.
     *
     * @param resourceType The type of the resource.
     * @return the list of permissions that the resource type has.
     * @throws SpicedbEvaluationException If an error occurs while retrieving the actions.
     */
    public static ArrayList<String> getAllPermissions(String resourceType)
            throws SpicedbEvaluationException {

        ReflectSchemaRequest reflectSchemaRequest =
                createReflectSchemaRequest(resourceType);
        try (CloseableHttpResponse response = HttpHandler.sendPOSTRequest(SpiceDbApiConstants.REFLECT_SCHEMA,
                JsonUtil.parseToJsonString(reflectSchemaRequest))) {
            String responseString = HttpHandler.parseResponseToString(response);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                ReflectSchemaResponse reflectSchemaResponse = JsonUtil.jsonToResponseModel(
                        responseString, ReflectSchemaResponse.class);
                ArrayList<String> actions = new ArrayList<>();
                for (Definition definition : reflectSchemaResponse.getDefinitions()) {
                    actions.addAll(definition.getPermissionNames());
                }
                return actions;
            } else if (HttpStatus.SC_BAD_REQUEST <= statusCode &&
                    statusCode <= HttpStatus.SC_INSUFFICIENT_STORAGE) {
                SpiceDbErrorResponse error = JsonUtil.jsonToResponseModel(responseString, SpiceDbErrorResponse.class);
                throw new SpicedbEvaluationException(error.getCode(), error.getMessage());
            } else {
                throw new SpicedbEvaluationException("Searching actions from spiceDB failed while reflecting the " +
                        "schema. Cannot identify error code.");
            }
        } catch (IOException e) {
            throw new SpicedbEvaluationException("Could not connect to SpiceDB to reflect schema.",
                    e.getMessage());
        } catch (URISyntaxException ue) {
            throw new SpicedbEvaluationException("URI error occurred while creating the request URL. Could not " +
                    "connect to SpiceDB to reflect schema.", ue.getMessage());
        }
    }

    /**
     * This method creates a Reflect Schema request object from resource type to get all the permissions that
     * resource type has.
     *
     * @param resourceType The type of the resource.
     * @return A {@link ReflectSchemaRequest} object.
     */
    private static ReflectSchemaRequest createReflectSchemaRequest(String resourceType) {

        ArrayList<OptionalSchemaFilter> optionalSchemaFilters = new ArrayList<>();
        OptionalSchemaFilter optionalSchemaFilter = new OptionalSchemaFilter();
        optionalSchemaFilter.setOptionalDefinitionNameFilter(resourceType);
        optionalSchemaFilters.add(optionalSchemaFilter);
        return new ReflectSchemaRequest(optionalSchemaFilters);
    }

    /**
     * This method retrieves all the permissions that a subject can perform on a resource using the Bulk Check endpoint
     * in SpiceDB and returns a {@code SearchActionsResponse} object.
     *
     * @param allPermissions The list of all permissions that the resource type has.
     * @param subject        The subject to check.
     * @param resource       The resource to check.
     * @return A {@link SearchActionsResponse} object containing the actions that the subject can perform on the
     * resource.
     * @throws SpicedbEvaluationException If an error occurs while retrieving the actions.
     */
    public static SearchActionsResponse getAuthorizedActions(ArrayList<String> allPermissions,
                                                             AuthorizationSubject subject,
                                                             AuthorizationResource resource)
            throws SpicedbEvaluationException {

        ArrayList<AccessEvaluationRequest> requests = new ArrayList<>();
        for (String permission: allPermissions) {
            requests.add(createAccessEvaluationRequest(subject, permission, resource));
        }
        BulkAccessEvaluationRequest bulkAccessEvaluationRequest = new BulkAccessEvaluationRequest(requests);
        SpicedbPermissionRequestService requestService = new SpicedbPermissionRequestService();
        BulkAccessEvaluationResponse bulkAccessEvaluationResponse = requestService
                .bulkEvaluate(bulkAccessEvaluationRequest);
        return new SearchActionsResponse(createAuthorizationActions(bulkAccessEvaluationResponse.getResults(),
                allPermissions));
    }


    /**
     * This method converts a list of action strings to a list of {@link AuthorizationAction} objects to send back in
     * the response.
     *
     *
     * @return A list of {@link AuthorizationAction} objects.
     */
    private static ArrayList<AuthorizationAction> createAuthorizationActions(
            List<AccessEvaluationResponse> responses, ArrayList<String> allPermissions) {

        ArrayList<AuthorizationAction> authorizationActions = new ArrayList<>();
        for (int i = 0; i < responses.size(); i++) {
            if (responses.get(i).getDecision()) {
                AuthorizationAction action = new AuthorizationAction(allPermissions.get(i));
                authorizationActions.add(action);
            }
        }
        return authorizationActions;
    }

    /**
     * This method creates an {@link AccessEvaluationRequest} object from the subject, permission and resource
     * to send in bulk check request.
     *
     * @param subject the subject to check.
     * @param permission the permission to check.
     * @param resource the resource to check.
     * @return An {@link AccessEvaluationRequest} object.
     */
    private static AccessEvaluationRequest createAccessEvaluationRequest(AuthorizationSubject subject,
                                                                         String permission,
                                                                         AuthorizationResource resource) {

        AuthorizationAction action = new AuthorizationAction(permission);
        return new AccessEvaluationRequest(subject, action, resource);
    }

}
