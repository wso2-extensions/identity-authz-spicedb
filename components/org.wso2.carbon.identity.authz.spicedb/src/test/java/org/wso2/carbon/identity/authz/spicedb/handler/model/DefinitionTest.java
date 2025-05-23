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
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Unit test class for {@code Definition} class.
 */
@Test
public class DefinitionTest {

    private Definition definition;

    @BeforeMethod
    public void setUp() {
        String json = "{\"name\":\"record\",\"comment\":\"\",\"relations\":[{\"name\":\"parent_org\",\"comment\":" +
                "\"\",\"parentDefinitionName\":\"record\",\"subjectTypes\":[{\"subjectDefinitionName\":" +
                "\"organization\",\"optionalCaveatName\":\"\",\"isTerminalSubject\":false}]}],\"permissions\":" +
                "[{\"name\":\"view\",\"comment\":\"\",\"parentDefinitionName\":\"record\"},{\"name\":\"edit\"," +
                "\"comment\":\"\",\"parentDefinitionName\":\"record\"}],\"caveats\":[],\"readAt\":{\"token\":" +
                "\"GhUKEzE3NDU4MjU5Mjc0MjY4MTA2Mzc=\"}}";

        this.definition = new Gson().fromJson(json, Definition.class);
    }

    @Test
    public void testClassCreationWithJson() {

        assertNotNull(definition);
        assertNotNull(definition.getDefinitionName());
        assertNotNull(definition.getDefinitionComment());
        assertNotNull(definition.getPermissions());
        assertNotNull(definition.getRelations());
    }

    @Test
    public void testGetPermissionNames() {

        ArrayList<String> expectedPermissions = new ArrayList<>();
        expectedPermissions.add("view");
        expectedPermissions.add("edit");
        assertEquals(definition.getPermissionNames(), expectedPermissions);
    }
}
