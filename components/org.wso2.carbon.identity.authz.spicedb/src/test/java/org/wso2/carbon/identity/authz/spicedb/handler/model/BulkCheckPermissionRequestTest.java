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

import org.testng.Assert;
import org.testng.annotations.Test;
import org.wso2.carbon.identity.authorization.framework.model.AccessEvaluationRequest;
import org.wso2.carbon.identity.authorization.framework.model.AuthorizationAction;
import org.wso2.carbon.identity.authorization.framework.model.AuthorizationResource;
import org.wso2.carbon.identity.authorization.framework.model.AuthorizationSubject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit test class for {@link BulkCheckPermissionRequest}.
 */
@Test
public class BulkCheckPermissionRequestTest {

    @Test
    public void testConstructorWithNullItems() {

        try {
            new BulkCheckPermissionRequest(null);
        } catch (IllegalArgumentException e) {
            Assert.assertEquals(e.getMessage(), "Invalid request. The list of requests cannot be null or empty.");
        }
    }

    @Test
    public void testConstructorWithEmptyItems() {

        try {
            new BulkCheckPermissionRequest(new ArrayList<>());
        } catch (IllegalArgumentException e) {
            Assert.assertEquals(e.getMessage(), "Invalid request. The list of requests cannot be null or empty.");
        }
    }

    @Test
    public void testConstructorWithValidItems() throws Exception {

        ArrayList<AccessEvaluationRequest> items = new ArrayList<>();
        AuthorizationResource authorizationResource = mock(AuthorizationResource.class);
        when(authorizationResource.getResourceId()).thenReturn("resourceId");
        AuthorizationSubject authorizationSubject = mock(AuthorizationSubject.class);
        when(authorizationSubject.getSubjectId()).thenReturn("subjectId");
        AuthorizationAction authorizationAction = mock(AuthorizationAction.class);
        items.add(new AccessEvaluationRequest(authorizationSubject, authorizationAction, authorizationResource));
        BulkCheckPermissionRequest bulkCheckPermissionRequest = new BulkCheckPermissionRequest(items);
        Field field = BulkCheckPermissionRequest.class.getDeclaredField("items");
        field.setAccessible(true);
        List<?> internalItems = (List<?>) field.get(bulkCheckPermissionRequest);

        Assert.assertNotNull(internalItems);
        Assert.assertEquals(internalItems.size(), 1);
        Assert.assertTrue(internalItems.get(0) instanceof CheckPermissionRequest);
    }
}
