package com.example.app;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class HistoryActivity extends AppCompatActivity {
    private Logger logger;
    private TextView logTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        logTextView = findViewById(R.id.logTextView);

        String loggedInUsername = getSharedPreferences("MyPrefs", MODE_PRIVATE).getString("username", null);
        if (loggedInUsername == null) {
            Toast.makeText(HistoryActivity.this, "Username not available", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        logger = new Logger(loggedInUsername, getApplicationContext());

        // Fetch and display logs
        String logs = logger.readLogFile();
        logTextView.setText(logs);
    }
}
