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

import org.wso2.carbon.identity.authorization.framework.model.AuthorizationResource;
import org.wso2.carbon.identity.authorization.framework.model.AuthorizationSubject;
import org.wso2.carbon.identity.authorization.framework.model.SearchResourcesResponse;
import org.wso2.carbon.identity.authorization.framework.model.SearchSubjectsResponse;
import org.wso2.carbon.identity.authz.spicedb.handler.util.JsonUtil;

import java.util.ArrayList;

/**
 * The {@code LookupObjectsResponseHolder} class holds the results stream returned from a Lookup subject or Lookup
 * resource response. The results stream is converted into a {@code String[]} to extract each result.
 */
public class LookupObjectsResponseHolder {

    private final String[] results;
    private final String resultType;

    public LookupObjectsResponseHolder(String response, String resultType) {

        this.results = response.split("(?<=})\\s*(?=\\{)");
        this.resultType = resultType;
    }

    public String[] getResults () {

        return results;
    }

    /**
     * Converts the response to a {@link SearchResourcesResponse} object from a lookup resources response.
     *
     * @return The {@link SearchResourcesResponse} object.
     */
    public SearchResourcesResponse toSearchResourcesResponse() {

        ArrayList<AuthorizationResource> resourceArrayList = new ArrayList<>();
        for (String result : this.results) {
            if (result.isEmpty()) {
                continue;
            }
            LookupResourcesResult lookUpResourcesResult = JsonUtil.jsonToResponseModel(result,
                    LookupResourcesResult.class);
            AuthorizationResource authorizationResource = new AuthorizationResource(this.resultType,
                    lookUpResourcesResult.getLookupResourcesResult().getResourceId());
            authorizationResource.setProperties(lookUpResourcesResult.getLookupResourcesResult()
                    .getPartialCaveatInfo());
            resourceArrayList.add(authorizationResource);
        }
        return new SearchResourcesResponse(resourceArrayList);
    }

    /**
     * Converts the response to a {@link SearchSubjectsResponse} object from a lookup subjects response.
     *
     * @return The {@link SearchSubjectsResponse} object.
     */
    public SearchSubjectsResponse toSearchSubjectsResponse() {

        ArrayList<AuthorizationSubject> listObjectsResults = new ArrayList<>();
        for (String result : this.results) {
            if (result.isEmpty()) {
                continue;
            }
            LookupSubjectsResult lookUpSubjectsResult = JsonUtil.jsonToResponseModel(result,
                    LookupSubjectsResult.class);
            AuthorizationSubject searchObjectsResult = new AuthorizationSubject(this.resultType,
                    lookUpSubjectsResult.getLookupSubjectsResult().getSubjectId());
            searchObjectsResult.setProperties(lookUpSubjectsResult.getLookupSubjectsResult().getPartialCaveatInfo());
            listObjectsResults.add(searchObjectsResult);
        }
        return new SearchSubjectsResponse(listObjectsResults);
    }
}
