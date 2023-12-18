package com.example.app;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;
import java.util.regex.*;

public class RegisterActivity extends AppCompatActivity {
    EditText username, password, re_password, ip_domain, passphrase;
    Button register;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new DatabaseHelper(this);

        username = findViewById(R.id.editTextUsername);
        password = findViewById(R.id.editTextPassword);
        re_password = findViewById(R.id.editTextReEnterPassword);
        ip_domain = findViewById(R.id.editTextIPDomain);
        passphrase = findViewById(R.id.editTextPassphrase);
        register = findViewById(R.id.buttonRegister);

        TextView backLoginTextView = findViewById(R.id.back_login_page);
        backLoginTextView.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        register.setOnClickListener(v -> registerUser());
    }

    // Method to check password strength and length
    private static boolean isValidPassword(String password) {
        String regex = "^(?=.*[0-9])"
                + "(?=.*[a-z])(?=.*[A-Z])"
                + "(?=.*[@#$%^&+=])"
                + "(?=\\S+$).{8,20}$";

        Pattern p = Pattern.compile(regex);

        if (password == null) {
            return false;
        }

        Matcher m = p.matcher(password);

        return m.matches();
    }

    private void registerUser() {
        String user = username.getText().toString().trim();
        String pass = password.getText().toString().trim();
        String rePass = re_password.getText().toString().trim();
        String host = ip_domain.getText().toString().trim();
        String passPhrase = passphrase.getText().toString().trim();

        if (user.isEmpty() || pass.isEmpty() || rePass.isEmpty() || host.isEmpty() || passPhrase.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidPassword(pass)) {
            password.setError("Password must contain at least 8 characters including at least one digit, one lower and upper case alphabet, one special character, and no white space");
            return;
        }

        if (!pass.equals(rePass)) {
            re_password.setError("The password entered does not match");
            return;
        }

        // Check if the username already exists in the database
        if (dbHelper.checkUserExistence(user)) {
            Toast.makeText(RegisterActivity.this, "Username already exists!", Toast.LENGTH_SHORT).show();
        } else {
            // Add user to the database
            boolean isInserted = dbHelper.addUser(user, pass, host, passPhrase);

            if (isInserted) {
                Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(RegisterActivity.this, "Registration failed!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Method to handle Connection Test
    private void connectionTest () {
        // TODO
    }
}
