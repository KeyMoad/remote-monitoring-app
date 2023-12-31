package com.example.app;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find the CardViews
        CardView sectionMetrics = findViewById(R.id.section_metrics);
        CardView sectionActions = findViewById(R.id.section_actions);
        CardView sectionServerOperations = findViewById(R.id.section_server_operations);

        // Set OnClickListener for sectionMetrics CardView
        sectionMetrics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
            }
        });

        // Set OnClickListener for sectionActions CardView
        sectionActions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(MainActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
            }
        });

        // Set OnClickListener for sectionServerOperations CardView
        sectionServerOperations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
