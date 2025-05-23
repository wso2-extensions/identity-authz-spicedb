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

import org.testng.annotations.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * Unit test class for {@link ReflectSchemaRequest}.
 */
@Test
public class ReflectSchemaRequestTest {

    @Test
    public void testReflectSchemaRequest() throws Exception {

        ArrayList<OptionalSchemaFilter> optionalSchemaFilters = new ArrayList<>();
        optionalSchemaFilters.add(new OptionalSchemaFilter());
        optionalSchemaFilters.add(new OptionalSchemaFilter());
        ReflectSchemaRequest reflectSchemaRequest = new ReflectSchemaRequest(optionalSchemaFilters);
        Field optionalSchemaFiltersField = ReflectSchemaRequest.class.getDeclaredField("schemaFilters");
        optionalSchemaFiltersField.setAccessible(true);
        ArrayList<?> schemaFilters = (ArrayList<?>) optionalSchemaFiltersField.get(reflectSchemaRequest);

        assertNotNull(schemaFilters);
        assertEquals(schemaFilters.size(), 2);
        assertTrue(schemaFilters.get(0) instanceof OptionalSchemaFilter);
    }
}
