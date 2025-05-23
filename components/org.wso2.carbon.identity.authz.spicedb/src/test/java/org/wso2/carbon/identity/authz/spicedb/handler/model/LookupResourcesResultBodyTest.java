package org.wso2.carbon.identity.authz.spicedb.handler.model;

import com.google.gson.Gson;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Unit test for {@link LookupResourcesResultBody}.
 */
@Test
public class LookupResourcesResultBodyTest {

    @Test
    public void testClassCreationWithJson() {

        String json = "{\"lookedUpAt\":{\"token\":\"1111\"},\"resourceObjectId\":\"resourceId\",\"permissionship\":" +
                "\"LOOKUP_PERMISSIONSHIP_UNSPECIFIED\",\"partialCaveatInfo\":{\"missingRequiredContext\":" +
                "[\"<string>\",\"<string>\"]},\"afterResultCursor\":{\"token\":\"<string>\"}}\n";

        LookupResourcesResultBody lookupResourcesResultBody = new Gson().fromJson(json,
                LookupResourcesResultBody.class);

        assertNotNull(lookupResourcesResultBody);
        assertNotNull(lookupResourcesResultBody.getResourceId());
        assertNotNull(lookupResourcesResultBody.lookedAt());
        assertNotNull(lookupResourcesResultBody.getPermissionship());
        assertNotNull(lookupResourcesResultBody.getPartialCaveatInfo());
        assertNotNull(lookupResourcesResultBody.getAfterResultCursor());
        assertEquals(lookupResourcesResultBody.getResourceId(), "resourceId");
    }
}
