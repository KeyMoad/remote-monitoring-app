package com.example.app;

import androidx.appcompat.app.AppCompatActivity;
import android.net.Uri;
import android.os.Bundle;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    private EditText username, password;
    private TextView noAccountTextView;
    private Button login;
    private ImageView gitRepoLink;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DatabaseHelper(this);

        username = findViewById(R.id.editTextUsername);
        password = findViewById(R.id.editTextPassword);
        login = findViewById(R.id.buttonLogin);
        noAccountTextView = findViewById(R.id.no_account_text_view);
        gitRepoLink = findViewById(R.id.git_repo_link);

        noAccountTextView.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        gitRepoLink.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/KeyMoad/remote-monitoring-app/"));
            startActivity(browserIntent);
        });

        clickLogin();
    }

    // Method to handle login action
    private void clickLogin() {
        login.setOnClickListener(v -> {
            String user = username.getText().toString().trim();
            String pass = password.getText().toString().trim();

            if (user.isEmpty()) {
                username.setError("Please fill out this field");
            } else if (pass.isEmpty()) {
                password.setError("Please fill out this field");
            } else {
                boolean isValidUser = dbHelper.checkUserCredentials(user, pass);

                if (isValidUser) {
                    // Successful login
                    Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();

                    // Store the username in shared preferences
                    getSharedPreferences("MyPrefs", MODE_PRIVATE)
                            .edit()
                            .putString("username", user)
                            .apply();

                    Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                } else {
                    // Incorrect credentials
                    Toast.makeText(LoginActivity.this, "Couldn't login with this information!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}