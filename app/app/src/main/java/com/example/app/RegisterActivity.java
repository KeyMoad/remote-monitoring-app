package com.example.app;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.*;

public class RegisterActivity extends AppCompatActivity {
    EditText username, password, re_password, ip_domain;
    Button register;
    DatabaseHelper dbHelper;

    private static final String DEFAULT_PROTOCOL = "http";
    private static final String DEFAULT_PORT = "9932";
    private static final String PASSCODE_ENDPOINT = "/passcode";

    private interface PasscodeCallback {
        void onPasscodeFetched(String passcode);
        void onError(String errorMessage);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new DatabaseHelper(this);

        username = findViewById(R.id.editTextUsername);
        password = findViewById(R.id.editTextPassword);
        re_password = findViewById(R.id.editTextReEnterPassword);
        ip_domain = findViewById(R.id.editTextIPDomain);
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

    private static String isValidHost(String host) {
        // Check if the host already has a port specified
        Log.d("isValidHost", "Out port if condition: " + host);
        if (!host.contains(":")) {
            Log.d("isValidHost", "In port if condition: " + host);
            // No port specified, add the default port
            host = host + ":" + DEFAULT_PORT;
        }

        // Check if the host starts with http:// or https://
        if (!host.startsWith("http://") && !host.startsWith("https://")) {
            host = DEFAULT_PROTOCOL + "://" + host;
        }

        Log.d("isValidHost", "Final Host: " + host);
        return host;
    }


    private void fetchPassphrase(String hostURL, PasscodeCallback callback) {
        String passcodeUrl = hostURL + PASSCODE_ENDPOINT;
        // Create a Volley request
        StringRequest passcodeRequest = new StringRequest(Request.Method.GET, passcodeUrl,
                response -> {
                    // Handle successful response
                    String passPhrase = parsePassphrase(response);
                    callback.onPasscodeFetched(passPhrase);
                },
                error -> {
                    // Handle error response
                    callback.onError("Fetching Passcode Error: " + error.getMessage());
                });

        // Add the request to the Volley request queue
        Volley.newRequestQueue(this).add(passcodeRequest);
    }

    private String parsePassphrase(String response) {
        try {
            JSONObject jsonResponse = new JSONObject(response);
            if (jsonResponse.has("detail") && jsonResponse.getJSONObject("detail").has("passcode")) {
                return jsonResponse.getJSONObject("detail").getString("passcode");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void registerUser() {
        String user = username.getText().toString().trim();
        String pass = password.getText().toString().trim();
        String rePass = re_password.getText().toString().trim();
        String host = isValidHost(ip_domain.getText().toString().trim());

        if (user.isEmpty() || pass.isEmpty() || rePass.isEmpty() || host.isEmpty()) {
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
        if (!dbHelper.checkUserExistence(user)) {
            // Fetch passcode before registering user
            fetchPassphrase(host, new PasscodeCallback() {
                @Override
                public void onPasscodeFetched(String passcode) {
                    // Continue with user registration
                    boolean isInserted = dbHelper.addUser(user, pass, host, passcode);

                    if (isInserted) {
                        Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(RegisterActivity.this, "Registration failed!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(RegisterActivity.this, "Username already exists!", Toast.LENGTH_SHORT).show();
        }
    }
}
