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

public class MainActivity extends AppCompatActivity {

    // Replace these with the actual IDs from your XML layout
    private TextView loadAverageTextView;
    private TextView cpuLoadTextView;
    private TextView memoryUsageTextView;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find the CardViews
        CardView serviceSectionCardView = findViewById(R.id.section_action);
        CardView cronSectionCardView = findViewById(R.id.section_cron);
        CardView deleteSectionCardView = findViewById(R.id.section_delete);

        // Replace these with the actual IDs from your XML layout
        loadAverageTextView = findViewById(R.id.load_average_textview);
        cpuLoadTextView = findViewById(R.id.cpu_load_textview);
        memoryUsageTextView = findViewById(R.id.memory_usage_textview);

        // Retrieve the logged-in username from shared preferences
        String loggedInUsername = getSharedPreferences("MyPrefs", MODE_PRIVATE).getString("username", null);

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

            // Use the logged-in username for deletion
            if (loggedInUsername != null) {
                dbHelper.deleteUser(loggedInUsername);

                // Redirect to login page
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);

                Toast.makeText(MainActivity.this, "Deletion succeed !", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Deletion failed !", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
