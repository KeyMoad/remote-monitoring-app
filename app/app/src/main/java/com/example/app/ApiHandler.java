package com.example.app;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ApiHandler {
    private final RequestQueue requestQueue;
    private final DatabaseHelper databaseHelper;
    private final String username;

    public ApiHandler(Context context, String username) {
        this.requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        this.databaseHelper = new DatabaseHelper(context.getApplicationContext());
        this.username = username;
    }

    public void api(String path, HttpMethod method, Map<String, String> headers, Map<String, String> params, String requestBody, ResponseCallback callback) {
        String url = getCompleteUrl(path, this.username);

        // Create the appropriate type of request based on the specified HttpMethod
        Request<?> request = null;
        switch (method) {
            case GET:
                url = addParamsToUrl(url, params);
                request = new StringRequest(Request.Method.GET, url,
                        callback::onSuccess,
                        error -> callback.onError(error.toString()))
                {
                    @Override
                    public Map<String, String> getHeaders() {
                        return headers;
                    }
                };
                break;
            case POST:
                request = new StringRequest(Request.Method.POST, url,
                        callback::onSuccess,
                        error -> callback.onError(convertVolleyErrorToString(error))) {
                    @Override
                    public byte[] getBody() {
                        return requestBody != null ? requestBody.getBytes() : null;
                    }

                    @Override
                    public String getBodyContentType() {
                        return "application/json";
                    }

                    @Override
                    public Map<String, String> getHeaders() {
                        return headers;
                    }
                };
                break;
            case DELETE:
                url = addParamsToUrl(url, params);
                request = new StringRequest(Request.Method.DELETE, url,
                        callback::onSuccess,
                        error -> callback.onError(error.toString()))
                {
                    @Override
                    public Map<String, String> getHeaders() {
                        return headers;
                    }
                };
                break;
        }

        if (request != null) {
            // Add the request to the Volley request queue
            requestQueue.add(request);
        }
    }

    public void apiWithToken(String path, HttpMethod method, Map<String, String> params, String requestBody, ResponseCallback callback) {
        // Fetch the passcode first
        String passcode = databaseHelper.getPasscodeByUsername(username);

        if (passcode == null || passcode.isEmpty()) {
            callback.onError("Passcode not available");
            return;
        }


        // Use HashMap for mutable headers
        final Map<String, String> headers = new HashMap<>();

        // Now, use the passcode in the token request
        api("/token", HttpMethod.POST, headers, null, "{\"passcode\":\"" + passcode + "\"}", new ResponseCallback() {
            @Override
            public void onSuccess(String tokenResponse) {
                try {
                    // Parse the JSON response to extract the token
                    JSONObject jsonResponse = new JSONObject(tokenResponse);
                    String token = jsonResponse.getJSONObject("data").getString("token");

                    // Create headers for the subsequent API call
                    headers.put("Authorization", "Bearer " + token);

                    // Use the fetched token in the subsequent API call
                    api(path, method, headers, params, requestBody, callback);
                } catch (JSONException e) {
                    callback.onError("Error parsing JSON");
                }
            }

            @Override
            public void onError(String error) {
                // Handle error while fetching the token
                callback.onError(error);
            }
        });
    }

    private String addParamsToUrl(String url, Map<String, String> params) {
        if (params != null && !params.isEmpty()) {
            StringBuilder paramString = new StringBuilder();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (paramString.length() > 0) {
                    paramString.append("&");
                }
                paramString.append(entry.getKey()).append("=").append(entry.getValue());
            }
            url = url + "?" + paramString.toString();
        }
        return url;
    }

    private String getCompleteUrl(String path, String username) {
        String host = databaseHelper.getHostByUsername(username);
        return host + path;
    }

    private String convertVolleyErrorToString(VolleyError error) {
        return (error != null && error.getMessage() != null) ? error.getMessage() : "Unknown error";
    }

    // Define an interface for handling API responses
    public interface ResponseCallback {
        void onSuccess(String response);
        void onError(String error);
    }

    // Enum to represent HTTP methods
    public enum HttpMethod {
        GET,
        POST,
        DELETE
    }
}
