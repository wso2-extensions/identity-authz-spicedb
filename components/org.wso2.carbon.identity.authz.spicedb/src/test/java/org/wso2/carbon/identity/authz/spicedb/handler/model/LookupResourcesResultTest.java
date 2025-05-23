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
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;

/**
 * Test class for {@link LookupResourcesResult}.
 */
@Test
public class LookupResourcesResultTest {

    @Test
    public void testClassCreationWithJson() {

        String jsonString = "{\"result\":{\"lookedUpAt\":{\"token\":\"1111\"},\"resourceObjectId\":\"resourceId\"," +
                "\"permissionship\":\"LOOKUP_PERMISSIONSHIP_UNSPECIFIED\",\"partialCaveatInfo\":" +
                "{\"missingRequiredContext\":[\"<string>\",\"<string>\"]},\"afterResultCursor\":{\"token\":\"" +
                "<string>\"}},\"error\":{\"code\":\"<integer>\",\"message\":\"<string>\",\"details\":[{\"@type\":" +
                "\"<string>\",\"mollit59\":{}},{\"@type\":\"<string>\",\"Excepteur7\":{},\"eiusmod26c\":{}}]}}";

        LookupResourcesResult lookupResourcesResult = new Gson().fromJson(jsonString, LookupResourcesResult.class);

        assertNotNull(lookupResourcesResult);
        assertNotNull(lookupResourcesResult.getLookupResourcesResult());
        assertNotNull(lookupResourcesResult.getLookupResourcesResult().getResourceId());
    }
}
