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

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.wso2.carbon.identity.authorization.framework.model.AuthorizationAction;
import org.wso2.carbon.identity.authorization.framework.model.AuthorizationResource;
import org.wso2.carbon.identity.authorization.framework.model.AuthorizationSubject;
import org.wso2.carbon.identity.authorization.framework.model.SearchResourcesRequest;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Unit test class for {@link LookupResourcesRequest}.
 */
@Test
public class LookupResourcesRequestTest {

    private SearchResourcesRequest searchResourcesRequest;
    private LookupResourcesRequest lookupResourcesRequest;

    @BeforeMethod
    public void setUp() {

        searchResourcesRequest = new SearchResourcesRequest(
                new AuthorizationResource("resourceType"),
                new AuthorizationAction("action"),
                new AuthorizationSubject("subjectType", "subjectId"));
        lookupResourcesRequest = new LookupResourcesRequest(searchResourcesRequest);
    }

    @Test
    public void testConstructorWithNullItems() {

        try {
            LookupResourcesRequest lookupResourcesRequest = new LookupResourcesRequest(
                    new SearchResourcesRequest(null, null, null));
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "Invalid request. Resource with type, action," +
                    " and subject must be provided to lookup resources.");
        }
    }

    @Test
    public void testConstructorWithNullResourceId() {

        try {
            LookupResourcesRequest lookupResourcesRequest = new LookupResourcesRequest(
                    new SearchResourcesRequest(mock(AuthorizationResource.class), mock(AuthorizationAction.class), mock(
                            AuthorizationSubject.class)));
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "Invalid request. Subject id must be provided to lookup resources.");
        }
    }

    @Test
    public void testConstructorWithValidRequest() throws Exception {

        Field subject = lookupResourcesRequest.getClass().getDeclaredField("subject");
        subject.setAccessible(true);
        Subject subjectField = (Subject) subject.get(lookupResourcesRequest);
        Field permission = lookupResourcesRequest.getClass().getDeclaredField("permission");
        permission.setAccessible(true);
        Field resourceType = lookupResourcesRequest.getClass().getDeclaredField("resourceType");
        resourceType.setAccessible(true);
        Field subjectObject = subjectField.getClass().getDeclaredField("subjectObject");
        subjectObject.setAccessible(true);
        SubjectDetails subjectDetails = (SubjectDetails) subjectObject.get(subjectField);

        assertNotNull(lookupResourcesRequest);
        assertNotNull(subjectField);
        assertNotNull(subjectDetails);
        assertEquals(subjectDetails.getSubjectId(), searchResourcesRequest.getSubject().getSubjectId());
        assertEquals(subjectDetails.getSubjectType(), searchResourcesRequest.getSubject().getSubjectType());
        assertEquals(permission.get(lookupResourcesRequest), searchResourcesRequest.getAction().getAction());
        assertEquals(resourceType.get(lookupResourcesRequest), searchResourcesRequest.getResource().getResourceType());
    }

    @Test
    public void testSetMethods() throws Exception {

        Map<String, Object> context = new HashMap<>();
        context.put("key", "value");
        lookupResourcesRequest.setContext(context);
        lookupResourcesRequest.setOptionalCursor("optionalCursor");
        lookupResourcesRequest.setOptionalLimit(10L);
        lookupResourcesRequest.setOptionalRelation("optionalRelation");

        Field optionalCursorField = lookupResourcesRequest.getClass().getDeclaredField("optionalCursor");
        optionalCursorField.setAccessible(true);
        Field optionalLimitField = lookupResourcesRequest.getClass().getDeclaredField("optionalLimit");
        optionalLimitField.setAccessible(true);
        Field subjectField = lookupResourcesRequest.getClass().getDeclaredField("subject");
        subjectField.setAccessible(true);
        Subject subject = (Subject) subjectField.get(lookupResourcesRequest);


        assertEquals(lookupResourcesRequest.getContext(), context);
        assertEquals(optionalCursorField.get(lookupResourcesRequest), "optionalCursor");
        assertEquals(optionalLimitField.get(lookupResourcesRequest), 10L);
        assertEquals(subject.getOptionalRelation(), "optionalRelation");
    }

}
