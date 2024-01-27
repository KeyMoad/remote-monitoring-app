package com.example.app;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class ApiHandler {
    private final RequestQueue requestQueue;
    private final DatabaseHelper databaseHelper;
    private final String username;

    public ApiHandler(Context context, String username) {
        this.requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        this.databaseHelper = new DatabaseHelper(context.getApplicationContext());
        this.username = username;
    }

    public void api(String path, ResponseCallback callback) {
        String url = getCompleteUrl(path, this.username);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    // Print the received JSON response for debugging
                    Log.d("ApiHandler", "Received JSON: " + response.toString());

                    // Pass the JSON response to the callback
                    callback.onSuccess(response.toString());
                },
                error -> callback.onError(error.toString()));

        requestQueue.add(jsonObjectRequest);
    }

    public void apiWithToken(String path, ResponseCallback callback) {
        // Fetch the token first using the api method
        api("/token", new ResponseCallback() {
            @Override
            public void onSuccess(String tokenResponse) {
                try {
                    // Parse the JSON response to extract the token
                    JSONObject jsonResponse = new JSONObject(tokenResponse);
                    String token = jsonResponse.getJSONObject("data").getString("token");

                    // Use the fetched token in the subsequent API call
                    String url = getCompleteUrl(path, username);

                    StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                            callback::onSuccess,
                            error -> callback.onError(error.toString())) {
                        // Override this method to include headers
                        @Override
                        public java.util.Map<String, String> getHeaders() {
                            java.util.Map<String, String> headers = new java.util.HashMap<>();
                            headers.put("Authorization", "Bearer " + token);
                            return headers;
                        }
                    };
                    requestQueue.add(stringRequest);
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

    private String getCompleteUrl(String path, String username) {
        String host = databaseHelper.getHostByUsername(username);
        return host + path;
    }

    // Define an interface for handling API responses
    public interface ResponseCallback {
        void onSuccess(String response);
        void onError(String error);
    }
}
