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
import org.wso2.carbon.identity.authorization.framework.model.SearchSubjectsRequest;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Unit test for {@link LookupResourcesResultBody}.
 */
@Test
public class LookupSubjectsRequestTest {

    private SearchSubjectsRequest searchSubjectsRequest;
    private LookupSubjectsRequest lookupSubjectsRequest;

    @BeforeMethod
    public void setUp() {

        searchSubjectsRequest = new SearchSubjectsRequest(
                new AuthorizationSubject("subjectType"),
                new AuthorizationAction("action"),
                new AuthorizationResource("resourceType", "resourceId"));
        lookupSubjectsRequest = new LookupSubjectsRequest(searchSubjectsRequest);
    }

    @Test
    public void testConstructorWithNullRequest() {

        try {
            LookupSubjectsRequest lookupSubjectsRequest = new LookupSubjectsRequest(
                    new SearchSubjectsRequest(null, null, null));
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "Invalid request. Resource, action, and subject with type" +
                    " must be provided to lookup subjects.");
        }
    }

    @Test
    public void testConstructorWithNullResourceId() {

        try {
            LookupSubjectsRequest lookupSubjectsRequest = new LookupSubjectsRequest(
                    new SearchSubjectsRequest(mock(AuthorizationSubject.class), mock(AuthorizationAction.class), mock(
                            AuthorizationResource.class)));
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "Invalid request. Resource id must be provided to lookup subjects.");
        }
    }

    @Test
    public void testConstructorWithValidRequest() throws Exception {


        Field resource = lookupSubjectsRequest.getClass().getDeclaredField("resource");
        resource.setAccessible(true);
        Resource resourceField = (Resource) resource.get(lookupSubjectsRequest);
        Field permission = lookupSubjectsRequest.getClass().getDeclaredField("permission");
        permission.setAccessible(true);
        Field subjectType = lookupSubjectsRequest.getClass().getDeclaredField("subjectType");
        subjectType.setAccessible(true);

        assertNotNull(lookupSubjectsRequest);
        assertNotNull(resourceField);
        assertEquals(resourceField.getResourceId(), searchSubjectsRequest.getResource().getResourceId());
        assertEquals(resourceField.getResourceType(), searchSubjectsRequest.getResource().getResourceType());
        assertEquals(permission.get(lookupSubjectsRequest), searchSubjectsRequest.getAction().getAction());
        assertEquals(subjectType.get(lookupSubjectsRequest), searchSubjectsRequest.getSubject().getSubjectType());
    }

    @Test
    public void testSetMethods() throws Exception {

        Map<String, Object> context = new HashMap<>();
        context.put("key", "value");
        lookupSubjectsRequest.setContext(context);
        lookupSubjectsRequest.setOptionalSubjectRelation("optionalSubjectRelation");
        lookupSubjectsRequest.setOptionalCursor("optionalCursor");
        lookupSubjectsRequest.setOptionalConcreteLimit(10L);
        lookupSubjectsRequest.setWildCardOption("wildCardOption");

        Field contextField = lookupSubjectsRequest.getClass().getDeclaredField("context");
        contextField.setAccessible(true);
        Field optionalSubjectRelationField = lookupSubjectsRequest.getClass()
                .getDeclaredField("optionalSubjectRelation");
        optionalSubjectRelationField.setAccessible(true);
        Field optionalCursorField = lookupSubjectsRequest.getClass().getDeclaredField("optionalCursor");
        optionalCursorField.setAccessible(true);
        Field optionalConcreteLimitField = lookupSubjectsRequest.getClass().getDeclaredField("optionalConcreteLimit");
        optionalConcreteLimitField.setAccessible(true);
        Field wildCardOptionField = lookupSubjectsRequest.getClass().getDeclaredField("wildCardOption");
        wildCardOptionField.setAccessible(true);

        assertEquals(contextField.get(lookupSubjectsRequest), context);
        assertEquals(optionalSubjectRelationField.get(lookupSubjectsRequest), "optionalSubjectRelation");
        assertEquals(optionalCursorField.get(lookupSubjectsRequest), "optionalCursor");
        assertEquals(optionalConcreteLimitField.get(lookupSubjectsRequest), 10L);
        assertEquals(wildCardOptionField.get(lookupSubjectsRequest), "wildCardOption");

    }
}
