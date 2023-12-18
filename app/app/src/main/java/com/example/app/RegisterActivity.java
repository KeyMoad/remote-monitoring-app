package com.example.app;

import java.util.regex.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;;
import android.content.Intent;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;

public class RegisterActivity extends AppCompatActivity {
    EditText username, password, re_password, ip_domain, passphrase;
    Button register, connection_test;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.editTextUsername);
        password = findViewById(R.id.editTextPassword);
        re_password = findViewById(R.id.editTextReEnterPassword);
        ip_domain = findViewById(R.id.editTextIPDomain);
        passphrase = findViewById(R.id.editTextPassphrase);

        register = findViewById(R.id.buttonRegister);
        connection_test = findViewById(R.id.buttonTestConnection);

        TextView backLoginTextView = findViewById(R.id.back_login_page);
        backLoginTextView.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        ClickRegister();
    }

    // Method to check password strength and length
    private static boolean isValidPassword(String password)
    {
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

    // Method for Register information
    private void ClickRegister()
    {
        register.setOnClickListener(v -> {
            if (username.getText().toString().trim().isEmpty()) {

                username.setError("Please fill out this field");
            } else {

                username.setText("Salam");
            }
            if (password.getText().toString().trim().isEmpty()) {

                password.setError("Please fill out this field");
            } else if (! isValidPassword(password.getText().toString())) {

                password.setError("Password must contains at least 8 chars, one digit, one lower and upper case alphabet, one special chars and no white space");
            } else {

                password.setText("Salam");
            }
            if (re_password.getText().toString().trim().isEmpty()) {

                re_password.setError("Please fill out this field");
            } else {

                re_password.setText("Salam");
            }
            if (ip_domain.getText().toString().trim().isEmpty()) {

                ip_domain.setError("Please fill out this field");
            } else {

                ip_domain.setText("Salam");
            }
            if (passphrase.getText().toString().trim().isEmpty()) {

                passphrase.setError("Please fill out this field");
            } else {

                passphrase.setText("Salam");
            }
        });
    }

    private void ClickConnectionTest()
    {
        // TODO
    }
}