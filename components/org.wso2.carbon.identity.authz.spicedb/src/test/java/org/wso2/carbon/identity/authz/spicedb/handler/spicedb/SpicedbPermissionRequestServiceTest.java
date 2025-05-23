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

import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.wso2.carbon.identity.authorization.framework.model.AccessEvaluationRequest;
import org.wso2.carbon.identity.authorization.framework.model.AccessEvaluationResponse;
import org.wso2.carbon.identity.authorization.framework.model.AuthorizationAction;
import org.wso2.carbon.identity.authorization.framework.model.AuthorizationResource;
import org.wso2.carbon.identity.authorization.framework.model.AuthorizationSubject;
import org.wso2.carbon.identity.authorization.framework.model.BulkAccessEvaluationRequest;
import org.wso2.carbon.identity.authorization.framework.model.BulkAccessEvaluationResponse;
import org.wso2.carbon.identity.authorization.framework.model.SearchActionsRequest;
import org.wso2.carbon.identity.authorization.framework.model.SearchResourcesRequest;
import org.wso2.carbon.identity.authorization.framework.model.SearchResourcesResponse;
import org.wso2.carbon.identity.authorization.framework.model.SearchSubjectsRequest;
import org.wso2.carbon.identity.authorization.framework.model.SearchSubjectsResponse;
import org.wso2.carbon.identity.authz.spicedb.constants.SpiceDbApiConstants;
import org.wso2.carbon.identity.authz.spicedb.handler.exception.SpicedbEvaluationException;
import org.wso2.carbon.identity.authz.spicedb.handler.model.BulkCheckPermissionRequest;
import org.wso2.carbon.identity.authz.spicedb.handler.model.BulkCheckPermissionResponse;
import org.wso2.carbon.identity.authz.spicedb.handler.model.BulkCheckPermissionResult;
import org.wso2.carbon.identity.authz.spicedb.handler.model.CheckPermissionRequest;
import org.wso2.carbon.identity.authz.spicedb.handler.model.CheckPermissionResponse;
import org.wso2.carbon.identity.authz.spicedb.handler.model.LookupResourcesRequest;
import org.wso2.carbon.identity.authz.spicedb.handler.model.LookupResourcesResult;
import org.wso2.carbon.identity.authz.spicedb.handler.model.LookupSubjectsRequest;
import org.wso2.carbon.identity.authz.spicedb.handler.model.LookupSubjectsResult;
import org.wso2.carbon.identity.authz.spicedb.handler.model.SpiceDbErrorResponse;
import org.wso2.carbon.identity.authz.spicedb.handler.util.HttpHandler;
import org.wso2.carbon.identity.authz.spicedb.handler.util.JsonUtil;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

/**
 * Unit test class for {@link SpicedbPermissionRequestService}.
 */
@Test
public class SpicedbPermissionRequestServiceTest {

    private static final String FAILURE_MESSAGE = "Expected SpicedbEvaluationException";
    private static final String REQUEST_BODY = "mockedRequestBody";
    private static final String ERROR_MESSAGE = "error_message";
    private static final String ERROR_CODE = "error_code";
    private static final String IO_EXCEPTION_MESSAGE = "Connection error";
    private static final String URI_SYNTAX_EXCEPTION_INPUT = "URI input";
    private static final String URI_SYNTAX_EXCEPTION_REASON = "URI reason";
    private static final String SUBJECT_ID = "subjectId";
    private static final String RESOURCE_ID = "resourceId";
    private static final String SUBJECT_TYPE = "subjectType";
    private static final String RESOURCE_TYPE = "resourceType";

    private SpicedbPermissionRequestService service;
    private AccessEvaluationRequest accessEvaluationRequest;
    private BulkAccessEvaluationRequest bulkAccessEvaluationRequest;
    private SearchResourcesRequest searchResourcesRequest;
    private SearchSubjectsRequest searchSubjectsRequest;
    private SearchActionsRequest searchActionsRequest;
    private AuthorizationResource resource;
    private AuthorizationAction action;
    private AuthorizationSubject subject;

    @BeforeMethod
    public void setUp() {

        service = new SpicedbPermissionRequestService();
        resource = new AuthorizationResource(RESOURCE_TYPE, RESOURCE_ID);
        action = mock(AuthorizationAction.class);
        subject = new AuthorizationSubject(SUBJECT_TYPE, SUBJECT_ID);
        accessEvaluationRequest = new AccessEvaluationRequest(subject, action, resource);
        bulkAccessEvaluationRequest = mock(BulkAccessEvaluationRequest.class);
        ArrayList<AccessEvaluationRequest> requests = new ArrayList<>();
        requests.add(accessEvaluationRequest);
        when(bulkAccessEvaluationRequest.getRequestItems()).thenReturn(requests);
        searchResourcesRequest = new SearchResourcesRequest(resource, action, subject);
        searchSubjectsRequest = new SearchSubjectsRequest(subject, action, resource);
        searchActionsRequest = new SearchActionsRequest(subject, resource);
    }

    @AfterMethod
    public void tearDown() {

        service = null;
        accessEvaluationRequest = null;
        bulkAccessEvaluationRequest = null;
        searchResourcesRequest = null;
        searchSubjectsRequest = null;
        searchActionsRequest = null;
    }

    @Test
    public void testEvaluateWithNullRequest() {

        try {
            service.evaluate(null);
            fail(FAILURE_MESSAGE);
        } catch (SpicedbEvaluationException e) {
            assertEquals(e.getMessage(), "Invalid request. Access evaluation request cannot be null.");
        }
    }

    @Test
    public void testEvaluateForSuccessfulResponse() throws Exception {

        try (MockedStatic<HttpHandler> httpHandlerMock = mockStatic(HttpHandler.class);
             MockedStatic<JsonUtil> jsonUtilMock = mockStatic(JsonUtil.class)) {

            // Mock JSON serialization of CheckPermissionRequest
            jsonUtilMock.when(() -> JsonUtil.parseToJsonString(any(CheckPermissionRequest.class)))
                    .thenReturn(REQUEST_BODY);

            // Mock HTTP response
            CloseableHttpResponse mockedResponse = mock(CloseableHttpResponse.class);
            StatusLine statusLine = mock(StatusLine.class);
            when(mockedResponse.getStatusLine()).thenReturn(statusLine);
            when(statusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK);
            httpHandlerMock.when(() -> HttpHandler.sendPOSTRequest(
                            SpiceDbApiConstants.PERMISSION_CHECK, REQUEST_BODY))
                    .thenReturn(mockedResponse);

            CheckPermissionResponse checkResponse = mock(CheckPermissionResponse.class);
            when(checkResponse.isAuthorized()).thenReturn(true);
            HashMap<String, Object> context = new HashMap<>();
            context.put("key", "value");
            when(checkResponse.getPartialCaveatInfo()).thenReturn(context);
            jsonUtilMock.when(() -> JsonUtil.jsonToResponseModel(any(), eq(CheckPermissionResponse.class)))
                    .thenReturn(checkResponse);

            AccessEvaluationResponse result = service.evaluate(accessEvaluationRequest);

            assertTrue(result.getDecision());
            assertEquals(result.getContext().get("key"), "value");
            verify(mockedResponse).close();
        }
    }

    @Test
    public void testEvaluateForErrorResponse() {

        try (MockedStatic<HttpHandler> httpHandlerMock = mockStatic(HttpHandler.class);
             MockedStatic<JsonUtil> jsonUtilMock = mockStatic(JsonUtil.class)) {

            // Mock JSON serialization of CheckPermissionRequest
            jsonUtilMock.when(() -> JsonUtil.parseToJsonString(any(CheckPermissionRequest.class)))
                    .thenReturn(REQUEST_BODY);

            // Mock HTTP response
            CloseableHttpResponse mockedResponse = mock(CloseableHttpResponse.class);
            StatusLine statusLine = mock(StatusLine.class);
            when(mockedResponse.getStatusLine()).thenReturn(statusLine);
            when(statusLine.getStatusCode()).thenReturn(HttpStatus.SC_BAD_REQUEST);

            httpHandlerMock.when(() -> HttpHandler.sendPOSTRequest(
                            SpiceDbApiConstants.PERMISSION_CHECK, REQUEST_BODY))
                    .thenReturn(mockedResponse);

            SpiceDbErrorResponse errorResponse = mock(SpiceDbErrorResponse.class);
            when(errorResponse.getCode()).thenReturn(ERROR_CODE);
            when(errorResponse.getMessage()).thenReturn(ERROR_MESSAGE);
            jsonUtilMock.when(() -> JsonUtil.jsonToResponseModel(any(), eq(SpiceDbErrorResponse.class)))
                    .thenReturn(errorResponse);

            try {
                service.evaluate(accessEvaluationRequest);
                fail(FAILURE_MESSAGE);
            } catch (SpicedbEvaluationException e) {
                assertEquals(e.getMessage(), ERROR_MESSAGE);
                assertEquals(e.getErrorCode(), ERROR_CODE);
            }
        }
    }

    @Test
    public void testEvaluateForUnknownErrorResponse() {

        try (MockedStatic<HttpHandler> httpHandlerMock = mockStatic(HttpHandler.class);
             MockedStatic<JsonUtil> jsonUtilMock = mockStatic(JsonUtil.class)) {

            // Mock JSON serialization of CheckPermissionRequest
            jsonUtilMock.when(() -> JsonUtil.parseToJsonString(any(CheckPermissionRequest.class)))
                    .thenReturn(REQUEST_BODY);

            // Mock HTTP response
            CloseableHttpResponse mockedResponse = mock(CloseableHttpResponse.class);
            StatusLine statusLine = mock(StatusLine.class);
            when(mockedResponse.getStatusLine()).thenReturn(statusLine);
            when(statusLine.getStatusCode()).thenReturn(0);

            httpHandlerMock.when(() -> HttpHandler.sendPOSTRequest(
                            SpiceDbApiConstants.PERMISSION_CHECK, REQUEST_BODY))
                    .thenReturn(mockedResponse);

            try {
                service.evaluate(accessEvaluationRequest);
                fail(FAILURE_MESSAGE);
            } catch (SpicedbEvaluationException e) {
                assertEquals(e.getMessage(), "Authorization check from spiceDB failed. " +
                        "Cannot identify error code.");
            }
        }
    }


    @Test
    public void testEvaluateForIOException() {

        try (MockedStatic<HttpHandler> httpHandlerMock = Mockito.mockStatic(HttpHandler.class);
             MockedStatic<JsonUtil> jsonUtilMock = Mockito.mockStatic(JsonUtil.class)) {

            jsonUtilMock.when(() -> JsonUtil.parseToJsonString(any(CheckPermissionRequest.class)))
                    .thenReturn(REQUEST_BODY);

            httpHandlerMock.when(() -> HttpHandler.sendPOSTRequest(any(), any()))
                    .thenThrow(new IOException(IO_EXCEPTION_MESSAGE));

            try {
                service.evaluate(accessEvaluationRequest);
                fail(FAILURE_MESSAGE);
            } catch (SpicedbEvaluationException ex) {
                assertEquals(ex.getMessage(), IO_EXCEPTION_MESSAGE);
            }
        }
    }

    @Test
    public void testEvaluateForURISyntaxException() {

        try (MockedStatic<HttpHandler> httpHandlerMock = Mockito.mockStatic(HttpHandler.class);
             MockedStatic<JsonUtil> jsonUtilMock = Mockito.mockStatic(JsonUtil.class)) {

            jsonUtilMock.when(() -> JsonUtil.parseToJsonString(any(CheckPermissionRequest.class)))
                    .thenReturn(REQUEST_BODY);

            httpHandlerMock.when(() -> HttpHandler.sendPOSTRequest(any(), any()))
                    .thenThrow(new URISyntaxException(URI_SYNTAX_EXCEPTION_INPUT, URI_SYNTAX_EXCEPTION_REASON));

            try {
                service.evaluate(accessEvaluationRequest);
                fail(FAILURE_MESSAGE);
            } catch (SpicedbEvaluationException ex) {
                assertEquals(ex.getMessage(), URI_SYNTAX_EXCEPTION_REASON + ": " + URI_SYNTAX_EXCEPTION_INPUT);
            }
        }
    }

    @Test
    public void testBulkEvaluateWithNullRequest() {

        try {
            service.bulkEvaluate(null);
            fail(FAILURE_MESSAGE);
        } catch (SpicedbEvaluationException e) {
            assertEquals(e.getMessage(), "Invalid request. Bulk access evaluation request cannot be null.");
        }
    }

    @Test
    public void testBulkEvaluateForSuccessResponse() throws Exception {

        try (MockedStatic<HttpHandler> httpHandlerMock = mockStatic(HttpHandler.class);
             MockedStatic<JsonUtil> jsonUtilMock = mockStatic(JsonUtil.class)) {

            // Mock JSON serialization of BulkCheckPermissionRequest
            jsonUtilMock.when(() -> JsonUtil.parseToJsonString(any(BulkCheckPermissionRequest.class)))
                    .thenReturn(REQUEST_BODY);

            // Mock HTTP response
            CloseableHttpResponse mockedResponse = mock(CloseableHttpResponse.class);
            StatusLine statusLine = mock(StatusLine.class);
            when(mockedResponse.getStatusLine()).thenReturn(statusLine);
            when(statusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK);

            httpHandlerMock.when(() -> HttpHandler.sendPOSTRequest(
                            SpiceDbApiConstants.PERMISSIONS_BULKCHECK, REQUEST_BODY))
                    .thenReturn(mockedResponse);

            BulkCheckPermissionResponse bulkCheckPermissionResponse = new BulkCheckPermissionResponse();
            Field resultsField = BulkCheckPermissionResponse.class.getDeclaredField("results");
            resultsField.setAccessible(true);
            List<BulkCheckPermissionResult> results = new ArrayList<>();
            BulkCheckPermissionResult result = mock(BulkCheckPermissionResult.class);
            when(result.isResultAvailable()).thenReturn(true);
            when(result.getResult()).thenReturn(new HashMap<>());
            results.add(result);
            resultsField.set(bulkCheckPermissionResponse, results);
            jsonUtilMock.when(() -> JsonUtil.jsonToResponseModel(any(), eq(BulkCheckPermissionResponse.class)))
                    .thenReturn(bulkCheckPermissionResponse);

            BulkAccessEvaluationResponse response = service.bulkEvaluate(bulkAccessEvaluationRequest);

            assertNotNull(response);
            assertEquals(response.getResults().size(), results.size());
            verify(mockedResponse).close();
        }
    }

    @Test
    public void testBulkEvaluateForErrorResponse() {

        try (MockedStatic<HttpHandler> httpHandlerMock = mockStatic(HttpHandler.class);
             MockedStatic<JsonUtil> jsonUtilMock = mockStatic(JsonUtil.class)) {

            // Mock JSON serialization of BulkCheckPermissionRequest
            jsonUtilMock.when(() -> JsonUtil.parseToJsonString(any(BulkCheckPermissionRequest.class)))
                    .thenReturn(REQUEST_BODY);

            // Mock HTTP response
            CloseableHttpResponse mockedResponse = mock(CloseableHttpResponse.class);
            StatusLine statusLine = mock(StatusLine.class);
            when(mockedResponse.getStatusLine()).thenReturn(statusLine);
            when(statusLine.getStatusCode()).thenReturn(HttpStatus.SC_BAD_REQUEST);
            httpHandlerMock.when(() -> HttpHandler.sendPOSTRequest(
                            SpiceDbApiConstants.PERMISSIONS_BULKCHECK, REQUEST_BODY))
                    .thenReturn(mockedResponse);

            SpiceDbErrorResponse errorResponse = mock(SpiceDbErrorResponse.class);
            when(errorResponse.getCode()).thenReturn(ERROR_CODE);
            when(errorResponse.getMessage()).thenReturn(ERROR_MESSAGE);
            jsonUtilMock.when(() -> JsonUtil.jsonToResponseModel(any(), eq(SpiceDbErrorResponse.class)))
                    .thenReturn(errorResponse);

            try {
                service.bulkEvaluate(bulkAccessEvaluationRequest);
                fail(FAILURE_MESSAGE);
            } catch (SpicedbEvaluationException e) {
                assertEquals(e.getMessage(), ERROR_MESSAGE);
                assertEquals(e.getErrorCode(), ERROR_CODE);
            }
        }
    }

    @Test
    public void testBulkEvaluateForUnknownErrorResponse() {

        try (MockedStatic<HttpHandler> httpHandlerMock = mockStatic(HttpHandler.class);
             MockedStatic<JsonUtil> jsonUtilMock = mockStatic(JsonUtil.class)) {

            // Mock JSON serialization of BulkCheckPermissionRequest
            jsonUtilMock.when(() -> JsonUtil.parseToJsonString(any(BulkCheckPermissionRequest.class)))
                    .thenReturn(REQUEST_BODY);

            // Mock HTTP response
            CloseableHttpResponse mockedResponse = mock(CloseableHttpResponse.class);
            StatusLine statusLine = mock(StatusLine.class);
            when(mockedResponse.getStatusLine()).thenReturn(statusLine);
            when(statusLine.getStatusCode()).thenReturn(0);

            httpHandlerMock.when(() -> HttpHandler.sendPOSTRequest(
                            SpiceDbApiConstants.PERMISSIONS_BULKCHECK, REQUEST_BODY))
                    .thenReturn(mockedResponse);

            try {
                service.bulkEvaluate(bulkAccessEvaluationRequest);
                fail(FAILURE_MESSAGE);
            } catch (SpicedbEvaluationException e) {
                assertEquals(e.getMessage(), "Authorization bulk check from spiceDB failed." +
                        " Cannot identify error code.");
            }
        }
    }

    @Test
    public void testBulkEvaluateforIOException() {

        try (MockedStatic<HttpHandler> httpHandlerMock = mockStatic(HttpHandler.class);
             MockedStatic<JsonUtil> jsonUtilMock = mockStatic(JsonUtil.class)) {

            jsonUtilMock.when(() -> JsonUtil.parseToJsonString(any(BulkCheckPermissionRequest.class)))
                    .thenReturn(REQUEST_BODY);

            httpHandlerMock.when(() -> HttpHandler.sendPOSTRequest(any(), any()))
                    .thenThrow(new IOException(IO_EXCEPTION_MESSAGE));

            try {
                service.bulkEvaluate(bulkAccessEvaluationRequest);
                fail(FAILURE_MESSAGE);
            } catch (SpicedbEvaluationException ex) {
                assertEquals(ex.getMessage(), IO_EXCEPTION_MESSAGE);
            }
        }
    }

    @Test
    public void testBulkEvaluateforURISyntaxException() {

        try (MockedStatic<HttpHandler> httpHandlerMock = mockStatic(HttpHandler.class);
             MockedStatic<JsonUtil> jsonUtilMock = mockStatic(JsonUtil.class)) {

            jsonUtilMock.when(() -> JsonUtil.parseToJsonString(any(BulkCheckPermissionRequest.class)))
                    .thenReturn(REQUEST_BODY);

            httpHandlerMock.when(() -> HttpHandler.sendPOSTRequest(any(), any()))
                    .thenThrow(new URISyntaxException(URI_SYNTAX_EXCEPTION_INPUT, URI_SYNTAX_EXCEPTION_REASON));

            try {
                service.bulkEvaluate(bulkAccessEvaluationRequest);
                fail(FAILURE_MESSAGE);
            } catch (SpicedbEvaluationException ex) {
                assertEquals(ex.getMessage(), URI_SYNTAX_EXCEPTION_REASON + ": " + URI_SYNTAX_EXCEPTION_INPUT);
            }
        }
    }

    @Test
    public void testSearchResourcesWithNullRequest() {

        try {
            service.searchResources(null);
            fail(FAILURE_MESSAGE);
        } catch (SpicedbEvaluationException e) {
            assertEquals(e.getMessage(), "Invalid request. Search resources request cannot be null.");
        }
    }

    @Test
    public void testSearchResourcesForSuccessfulResponse() throws Exception {

        try (MockedStatic<HttpHandler> httpHandlerMock = mockStatic(HttpHandler.class);
             MockedStatic<JsonUtil> jsonUtilMock = mockStatic(JsonUtil.class)) {

            // Mock JSON serialization of CheckPermissionRequest
            jsonUtilMock.when(() -> JsonUtil.parseToJsonString(any(LookupResourcesRequest.class)))
                    .thenReturn(REQUEST_BODY);

            // Mock HTTP response
            CloseableHttpResponse mockedResponse = mock(CloseableHttpResponse.class);
            StatusLine statusLine = mock(StatusLine.class);
            when(mockedResponse.getStatusLine()).thenReturn(statusLine);
            when(statusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK);
            httpHandlerMock.when(() -> HttpHandler.sendPOSTRequest(
                            SpiceDbApiConstants.LOOKUP_RESOURCES, REQUEST_BODY))
                    .thenReturn(mockedResponse);

            String response = "{\"result\":{\"lookedUpAt\":{\"token\":\"1111\"},\"resourceObjectId\":\"resourceId\"" +
                    ",\"permissionship\":\"LOOKUP_PERMISSIONSHIP_UNSPECIFIED\",\"partialCaveatInfo\":" +
                    "{\"missingRequiredContext\":[\"<string>\",\"<string>\"]},\"afterResultCursor\":{\"token\":\"" +
                    "<string>\"}},\"error\":{\"code\":\"<integer>\",\"message\":\"<string>\",\"details\":[{\"@type\":" +
                    "\"<string>\",\"mollit59\":{}},{\"@type\":\"<string>\",\"Excepteur7\":{},\"eiusmod26c\":{}}]}}";

            httpHandlerMock.when(()-> HttpHandler.parseResponseToString(any())).thenReturn(response);
            jsonUtilMock.when(()-> JsonUtil.jsonToResponseModel(any(), eq(LookupResourcesResult.class)))
                    .thenCallRealMethod();

            SearchResourcesResponse result = service.searchResources(searchResourcesRequest);

            assertNotNull(result);
            assertEquals(result.getResults().size(), 1);
            assertEquals(result.getResults().get(0).getResourceId(), RESOURCE_ID);
        }
    }

    @Test
    public void testSearchResourcesForErrorResponse() {

        try (MockedStatic<HttpHandler> httpHandlerMock = mockStatic(HttpHandler.class);
             MockedStatic<JsonUtil> jsonUtilMock = mockStatic(JsonUtil.class)) {

            // Mock JSON serialization of CheckPermissionRequest
            jsonUtilMock.when(() -> JsonUtil.parseToJsonString(any(LookupResourcesRequest.class)))
                    .thenReturn(REQUEST_BODY);

            // Mock HTTP response
            CloseableHttpResponse mockedResponse = mock(CloseableHttpResponse.class);
            StatusLine statusLine = mock(StatusLine.class);
            when(mockedResponse.getStatusLine()).thenReturn(statusLine);
            when(statusLine.getStatusCode()).thenReturn(HttpStatus.SC_BAD_REQUEST);
            httpHandlerMock.when(() -> HttpHandler.sendPOSTRequest(
                            SpiceDbApiConstants.LOOKUP_RESOURCES, REQUEST_BODY))
                    .thenReturn(mockedResponse);

            SpiceDbErrorResponse errorResponse = mock(SpiceDbErrorResponse.class);
            when(errorResponse.getCode()).thenReturn(ERROR_CODE);
            when(errorResponse.getMessage()).thenReturn(ERROR_MESSAGE);
            jsonUtilMock.when(() -> JsonUtil.jsonToResponseModel(any(), eq(SpiceDbErrorResponse.class)))
                    .thenReturn(errorResponse);

            try {
                service.searchResources(searchResourcesRequest);
                fail(FAILURE_MESSAGE);
            } catch (SpicedbEvaluationException e) {
                assertEquals(e.getMessage(), ERROR_MESSAGE);
                assertEquals(e.getErrorCode(), ERROR_CODE);
            }
        }
    }

    @Test
    public void testSearchResourcesForUnknownErrorResponse() {

        try (MockedStatic<HttpHandler> httpHandlerMock = mockStatic(HttpHandler.class);
             MockedStatic<JsonUtil> jsonUtilMock = mockStatic(JsonUtil.class)) {

            // Mock JSON serialization of CheckPermissionRequest
            jsonUtilMock.when(() -> JsonUtil.parseToJsonString(any(LookupResourcesRequest.class)))
                    .thenReturn(REQUEST_BODY);

            // Mock HTTP response
            CloseableHttpResponse mockedResponse = mock(CloseableHttpResponse.class);
            StatusLine statusLine = mock(StatusLine.class);
            when(mockedResponse.getStatusLine()).thenReturn(statusLine);
            when(statusLine.getStatusCode()).thenReturn(0);

            httpHandlerMock.when(() -> HttpHandler.sendPOSTRequest(
                            SpiceDbApiConstants.LOOKUP_RESOURCES, REQUEST_BODY))
                    .thenReturn(mockedResponse);

            try {
                service.searchResources(searchResourcesRequest);
                fail(FAILURE_MESSAGE);
            } catch (SpicedbEvaluationException e) {
                assertEquals(e.getMessage(), "Looking up resources from spiceDB failed. Cannot identify error code.");
            }
        }
    }

    @Test
    public void testSearchResourcesForIOException() {

        try (MockedStatic<HttpHandler> httpHandlerMock = mockStatic(HttpHandler.class);
             MockedStatic<JsonUtil> jsonUtilMock = mockStatic(JsonUtil.class)) {

            jsonUtilMock.when(() -> JsonUtil.parseToJsonString(any(LookupResourcesRequest.class)))
                    .thenReturn(REQUEST_BODY);

            httpHandlerMock.when(() -> HttpHandler.sendPOSTRequest(any(), any()))
                    .thenThrow(new IOException(IO_EXCEPTION_MESSAGE));

            try {
                service.searchResources(searchResourcesRequest);
                fail(FAILURE_MESSAGE);
            } catch (SpicedbEvaluationException ex) {
                assertEquals(ex.getMessage(), IO_EXCEPTION_MESSAGE);
            }
        }
    }

    @Test
    public void testSearchResourcesForURISyntaxException() {

        try (MockedStatic<HttpHandler> httpHandlerMock = mockStatic(HttpHandler.class);
             MockedStatic<JsonUtil> jsonUtilMock = mockStatic(JsonUtil.class)) {

            jsonUtilMock.when(() -> JsonUtil.parseToJsonString(any(LookupResourcesRequest.class)))
                    .thenReturn(REQUEST_BODY);

            httpHandlerMock.when(() -> HttpHandler.sendPOSTRequest(any(), any()))
                    .thenThrow(new URISyntaxException(URI_SYNTAX_EXCEPTION_INPUT, URI_SYNTAX_EXCEPTION_REASON));

            try {
                service.searchResources(searchResourcesRequest);
                fail(FAILURE_MESSAGE);
            } catch (SpicedbEvaluationException ex) {
                assertEquals(ex.getMessage(), URI_SYNTAX_EXCEPTION_REASON + ": " + URI_SYNTAX_EXCEPTION_INPUT);
            }
        }
    }

    @Test
    public void testSearchSubjectsWithNullRequest() {

        try {
            service.searchSubjects(null);
            fail(FAILURE_MESSAGE);
        } catch (SpicedbEvaluationException e) {
            assertEquals(e.getMessage(), "Invalid request. Search subjects request cannot be null.");
        }
    }

    @Test
    public void testSearchSubjectsForSuccessfulResponse() throws Exception {

        try (MockedStatic<HttpHandler> httpHandlerMock = mockStatic(HttpHandler.class);
             MockedStatic<JsonUtil> jsonUtilMock = mockStatic(JsonUtil.class)) {

            // Mock JSON serialization of CheckPermissionRequest
            jsonUtilMock.when(() -> JsonUtil.parseToJsonString(any(LookupSubjectsRequest.class)))
                    .thenReturn(REQUEST_BODY);

            // Mock HTTP response
            CloseableHttpResponse mockedResponse = mock(CloseableHttpResponse.class);
            StatusLine statusLine = mock(StatusLine.class);
            when(mockedResponse.getStatusLine()).thenReturn(statusLine);
            when(statusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK);
            httpHandlerMock.when(() -> HttpHandler.sendPOSTRequest(
                            SpiceDbApiConstants.LOOKUP_SUBJECTS, REQUEST_BODY))
                    .thenReturn(mockedResponse);

            String response = "{\"result\": {\"lookedUpAt\": {\"token\": \"1234\"},\"subjectObjectId\": \"subjectId\"" +
                    ",\"excludedSubjectIds\": [\"<string>\",\"<string>\"],\"permissionship\": " +
                    "\"LOOKUP_PERMISSIONSHIP_UNSPECIFIED\",\"partialCaveatInfo\": {\"missingRequiredContext\": " +
                    "[\"<string>\",\"<string>\"]},\"subject\": {\"subjectObjectId\": \"<string>\"," +
                    "\"permissionship\": \"LOOKUP_PERMISSIONSHIP_UNSPECIFIED\",\"partialCaveatInfo\": {" +
                    "\"missingRequiredContext\": [\"<string>\",\"<string>\"]}},\"excludedSubjects\": [],\"" +
                    "afterResultCursor\": {\"token\": \"<string>\"}}}";

            httpHandlerMock.when(()-> HttpHandler.parseResponseToString(any())).thenReturn(response);
            jsonUtilMock.when(()-> JsonUtil.jsonToResponseModel(any(), eq(LookupSubjectsResult.class)))
                    .thenCallRealMethod();

            SearchSubjectsResponse result = service.searchSubjects(searchSubjectsRequest);

            assertNotNull(result);
            assertEquals(result.getResults().size(), 1);
            assertEquals(result.getResults().get(0).getSubjectId(), SUBJECT_ID);
        }
    }

    @Test
    public void testSearchSubjectsForErrorResponse() {

        try (MockedStatic<HttpHandler> httpHandlerMock = mockStatic(HttpHandler.class);
             MockedStatic<JsonUtil> jsonUtilMock = mockStatic(JsonUtil.class)) {

            // Mock JSON serialization of CheckPermissionRequest
            jsonUtilMock.when(() -> JsonUtil.parseToJsonString(any(LookupSubjectsRequest.class)))
                    .thenReturn(REQUEST_BODY);

            // Mock HTTP response
            CloseableHttpResponse mockedResponse = mock(CloseableHttpResponse.class);
            StatusLine statusLine = mock(StatusLine.class);
            when(mockedResponse.getStatusLine()).thenReturn(statusLine);
            when(statusLine.getStatusCode()).thenReturn(HttpStatus.SC_BAD_REQUEST);
            httpHandlerMock.when(() -> HttpHandler.sendPOSTRequest(
                            SpiceDbApiConstants.LOOKUP_SUBJECTS, REQUEST_BODY))
                    .thenReturn(mockedResponse);

            SpiceDbErrorResponse errorResponse = mock(SpiceDbErrorResponse.class);
            when(errorResponse.getCode()).thenReturn(ERROR_CODE);
            when(errorResponse.getMessage()).thenReturn(ERROR_MESSAGE);
            jsonUtilMock.when(() -> JsonUtil.jsonToResponseModel(any(), eq(SpiceDbErrorResponse.class)))
                    .thenReturn(errorResponse);

            try {
                service.searchSubjects(searchSubjectsRequest);
                fail(FAILURE_MESSAGE);
            } catch (SpicedbEvaluationException e) {
                assertEquals(e.getMessage(), ERROR_MESSAGE);
                assertEquals(e.getErrorCode(), ERROR_CODE);
            }
        }
    }

    @Test
    public void testSearchSubjectsForUnknownErrorResponse() {

        try (MockedStatic<HttpHandler> httpHandlerMock = mockStatic(HttpHandler.class);
             MockedStatic<JsonUtil> jsonUtilMock = mockStatic(JsonUtil.class)) {

            // Mock JSON serialization of CheckPermissionRequest
            jsonUtilMock.when(() -> JsonUtil.parseToJsonString(any(LookupSubjectsRequest.class)))
                    .thenReturn(REQUEST_BODY);

            // Mock HTTP response
            CloseableHttpResponse mockedResponse = mock(CloseableHttpResponse.class);
            StatusLine statusLine = mock(StatusLine.class);
            when(mockedResponse.getStatusLine()).thenReturn(statusLine);
            when(statusLine.getStatusCode()).thenReturn(0);

            httpHandlerMock.when(() -> HttpHandler.sendPOSTRequest(
                            SpiceDbApiConstants.LOOKUP_SUBJECTS, REQUEST_BODY))
                    .thenReturn(mockedResponse);

            try {
                service.searchSubjects(searchSubjectsRequest);
                fail(FAILURE_MESSAGE);
            } catch (SpicedbEvaluationException e) {
                assertEquals(e.getMessage(), "Looking up subjects from spiceDB failed. Cannot identify error code.");
            }
        }
    }

    @Test
    public void testSearchSubjectsForIOException() {

        try (MockedStatic<HttpHandler> httpHandlerMock = mockStatic(HttpHandler.class);
             MockedStatic<JsonUtil> jsonUtilMock = mockStatic(JsonUtil.class)) {

            jsonUtilMock.when(() -> JsonUtil.parseToJsonString(any(LookupSubjectsRequest.class)))
                    .thenReturn(REQUEST_BODY);

            httpHandlerMock.when(() -> HttpHandler.sendPOSTRequest(any(), any()))
                    .thenThrow(new IOException(IO_EXCEPTION_MESSAGE));

            try {
                service.searchSubjects(searchSubjectsRequest);
                fail(FAILURE_MESSAGE);
            } catch (SpicedbEvaluationException ex) {
                assertEquals(ex.getMessage(), IO_EXCEPTION_MESSAGE);
            }
        }
    }

    @Test
    public void testSearchSubjectsForURISyntaxException() {

        try (MockedStatic<HttpHandler> httpHandlerMock = mockStatic(HttpHandler.class);
             MockedStatic<JsonUtil> jsonUtilMock = mockStatic(JsonUtil.class)) {

            jsonUtilMock.when(() -> JsonUtil.parseToJsonString(any(LookupSubjectsRequest.class)))
                    .thenReturn(REQUEST_BODY);

            httpHandlerMock.when(() -> HttpHandler.sendPOSTRequest(any(), any()))
                    .thenThrow(new URISyntaxException(URI_SYNTAX_EXCEPTION_INPUT, URI_SYNTAX_EXCEPTION_REASON));

            try {
                service.searchSubjects(searchSubjectsRequest);
                fail(FAILURE_MESSAGE);
            } catch (SpicedbEvaluationException ex) {
                assertEquals(ex.getMessage(), URI_SYNTAX_EXCEPTION_REASON + ": " + URI_SYNTAX_EXCEPTION_INPUT);
            }
        }
    }

    @Test
    public void testSearchActionsWithNullRequest() {

        try {
            service.searchActions(null);
            fail(FAILURE_MESSAGE);
        } catch (SpicedbEvaluationException e) {
            assertEquals(e.getMessage(), "Invalid request. Search actions request cannot be null.");
        }
    }
}
