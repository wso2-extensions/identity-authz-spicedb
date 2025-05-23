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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Unit test class for{@link OptionalSchemaFilter}.
 */
@Test
public class OptionalSchemaFilterTest {

    @Test
    public void testOptionalSchemaFilter() throws Exception {
        OptionalSchemaFilter optionalSchemaFilter = new OptionalSchemaFilter();
        optionalSchemaFilter.setOptionalRelationNameFilter("test relation");
        optionalSchemaFilter.setOptionalDefinitionNameFilter("test definition");
        optionalSchemaFilter.setOptionalCaveatNameFilter("test caveat");
        optionalSchemaFilter.setOptionalPermissionNameFilter("test permission");

        Field relationNameFilter = OptionalSchemaFilter.class.getDeclaredField("optionalRelationNameFilter");
        relationNameFilter.setAccessible(true);
        Field definitionNameFilter = OptionalSchemaFilter.class.getDeclaredField("optionalDefinitionNameFilter");
        definitionNameFilter.setAccessible(true);
        Field caveatNameFilter = OptionalSchemaFilter.class.getDeclaredField("optionalCaveatNameFilter");
        caveatNameFilter.setAccessible(true);
        Field permissionNameFilter = OptionalSchemaFilter.class.getDeclaredField("optionalPermissionNameFilter");
        permissionNameFilter.setAccessible(true);

        assertNotNull(optionalSchemaFilter);
        assertEquals(relationNameFilter.get(optionalSchemaFilter), "test relation");
        assertEquals(definitionNameFilter.get(optionalSchemaFilter), "test definition");
        assertEquals(caveatNameFilter.get(optionalSchemaFilter), "test caveat");
        assertEquals(permissionNameFilter.get(optionalSchemaFilter), "test permission");
    }

}
