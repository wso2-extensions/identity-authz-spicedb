/*
 * Copyright (c) 2024, WSO2 LLC. (http://www.wso2.com).
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

package org.wso2.carbon.identity.authz.spicedb.handler.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;
import org.wso2.carbon.identity.authz.spicedb.constants.SpiceDbConstants;
import org.wso2.carbon.identity.oauth2.fga.HTTPHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * This class is to create and send HTTP requests and receive responses.
 */
public class SpiceDbHttpHandler extends HTTPHandler {

    @Override
    public HttpResponse sendGETRequest (String url) throws IOException {

        HttpResponse response;
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            HttpGet httpGet = new HttpGet(url);
            response = httpClient.execute(httpGet);
        }
        return response;
    }

    @Override
    public HttpResponse sendPOSTRequest (String url, JSONObject jsonObject) throws IOException {

        HttpResponse response;
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Authorization", SpiceDbConstants.PRE_SHARED_KEY);
            httpPost.setEntity(new StringEntity(jsonObject.toString()));
            response = httpClient.execute(httpPost);
        }
        return response;
    }

    @Override
    public String parseToString (HttpResponse response) throws IOException {

        HttpEntity entity = response.getEntity();
        InputStream stream = entity.getContent();
        try (BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            StringBuilder stringBuilder = new StringBuilder();
            String inputLine = bufferedReader.readLine();
            while (inputLine != null) {
                stringBuilder.append(inputLine).append("\n");
                inputLine = bufferedReader.readLine();
            }
            return stringBuilder.toString();
        }
    }
}
