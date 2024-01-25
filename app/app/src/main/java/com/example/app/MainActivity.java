// MainActivity.java
package com.example.app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements APIHandler.MetricsCallback {

    private APIHandler apiHandler;

    // Replace these with the actual IDs from your XML layout
    private TextView loadAverageTextView;
    private TextView cpuLoadTextView;
    private TextView memoryUsageTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        apiHandler = new APIHandler(this);

        // Find the CardViews
        CardView serviceSectionCardView = findViewById(R.id.section_action);
        CardView cronSectionCardView = findViewById(R.id.section_cron);
        CardView deleteSectionCardView = findViewById(R.id.section_delete);

        // Replace these with the actual IDs from your XML layout
        loadAverageTextView = findViewById(R.id.load_average_textview);
        cpuLoadTextView = findViewById(R.id.cpu_load_textview);
        memoryUsageTextView = findViewById(R.id.memory_usage_textview);

        serviceSectionCardView.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ServiceActivity.class);
            startActivity(intent);
        });

        cronSectionCardView.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CronActivity.class);
            startActivity(intent);
        });
        apiHandler.fetchMetrics();
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onMetricsFetched(JSONObject metrics) {
        if (metrics != null) {
            try {
                // Update TextViews with fetched data
                JSONObject loadAverage = metrics.getJSONObject("load_average");
                loadAverageTextView.setText(String.format("1min: %.2f  -  5min: %.2f  -  15min: %.2f",
                        loadAverage.getDouble("load1"),
                        loadAverage.getDouble("load5"),
                        loadAverage.getDouble("load15")));

                double cpuLoad = metrics.getDouble("cpu_load");
                cpuLoadTextView.setText(String.format("%.2f", cpuLoad));

                JSONObject memoryUsage = metrics.getJSONObject("memory_usage");
                memoryUsageTextView.setText(String.format("Total: %dGB  -  Used: %dGB  -  Free: %dGB",
                        memoryUsage.getLong("total") / (1024 * 1024 * 1024),
                        memoryUsage.getLong("used") / (1024 * 1024 * 1024),
                        memoryUsage.getLong("free") / (1024 * 1024 * 1024)));
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Error processing metrics data", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(MainActivity.this, "Error fetching metrics", Toast.LENGTH_SHORT).show();
        }
    }
}
