// APIHandler.java
package com.example.app;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class APIHandler {

    public interface MetricsCallback {
        void onMetricsFetched(JSONObject metrics);
    }

    private MetricsCallback callback;

    public APIHandler(MetricsCallback callback) {
        this.callback = callback;
    }

    public void fetchMetrics() {
        new FetchMetricsTask().execute();
    }

    private class FetchMetricsTask extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(Void... voids) {
            try {
                // Replace with your API base URL from the database
                String apiUrl = "http://0.0.0.0:9932";

                // Fetch load average
                URL loadAverageUrl = new URL(apiUrl + "/metrics/load_average");
                String loadAverageResponse = fetchDataFromApi(loadAverageUrl);

                // Fetch CPU load
                URL cpuLoadUrl = new URL(apiUrl + "/metrics/cpu_load");
                String cpuLoadResponse = fetchDataFromApi(cpuLoadUrl);

                // Fetch memory usage
                URL memoryUsageUrl = new URL(apiUrl + "/metrics/memory_usage");
                String memoryUsageResponse = fetchDataFromApi(memoryUsageUrl);

                // Parse JSON responses
                JSONObject loadAverageJson = new JSONObject(loadAverageResponse);
                JSONObject cpuLoadJson = new JSONObject(cpuLoadResponse);
                JSONObject memoryUsageJson = new JSONObject(memoryUsageResponse);

                // Combine results into a single JSONObject
                JSONObject result = new JSONObject();
                result.put("load_average", loadAverageJson.getJSONObject("data"));
                result.put("cpu_load", cpuLoadJson.getJSONObject("detail").getDouble("data"));
                result.put("memory_usage", memoryUsageJson.getJSONObject("detail").getJSONObject("data"));

                return result;
            } catch (IOException | JSONException e) {
                Log.e("APIHandler", "Error fetching metrics: " + e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            if (callback != null) {
                callback.onMetricsFetched(result);
            }
        }

        private String fetchDataFromApi(URL url) throws IOException {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");

                // Read the input stream into a String
                StringBuilder buffer = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }

                return buffer.toString();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
