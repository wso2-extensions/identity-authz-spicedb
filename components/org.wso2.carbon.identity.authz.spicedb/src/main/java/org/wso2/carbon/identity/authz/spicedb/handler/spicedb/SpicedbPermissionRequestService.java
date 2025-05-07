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

package org.wso2.carbon.identity.authz.spicedb.handler.spicedb;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.wso2.carbon.identity.authorization.framework.model.AccessEvaluationRequest;
import org.wso2.carbon.identity.authorization.framework.model.AccessEvaluationResponse;
import org.wso2.carbon.identity.authorization.framework.model.BulkAccessEvaluationRequest;
import org.wso2.carbon.identity.authorization.framework.model.BulkAccessEvaluationResponse;
import org.wso2.carbon.identity.authorization.framework.model.SearchActionsRequest;
import org.wso2.carbon.identity.authorization.framework.model.SearchActionsResponse;
import org.wso2.carbon.identity.authorization.framework.model.SearchResourcesRequest;
import org.wso2.carbon.identity.authorization.framework.model.SearchResourcesResponse;
import org.wso2.carbon.identity.authorization.framework.model.SearchSubjectsRequest;
import org.wso2.carbon.identity.authorization.framework.model.SearchSubjectsResponse;
import org.wso2.carbon.identity.authorization.framework.service.AccessEvaluationService;
import org.wso2.carbon.identity.authz.spicedb.constants.SpiceDbApiConstants;
import org.wso2.carbon.identity.authz.spicedb.handler.exception.SpicedbEvaluationException;
import org.wso2.carbon.identity.authz.spicedb.handler.model.BulkCheckPermissionRequest;
import org.wso2.carbon.identity.authz.spicedb.handler.model.BulkCheckPermissionResponse;
import org.wso2.carbon.identity.authz.spicedb.handler.model.CheckPermissionRequest;
import org.wso2.carbon.identity.authz.spicedb.handler.model.CheckPermissionResponse;
import org.wso2.carbon.identity.authz.spicedb.handler.model.LookupObjectsResponseHolder;
import org.wso2.carbon.identity.authz.spicedb.handler.model.LookupResourcesRequest;
import org.wso2.carbon.identity.authz.spicedb.handler.model.LookupSubjectsRequest;
import org.wso2.carbon.identity.authz.spicedb.handler.model.SpiceDbErrorResponse;
import org.wso2.carbon.identity.authz.spicedb.handler.util.HttpHandler;
import org.wso2.carbon.identity.authz.spicedb.handler.util.JsonUtil;
import org.wso2.carbon.identity.authz.spicedb.handler.util.SearchActionsUtil;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

/**
 * The {@code SpicedbPermissionRequestService} handles sending all evaluation related requests to SpiceDB
 * authorization engine. <a href="https://www.postman.com/authzed/workspace/spicedb/overview">More info</a>.
 * <p>
 *     This class implements the {@link AccessEvaluationService} and provides the implementation for access evaluation
 *     using SpiceDB. This class uses the {@link HttpHandler} to send HTTP requests to the SpiceDB authorization
 *     engine and parse the responses to generic response models.
 * </p>
 */
public class SpicedbPermissionRequestService implements AccessEvaluationService {

    /**
     * This method returns the name of the authorization engine.
     *
     * @return The name of the authorization engine.
     */
    @Override
    public String getEngine() {

        return SpiceDbApiConstants.AUTHORIZATION_ENGINE_NAME;
    }

    /**
     * This method checks if the requested subject is authorized to perform the requested action on the resource.
     *
     * @param accessEvaluationRequest The request object containing the permission check details.
     * @return {@link AccessEvaluationResponse} - The response object containing the authorization check result.
     * @throws SpicedbEvaluationException If an error occurs while sending the request or parsing the response.
     */
    @Override
    public AccessEvaluationResponse evaluate(AccessEvaluationRequest accessEvaluationRequest)
            throws SpicedbEvaluationException {

        if (accessEvaluationRequest == null) {
            throw new SpicedbEvaluationException("Invalid request. Access evaluation request cannot be null.");
        }
        CheckPermissionRequest checkPermissionRequest = new CheckPermissionRequest(accessEvaluationRequest);
        try (CloseableHttpResponse response = HttpHandler.sendPOSTRequest(
                SpiceDbApiConstants.PERMISSION_CHECK,
                JsonUtil.parseToJsonString(checkPermissionRequest))) {
            int statusCode = response.getStatusLine().getStatusCode();
            String responseString = HttpHandler.parseResponseToString(response);
            if (statusCode == HttpStatus.SC_OK) {
                CheckPermissionResponse checkPermissionResponse = JsonUtil.jsonToResponseModel(
                        responseString, CheckPermissionResponse.class);
                AccessEvaluationResponse accessEvaluationResponse = new AccessEvaluationResponse(
                        checkPermissionResponse.isAuthorized());
                accessEvaluationResponse.setContext(checkPermissionResponse.getPartialCaveatInfo());
                return accessEvaluationResponse;
            } else if (HttpStatus.SC_BAD_REQUEST <= statusCode &&
                    statusCode <= HttpStatus.SC_INSUFFICIENT_STORAGE) {
                SpiceDbErrorResponse error = JsonUtil.jsonToResponseModel(
                        responseString, SpiceDbErrorResponse.class);
                throw new SpicedbEvaluationException(error.getCode(), error.getMessage());
            } else {
                throw new SpicedbEvaluationException("Authorization check from spiceDB failed. " +
                        "Cannot identify error code.");
            }
        } catch (IOException e) {
            throw new SpicedbEvaluationException("Could not connect to SpiceDB to check authorization.",
                    e.getMessage());
        } catch (URISyntaxException ue) {
            throw new SpicedbEvaluationException("URI error occurred while creating the request URL. Could not " +
                    "connect to SpiceDB for check authorization", ue.getMessage());
        }
    }

    /**
     * This method performs a bulk check permission request which allows checking multiple permissions in a single
     * request.
     *
     * @param bulkAccessEvaluationRequest The request object containing the bulk permission check details.
     * @return {@link BulkAccessEvaluationResponse} - The response object containing the authorization bulk check
     * result.
     * @throws SpicedbEvaluationException If an error occurs while sending the request or parsing the response.
     */
    @Override
    public BulkAccessEvaluationResponse bulkEvaluate
    (BulkAccessEvaluationRequest bulkAccessEvaluationRequest) throws SpicedbEvaluationException {

        if (bulkAccessEvaluationRequest == null) {
            throw new SpicedbEvaluationException("Invalid request. Bulk access evaluation request cannot be null.");
        }
        BulkCheckPermissionRequest bulkCheckPermissionRequest =
                new BulkCheckPermissionRequest(bulkAccessEvaluationRequest.getRequestItems());
        try (CloseableHttpResponse response = HttpHandler.sendPOSTRequest(
                SpiceDbApiConstants.PERMISSIONS_BULKCHECK,
                JsonUtil.parseToJsonString(bulkCheckPermissionRequest))) {
            int statusCode = response.getStatusLine().getStatusCode();
            String responseString = HttpHandler.parseResponseToString(response);
            if (statusCode == HttpStatus.SC_OK) {
                BulkCheckPermissionResponse bulkCheckPermissionResponse = JsonUtil.jsonToResponseModel(
                        responseString, BulkCheckPermissionResponse.class);
                return bulkCheckPermissionResponse.toBulkAccessEvalResponse();
            } else if (HttpStatus.SC_BAD_REQUEST <= statusCode &&
                    statusCode <= HttpStatus.SC_INSUFFICIENT_STORAGE) {
                SpiceDbErrorResponse error = JsonUtil.jsonToResponseModel(
                        responseString, SpiceDbErrorResponse.class);
                throw new SpicedbEvaluationException(error.getCode(), error.getMessage());
            } else {
                throw new SpicedbEvaluationException("Authorization bulk check from spiceDB failed. " +
                        "Cannot identify error code.");
            }
        } catch (IOException e) {
            throw new SpicedbEvaluationException("Could not connect to SpiceDB to bulk check authorization.",
                    e.getMessage());
        } catch (URISyntaxException ue) {
            throw new SpicedbEvaluationException("URI error occurred while creating the request URL. Could not " +
                    "connect to SpiceDB for bulk check authorization", ue.getMessage());
        }
    }

    /**
     * This method returns the resources that the requested subject can perform the requested action on. This is done
     * by sending a Lookup resource request to SpiceDB.
     *
     * @param searchResourcesRequest The request object containing the resource details to look up.
     * @return {@link SearchResourcesResponse} - The response object containing the list of resources.
     * @throws SpicedbEvaluationException If an error occurs while sending the request or parsing the response.
     */
    @Override
    public SearchResourcesResponse searchResources(SearchResourcesRequest searchResourcesRequest)
            throws SpicedbEvaluationException {

        if (searchResourcesRequest == null) {
            throw new SpicedbEvaluationException("Invalid request. Search resources request cannot be null.");
        }
        LookupResourcesRequest lookupResourcesRequest = new LookupResourcesRequest(searchResourcesRequest);
        try (CloseableHttpResponse response = HttpHandler.sendPOSTRequest(SpiceDbApiConstants.LOOKUP_RESOURCES,
                JsonUtil.parseToJsonString(lookupResourcesRequest))) {
            String responseString = HttpHandler.parseResponseToString(response);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                LookupObjectsResponseHolder lookupObjectsResponseHolder = new LookupObjectsResponseHolder(
                        responseString,
                        searchResourcesRequest.getResource().getResourceType());
                return lookupObjectsResponseHolder.toSearchResourcesResponse();
            } else if (HttpStatus.SC_BAD_REQUEST <= statusCode &&
                    statusCode <= HttpStatus.SC_INSUFFICIENT_STORAGE) {
                SpiceDbErrorResponse error = JsonUtil.jsonToResponseModel(responseString, SpiceDbErrorResponse.class);
                throw new SpicedbEvaluationException(error.getCode(), error.getMessage());
            } else {
                throw new SpicedbEvaluationException("Looking up resources from spiceDB failed. " +
                        "Cannot identify error code.");
            }
        } catch (IOException e) {
            throw new SpicedbEvaluationException("Could not connect to SpiceDB to Lookup Resources.",
                    e.getMessage());
        } catch (URISyntaxException ue) {
            throw new SpicedbEvaluationException("URI error occurred while creating the request URL. Could not " +
                    "connect to SpiceDB to look uo resources.", ue.getMessage());
        }
    }

    /**
     * This method returns the subjects that can perform the requested action on the requested resource. This is done
     * by sending a Lookup subject request to SpiceDB.
     *
     * @param searchSubjectsRequest The request object containing the subject details to look up.
     * @return {@link SearchSubjectsResponse} - The response object containing the list of subjects.
     * @throws SpicedbEvaluationException If an error occurs while sending the request or parsing the response.
     */
    @Override
    public SearchSubjectsResponse searchSubjects(SearchSubjectsRequest searchSubjectsRequest)
            throws SpicedbEvaluationException {

        if (searchSubjectsRequest == null) {
            throw new SpicedbEvaluationException("Invalid request. Search subjects request cannot be null.");
        }
        LookupSubjectsRequest lookupSubjectsRequest = new LookupSubjectsRequest(searchSubjectsRequest);
        try (CloseableHttpResponse response = HttpHandler.sendPOSTRequest(SpiceDbApiConstants.LOOKUP_SUBJECTS,
                JsonUtil.parseToJsonString(lookupSubjectsRequest))) {
            String responseString = HttpHandler.parseResponseToString(response);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                LookupObjectsResponseHolder lookupObjectsResponseHolder = new LookupObjectsResponseHolder(
                        responseString,
                        searchSubjectsRequest.getSubject().getSubjectType());
                return lookupObjectsResponseHolder.toSearchSubjectsResponse();
            } else if (HttpStatus.SC_BAD_REQUEST <= statusCode &&
                    statusCode <= HttpStatus.SC_INSUFFICIENT_STORAGE) {
                SpiceDbErrorResponse error = JsonUtil.jsonToResponseModel(responseString, SpiceDbErrorResponse.class);
                throw new SpicedbEvaluationException(error.getCode(), error.getMessage());
            } else {
                throw new SpicedbEvaluationException("Looking up subjects from spiceDB failed. " +
                        "Cannot identify error code.");
            }
        } catch (IOException e) {
            throw new SpicedbEvaluationException("Could not connect to SpiceDB to lookup subjects.",
                    e.getMessage());
        } catch (URISyntaxException ue) {
            throw new SpicedbEvaluationException("URI error occurred while creating the request URL. Could not " +
                    "connect to SpiceDB to look up subjects.", ue.getMessage());
        }
    }

    /**
     * This method returns the actions that can be performed on a resource by a subject. Since SpiceDB does not have
     * a direct search Actions API yet(as of 2025 April), this method uses Schema Reflection and bulk check
     * APIs to retrieve the actions.
     *
     * @param searchActionsRequest The request object containing the subject and resource details to search actions
     *                             for.
     * @return {@link SearchActionsResponse} containing the actions.
     * @throws SpicedbEvaluationException If an error occurs while sending the request or parsing the response.
     * @see AccessEvaluationService#searchActions
     */
    @Override
    public SearchActionsResponse searchActions(SearchActionsRequest searchActionsRequest)
            throws SpicedbEvaluationException {

        if (searchActionsRequest == null) {
            throw new SpicedbEvaluationException("Invalid request. Search actions request cannot be null.");
        }
        ArrayList<String> allPermissions = SearchActionsUtil.getAllPermissions(searchActionsRequest
                .getResource().getResourceType());

        return SearchActionsUtil.getAuthorizedActions(allPermissions, searchActionsRequest.getSubject(),
                searchActionsRequest.getResource());
    }
}
