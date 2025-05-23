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

import com.google.gson.Gson;
import org.mockito.MockedStatic;
import org.testng.annotations.Test;
import org.wso2.carbon.identity.authorization.framework.model.SearchResourcesResponse;
import org.wso2.carbon.identity.authorization.framework.model.SearchSubjectsResponse;
import org.wso2.carbon.identity.authz.spicedb.handler.util.JsonUtil;

import static org.mockito.Mockito.mockStatic;
import static org.testng.Assert.assertEquals;

/**
 * Unit test for {@link LookupObjectsResponseHolder}.
 */
@Test
public class LookupObjectsResponseHolderTest {

    @Test
    public void testConstructorWithValidResults() {

        String validResponse = "{\"result\":{\"lookedUpAt\":{\"token\":\"1111\"},\"resourceObjectId\":\"resourceId\"" +
                ",\"permissionship\":\"LOOKUP_PERMISSIONSHIP_UNSPECIFIED\",\"partialCaveatInfo\":" +
                "{\"missingRequiredContext\":[\"<string>\",\"<string>\"]},\"afterResultCursor\":{\"token\":\"" +
                "<string>\"}},\"error\":{\"code\":\"<integer>\",\"message\":\"<string>\",\"details\":[{\"@type\":" +
                "\"<string>\",\"mollit59\":{}},{\"@type\":\"<string>\",\"Excepteur7\":{},\"eiusmod26c\":{}}]}}" +
                "{\"result\":{\"lookedUpAt\":{\"token\":\"2222\"},\"resourceObjectId\":\"resourceId\"," +
                "\"permissionship\":\"LOOKUP_PERMISSIONSHIP_UNSPECIFIED\",\"partialCaveatInfo\":" +
                "{\"missingRequiredContext\":[\"<string>\",\"<string>\"]},\"afterResultCursor\":{\"token\":\"" +
                "<string>\"}},\"error\":{\"code\":\"<integer>\",\"message\":\"<string>\",\"details\":[{\"@type\":" +
                "\"<string>\",\"mollit59\":{}},{\"@type\":\"<string>\",\"Excepteur7\":{},\"eiusmod26c\":{}}]}}";
        String validResultType = "object";
        LookupObjectsResponseHolder lookupObjectsResponseHolder = new LookupObjectsResponseHolder(validResponse,
                validResultType);

        assertEquals(lookupObjectsResponseHolder.getResults().length, 2);
    }

    public void testConstructorWithEmptyResults() {

        String emptyResponse = "";
        String emptyResultType = "object";
        LookupObjectsResponseHolder lookupObjectsResponseHolder = new LookupObjectsResponseHolder(emptyResponse,
                emptyResultType);

        assertEquals(lookupObjectsResponseHolder.getResults().length, 1);
        assertEquals(lookupObjectsResponseHolder.getResults()[0], "");
    }

    public void testToSearchResourcesResponse() {

        try (MockedStatic<JsonUtil> jsonUtilMock = mockStatic(JsonUtil.class)) {
            String response = "{\"result\":{\"lookedUpAt\":{\"token\":\"1111\"},\"resourceObjectId\":\"resourceId\"," +
                    "\"permissionship\":\"LOOKUP_PERMISSIONSHIP_UNSPECIFIED\",\"partialCaveatInfo\":" +
                    "{\"missingRequiredContext\":[\"<string>\",\"<string>\"]},\"afterResultCursor\":{\"token\":\"" +
                    "<string>\"}},\"error\":{\"code\":\"<integer>\",\"message\":\"<string>\",\"details\":[{\"@type\":" +
                    "\"<string>\",\"mollit59\":{}},{\"@type\":\"<string>\",\"Excepteur7\":{},\"eiusmod26c\":{}}]}}";
            String resultType = "object";
            LookupObjectsResponseHolder lookupObjectsResponseHolder = new LookupObjectsResponseHolder(response,
                    resultType);
            LookupResourcesResult lookupResourcesResult = new Gson().fromJson(response, LookupResourcesResult.class);
            jsonUtilMock.when(() -> JsonUtil.jsonToResponseModel(response, LookupResourcesResult.class))
                    .thenReturn(lookupResourcesResult);

            SearchResourcesResponse searchResourcesResponse = lookupObjectsResponseHolder.toSearchResourcesResponse();

            assertEquals(searchResourcesResponse.getResults().size(), 1);
            assertEquals(searchResourcesResponse.getResults().get(0).getResourceId(), "resourceId");
            assertEquals(searchResourcesResponse.getResults().get(0).getResourceType(), "object");
        }
    }

    public void testToSearchResourcesResponseWithEmptyResults() {

        String emptyResponse = "";
        String emptyResultType = "object";
        LookupObjectsResponseHolder lookupObjectsResponseHolder = new LookupObjectsResponseHolder(emptyResponse,
                emptyResultType);

        SearchResourcesResponse searchResourcesResponse = lookupObjectsResponseHolder.toSearchResourcesResponse();

        assertEquals(searchResourcesResponse.getResults().size(), 0);
    }

    public void testToSearchSubjectsResponse() {

        try (MockedStatic<JsonUtil> jsonUtilMock = mockStatic(JsonUtil.class)) {
            String response = "{\"result\":{\"lookedUpAt\":{\"token\":\"1111\"},\"subjectObjectId\":\"subjectId\"," +
                    "\"excludedSubjectIds\":[\"<string>\",\"<string>\"],\"permissionship\":" +
                    "\"LOOKUP_PERMISSIONSHIP_UNSPECIFIED\",\"partialCaveatInfo\":{\"missingRequiredContext\":" +
                    "[\"<string>\",\"<string>\"]},\"subject\":{\"subjectObjectId\":\"<string>\",\"permissionship\":" +
                    "\"LOOKUP_PERMISSIONSHIP_UNSPECIFIED\",\"partialCaveatInfo\":{\"missingRequiredContext\":" +
                    "[\"<string>\",\"<string>\"]}},\"excludedSubjects\":[{\"subjectObjectId\":\"<string>\"," +
                    "\"permissionship\":\"LOOKUP_PERMISSIONSHIP_UNSPECIFIED\",\"partialCaveatInfo\":" +
                    "{\"missingRequiredContext\":[\"<string>\",\"<string>\"]}}," +
                    "{\"subjectObjectId\":\"<string>\",\"permissionship\":\"LOOKUP_PERMISSIONSHIP_UNSPECIFIED\"," +
                    "\"partialCaveatInfo\":{\"missingRequiredContext\":[\"<string>\",\"<string>\"]}}]," +
                    "\"afterResultCursor\":{\"token\":\"<string>\"}}}";
            String resultType = "object";
            LookupObjectsResponseHolder lookupObjectsResponseHolder = new LookupObjectsResponseHolder(response,
                    resultType);
            LookupSubjectsResult lookupSubjectsResult = new Gson().fromJson(response, LookupSubjectsResult.class);
            jsonUtilMock.when(() -> JsonUtil.jsonToResponseModel(response, LookupSubjectsResult.class))
                    .thenReturn(lookupSubjectsResult);

            SearchSubjectsResponse searchSubjectsResponse = lookupObjectsResponseHolder.toSearchSubjectsResponse();

            assertEquals(searchSubjectsResponse.getResults().size(), 1);
            assertEquals(searchSubjectsResponse.getResults().get(0).getSubjectId(), "subjectId");
            assertEquals(searchSubjectsResponse.getResults().get(0).getSubjectType(), "object");
        }
    }

    public void testToSearchSubjectsResponseWithEmptyResults() {

        String emptyResponse = "";
        String emptyResultType = "object";
        LookupObjectsResponseHolder lookupObjectsResponseHolder = new LookupObjectsResponseHolder(emptyResponse,
                emptyResultType);

        SearchSubjectsResponse searchSubjectsResponse = lookupObjectsResponseHolder.toSearchSubjectsResponse();

        assertEquals(searchSubjectsResponse.getResults().size(), 0);
    }
}
