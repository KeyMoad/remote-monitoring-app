// MainActivity.java
package com.example.app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private TextView loadAverageTextView;
    private TextView cpuLoadTextView;
    private TextView memoryUsageTextView;
    private DatabaseHelper dbHelper;
    private ApiHandler apiHandler;
    private Logger logger;
    private final Handler handler = new Handler();
    private final long delayMillis = 15 * 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find the CardViews
        CardView serviceSectionCardView = findViewById(R.id.section_action);
        CardView cronSectionCardView = findViewById(R.id.section_cron);
        CardView deleteSectionCardView = findViewById(R.id.section_delete);
        CardView historySectionCardView = findViewById(R.id.section_history);

        // Replace these with the actual IDs from your XML layout
        loadAverageTextView = findViewById(R.id.load_average_textview);
        cpuLoadTextView = findViewById(R.id.cpu_load_textview);
        memoryUsageTextView = findViewById(R.id.memory_usage_textview);

        // Initialize ApiHandler with the username
        String loggedInUsername = getSharedPreferences("MyPrefs", MODE_PRIVATE).getString("username", null);
        if (loggedInUsername == null) {
            Toast.makeText(MainActivity.this, "Username not available", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        logger = new Logger(loggedInUsername, getApplicationContext());
        apiHandler = new ApiHandler(this, loggedInUsername);

        // Set up listeners for CardViews
        serviceSectionCardView.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ServiceActivity.class);
            startActivity(intent);
        });

        cronSectionCardView.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CronActivity.class);
            startActivity(intent);
        });

        deleteSectionCardView.setOnClickListener(v -> {
            dbHelper = new DatabaseHelper(this);
            dbHelper.deleteUser(loggedInUsername);

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);

            Toast.makeText(MainActivity.this, "Deletion succeed !", Toast.LENGTH_SHORT).show();
        });

        historySectionCardView.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(intent);
        });

        // Schedule periodic fetching after the initial fetch
        long initialDelayMillis = 0;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                fetchCpuLoadAvg();
                fetchMemoryUsage();
                fetchLoadAverage();
                handler.postDelayed(this, delayMillis);
            }
        }, initialDelayMillis);
    }

    private void fetchCpuLoadAvg() {
        apiHandler.apiWithToken("/metrics/cpu_load", ApiHandler.HttpMethod.GET, null, null, new ApiHandler.ResponseCallback() {
            @Override
            public void onSuccess(String response) {
                updateCpuLoadTextView(response);

                logger.writeLog("Info", "Fetched CPU load successfully");
            }

            @Override
            public void onError(String error) {
                handleApiError("Metrics Error", error);

                logger.writeLog("Error", "Error fetching CPU load: " + error);
            }
        });
    }

    private void updateCpuLoadTextView(String response) {
        try {
            JSONObject jsonResponse = new JSONObject(response);
            if (jsonResponse.has("detail") && jsonResponse.getJSONObject("detail").has("data")) {
                Object cpuLoadObj = jsonResponse.getJSONObject("detail").getDouble("data");
                String cpuLoad = String.valueOf(cpuLoadObj);
                String cpuLoadText = String.format("CPU Load: %s", cpuLoad);
                cpuLoadTextView.setText(cpuLoadText);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void fetchMemoryUsage() {
        apiHandler.apiWithToken("/metrics/memory_usage", ApiHandler.HttpMethod.GET, null, null, new ApiHandler.ResponseCallback() {
            @Override
            public void onSuccess(String response) {
                updateMemoryUsageTextView(response);

                logger.writeLog("Info", "Fetched Memory usage successfully");
            }

            @Override
            public void onError(String error) {
                handleApiError("Memory Usage Error", error);

                logger.writeLog("Error", "Error fetching Memory usage: " + error);
            }
        });
    }

    private void updateMemoryUsageTextView(String response) {
        try {
            JSONObject jsonResponse = new JSONObject(response);
            if (jsonResponse.has("detail") && jsonResponse.getJSONObject("detail").has("data")) {
                JSONObject memoryData = jsonResponse.getJSONObject("detail").getJSONObject("data");
                long totalMemoryBytes = memoryData.getLong("total");
                long usedMemoryBytes = memoryData.getLong("used");
                long freeMemoryBytes = memoryData.getLong("free");

                // Convert bytes to megabytes
                double totalMemoryMB = (double) totalMemoryBytes / (1024 * 1024 * 1024);
                double usedMemoryMB = (double) usedMemoryBytes / (1024 * 1024 * 1024);
                double freeMemoryMB = (double) freeMemoryBytes / (1024 * 1024 * 1024);

                @SuppressLint("DefaultLocale")
                String memoryUsageText = String.format("Total  -  %.2f GB | Used  -  %.2f GB | Free  -  %.2f GB", totalMemoryMB, usedMemoryMB, freeMemoryMB);
                memoryUsageTextView.setText(memoryUsageText);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void fetchLoadAverage() {
        apiHandler.apiWithToken("/metrics/load_average", ApiHandler.HttpMethod.GET, null, null, new ApiHandler.ResponseCallback() {
            @Override
            public void onSuccess(String response) {
                updateLoadAverageTextView(response);

                logger.writeLog("Info", "Fetched Load average successfully");
            }

            @Override
            public void onError(String error) {
                handleApiError("Load Average Error", error);

                logger.writeLog("Error", "Error fetching Load average: " + error);
            }
        });
    }

    private void updateLoadAverageTextView(String response) {
        try {
            JSONObject jsonResponse = new JSONObject(response);
            if (jsonResponse.has("detail") && jsonResponse.getJSONObject("detail").has("data")) {
                JSONObject loadAverageData = jsonResponse.getJSONObject("detail").getJSONObject("data");
                double load1 = loadAverageData.getDouble("load1");
                double load5 = loadAverageData.getDouble("load5");
                double load15 = loadAverageData.getDouble("load15");
                @SuppressLint("DefaultLocale") String loadAverageText = String.format("1min  -  %.2f | 5min  -  %.2f | 15min  -  %.2f", load1, load5, load15);
                loadAverageTextView.setText(loadAverageText);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void handleApiError(String prefix, String error) {
        Toast.makeText(getApplicationContext(), prefix + ": " + error, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onPause() {
        // Remove callbacks when the activity is not visible
        handler.removeCallbacksAndMessages(null);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Resume periodic fetching when the activity becomes visible
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isDestroyed()) {
                    fetchCpuLoadAvg();
                    fetchMemoryUsage();
                    fetchLoadAverage();
                    handler.postDelayed(this, delayMillis);
                }
            }
        }, delayMillis);
    }

    @Override
    protected void onDestroy() {
        // Remove callbacks to prevent memory leaks when the activity is destroyed
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
