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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Unit test class for {@link ReflectSchemaResponse}.
 */
@Test
public class ReflectSchemaResponseTest {

    @Test
    public void testClassCreationWithJson() {

        String json = "{\"definitions\": [{\"name\": \"definition\",\"comment\": \"\",\"relations\": [{\"name\": " +
                "\"relation\",\"comment\": \"\",\"parentDefinitionName\": \"definition\",\"subjectTypes\": [" +
                "{\"subjectDefinitionName\": \"definition\",\"optionalCaveatName\": \"\",\"isTerminalSubject\": " +
                "false}]}],\"permissions\": [{\"name\": \"permission\",\"comment\": \"\",\"parentDefinitionName\": " +
                "\"definition\"}]}]}";

        ReflectSchemaResponse reflectSchemaResponse = new Gson().fromJson(json, ReflectSchemaResponse.class);

        assertNotNull(reflectSchemaResponse);
        assertNotNull(reflectSchemaResponse.getDefinitions());
        assertEquals(reflectSchemaResponse.getDefinitions().size(), 1);
        assertNotNull(reflectSchemaResponse.getDefinitions().get(0));
    }

}
