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
    EditText username, password;
    TextView noAccountTextView;
    Button login;
    ImageView gitRepoLink;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/"));
            startActivity(browserIntent);
        });

        clickLogin();
    }

    // Method for handle login logic
    private void loginLogic(String username, String password) {
        if ("salam".equals(username) && "salam".equals(password)) {
            // Your successful login logic
            Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(mainIntent);
        } else {
            // Incorrect credentials
            Toast errorToast = Toast.makeText(LoginActivity.this, "Couldn't login with this information !!!", Toast.LENGTH_SHORT);
            errorToast.show();
        }
    }

    // Method to handle login action
    private void clickLogin() {
        login.setOnClickListener(v -> {
            if (username.getText().toString().trim().isEmpty()) {

                username.setError("Please fill out this field");
            } else if (password.getText().toString().trim().isEmpty()) {

                password.setError("Please fill out this field");
            } else {

                loginLogic(username.getText().toString(), password.getText().toString());

            }
        });
    }
}