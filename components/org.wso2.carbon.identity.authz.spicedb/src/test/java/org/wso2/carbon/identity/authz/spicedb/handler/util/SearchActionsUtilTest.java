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

import com.google.gson.Gson;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.mockito.MockedStatic;
import org.testng.annotations.Test;
import org.wso2.carbon.identity.authorization.framework.model.AuthorizationResource;
import org.wso2.carbon.identity.authorization.framework.model.AuthorizationSubject;
import org.wso2.carbon.identity.authorization.framework.model.SearchActionsResponse;
import org.wso2.carbon.identity.authz.spicedb.constants.SpiceDbApiConstants;
import org.wso2.carbon.identity.authz.spicedb.handler.exception.SpicedbEvaluationException;
import org.wso2.carbon.identity.authz.spicedb.handler.model.BulkCheckPermissionRequest;
import org.wso2.carbon.identity.authz.spicedb.handler.model.BulkCheckPermissionResponse;
import org.wso2.carbon.identity.authz.spicedb.handler.model.BulkCheckPermissionResult;
import org.wso2.carbon.identity.authz.spicedb.handler.model.ReflectSchemaRequest;
import org.wso2.carbon.identity.authz.spicedb.handler.model.ReflectSchemaResponse;
import org.wso2.carbon.identity.authz.spicedb.handler.model.SpiceDbErrorResponse;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Unit test class for {@link SearchActionsUtil}.
 */
@Test
public class SearchActionsUtilTest {

    private static final String TEST_RESOURCE_TYPE = "document";
    private static final String REFLECT_SCHEMA_ENDPOINT = SpiceDbApiConstants.REFLECT_SCHEMA;
    private static final String BULK_EVALUATION_ENDPOINT = SpiceDbApiConstants.PERMISSIONS_BULKCHECK;

    @Test
    public void testGetAllPermissionsForSuccessFullReturn() throws Exception {

        try (MockedStatic<HttpHandler> httpHandlerMock = mockStatic(HttpHandler.class);
             MockedStatic<JsonUtil> jsonUtilMock = mockStatic(JsonUtil.class)) {

            jsonUtilMock.when(() -> JsonUtil.parseToJsonString(any(ReflectSchemaRequest.class)))
                    .thenReturn("mockRequestBody");

            CloseableHttpResponse mockedResponse = mock(CloseableHttpResponse.class);
            StatusLine statusLine = mock(StatusLine.class);
            when(statusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK);
            when(mockedResponse.getStatusLine()).thenReturn(statusLine);

            httpHandlerMock.when(() -> HttpHandler.sendPOSTRequest(
                            eq(REFLECT_SCHEMA_ENDPOINT),
                            anyString()))
                    .thenReturn(mockedResponse);

            String responseJson =
                    "{\"definitions\": [{\"name\": \"definition\",\"comment\": \"\",\"relations\": [{\"name\": " +
                            "\"relation\",\"comment\": \"\",\"parentDefinitionName\": \"definition\"," +
                            "\"subjectTypes\": [{\"subjectDefinitionName\": \"definition\",\"optionalCaveatName\": " +
                            "\"\",\"isTerminalSubject\": false}]}],\"permissions\": [{\"name\": \"read\",\"comment\":" +
                            " \"\",\"parentDefinitionName\":\"definition\"}]}]}";
            httpHandlerMock.when(() -> HttpHandler.parseResponseToString(mockedResponse))
                    .thenReturn(responseJson);

            ReflectSchemaResponse mockSchemaResponse = new Gson().fromJson(responseJson, ReflectSchemaResponse.class);
            jsonUtilMock.when(() -> JsonUtil.jsonToResponseModel(
                            eq(responseJson),
                            eq(ReflectSchemaResponse.class)))
                    .thenReturn(mockSchemaResponse);

            List<String> permissions = SearchActionsUtil.getAllPermissions(TEST_RESOURCE_TYPE);

            List<String> expectedPermissions = new ArrayList<>();
            expectedPermissions.add("read");
            assertEquals(permissions, expectedPermissions);
            verify(mockedResponse).close();
        }
    }

    public void testGetAllPermissionsForErrorResponse() {

        try (MockedStatic<HttpHandler> httpHandlerMock = mockStatic(HttpHandler.class);
             MockedStatic<JsonUtil> jsonUtilMock = mockStatic(JsonUtil.class)) {

            jsonUtilMock.when(() -> JsonUtil.parseToJsonString(any(ReflectSchemaRequest.class)))
                    .thenReturn("mockRequestBody");

            CloseableHttpResponse mockedResponse = mock(CloseableHttpResponse.class);
            StatusLine statusLine = mock(StatusLine.class);
            when(statusLine.getStatusCode()).thenReturn(HttpStatus.SC_BAD_REQUEST);
            when(mockedResponse.getStatusLine()).thenReturn(statusLine);

            httpHandlerMock.when(() -> HttpHandler.sendPOSTRequest(
                            eq(REFLECT_SCHEMA_ENDPOINT),
                            anyString()))
                    .thenReturn(mockedResponse);

            String responseJson =
                    "{\"code\": 7,\"message\": \"invalid preshared key: invalid token\",\"details\": []}";
            httpHandlerMock.when(() -> HttpHandler.parseResponseToString(mockedResponse))
                    .thenReturn(responseJson);

            SpiceDbErrorResponse mockErrorResponse = new Gson().fromJson(responseJson, SpiceDbErrorResponse.class);
            jsonUtilMock.when(() -> JsonUtil.jsonToResponseModel(
                            eq(responseJson),
                            eq(SpiceDbErrorResponse.class)))
                    .thenReturn(mockErrorResponse);

            try {
                SearchActionsUtil.getAllPermissions(TEST_RESOURCE_TYPE);
            } catch (SpicedbEvaluationException e) {
                assertEquals(e.getErrorCode(), "7");
                assertEquals(e.getMessage(), "invalid preshared key: invalid token");
            }
        }
    }

    @Test
    public void testGetAllPermissionsForUnknownErrorResponse() {

        try (MockedStatic<HttpHandler> httpHandlerMock = mockStatic(HttpHandler.class);
             MockedStatic<JsonUtil> jsonUtilMock = mockStatic(JsonUtil.class)) {

            jsonUtilMock.when(() -> JsonUtil.parseToJsonString(any(ReflectSchemaRequest.class)))
                    .thenReturn("mockRequestBody");

            CloseableHttpResponse mockedResponse = mock(CloseableHttpResponse.class);
            StatusLine statusLine = mock(StatusLine.class);
            when(statusLine.getStatusCode()).thenReturn(0);
            when(mockedResponse.getStatusLine()).thenReturn(statusLine);

            httpHandlerMock.when(() -> HttpHandler.sendPOSTRequest(
                            eq(REFLECT_SCHEMA_ENDPOINT),
                            anyString()))
                    .thenReturn(mockedResponse);

            try {
                SearchActionsUtil.getAllPermissions(TEST_RESOURCE_TYPE);
            } catch (SpicedbEvaluationException e) {
                assertEquals(e.getMessage(), "Searching actions from spiceDB failed while reflecting the " +
                        "schema. Cannot identify error code.");
            }
        }
    }

    @Test
    public void testGetAllPermissionsForIOException() {

        try (MockedStatic<HttpHandler> httpHandlerMock = mockStatic(HttpHandler.class);
             MockedStatic<JsonUtil> jsonUtilMock = mockStatic(JsonUtil.class)) {

            jsonUtilMock.when(() -> JsonUtil.parseToJsonString(any(ReflectSchemaRequest.class)))
                    .thenReturn("mockRequestBody");

            httpHandlerMock.when(() -> HttpHandler.sendPOSTRequest(
                            eq(REFLECT_SCHEMA_ENDPOINT),
                            anyString())).thenThrow(new IOException("IO Exception"));

            try {
                SearchActionsUtil.getAllPermissions(TEST_RESOURCE_TYPE);
            } catch (SpicedbEvaluationException e) {
                assertEquals(e.getErrorCode(), "Could not connect to SpiceDB to reflect schema.");
                assertEquals(e.getMessage(), "IO Exception");
            }
        }
    }

    @Test
    public void testGetAllPermissionsForURISyntaxException() {

        try (MockedStatic<HttpHandler> httpHandlerMock = mockStatic(HttpHandler.class);
             MockedStatic<JsonUtil> jsonUtilMock = mockStatic(JsonUtil.class)) {

            jsonUtilMock.when(() -> JsonUtil.parseToJsonString(any(ReflectSchemaRequest.class)))
                    .thenReturn("mockRequestBody");

            httpHandlerMock.when(() -> HttpHandler.sendPOSTRequest(
                    eq(REFLECT_SCHEMA_ENDPOINT),
                    anyString())).thenThrow(new URISyntaxException("Input", "URI Syntax Exception"));

            try {
                SearchActionsUtil.getAllPermissions(TEST_RESOURCE_TYPE);
            } catch (SpicedbEvaluationException e) {
                assertEquals(e.getErrorCode(), "URI error occurred while creating the request URL. Could not " +
                        "connect to SpiceDB to reflect schema.");
                assertEquals(e.getMessage(), "URI Syntax Exception: Input");
            }
        }
    }

    @Test
    public void testGetAuthorizedAction() throws Exception {

        try (MockedStatic<HttpHandler> httpHandlerMock = mockStatic(HttpHandler.class);
             MockedStatic<JsonUtil> jsonUtilMock = mockStatic(JsonUtil.class)) {

            jsonUtilMock.when(() -> JsonUtil.parseToJsonString(any(BulkCheckPermissionRequest.class)))
                    .thenReturn("mockRequestBody");

            CloseableHttpResponse mockedResponse = mock(CloseableHttpResponse.class);
            StatusLine statusLine = mock(StatusLine.class);
            when(statusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK);
            when(mockedResponse.getStatusLine()).thenReturn(statusLine);

            httpHandlerMock.when(() -> HttpHandler.sendPOSTRequest(eq(BULK_EVALUATION_ENDPOINT), anyString()))
                    .thenReturn(mockedResponse);

            BulkCheckPermissionResponse bulkCheckPermissionResponse = new BulkCheckPermissionResponse();
            Field resultsField = BulkCheckPermissionResponse.class.getDeclaredField("results");
            resultsField.setAccessible(true);
            List<BulkCheckPermissionResult> results = new ArrayList<>();
            BulkCheckPermissionResult result = mock(BulkCheckPermissionResult.class);
            when(result.isResultAvailable()).thenReturn(true);
            Map<String, Object> mockResultMap = new HashMap<>();
            mockResultMap.put("permissionship", "PERMISSIONSHIP_HAS_PERMISSION");
            when(result.getResult()).thenReturn(mockResultMap);
            results.add(result);
            resultsField.set(bulkCheckPermissionResponse, results);
            jsonUtilMock.when(() -> JsonUtil.jsonToResponseModel(any(), eq(BulkCheckPermissionResponse.class)))
                    .thenReturn(bulkCheckPermissionResponse);

            ArrayList<String> permissions = new ArrayList<>();
            permissions.add("read");
            AuthorizationSubject subject = new AuthorizationSubject("subjectType", "subjectId");
            AuthorizationResource resource = new AuthorizationResource("resourceType", "resourceId");

            SearchActionsResponse searchActionsResponse = SearchActionsUtil.getAuthorizedActions(
                    permissions, subject, resource);

            assertNotNull(searchActionsResponse);
            assertEquals(searchActionsResponse.getResults().size(), 1);
            assertNotNull(searchActionsResponse.getResults().get(0));
        }
    }
}
