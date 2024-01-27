package com.example.app;

import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ServiceActivity extends AppCompatActivity {
    private EditText newServiceName;
    private Spinner servicesList;

    private ApiHandler apiHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);

        newServiceName = findViewById(R.id.editTextNewService);
        servicesList = findViewById(R.id.spinnerServiceList);
        Button addService = findViewById(R.id.buttonAddService);
        Button statusService = findViewById(R.id.buttonstatusServices);
        Button startService = findViewById(R.id.buttonStartService);
        Button stopService = findViewById(R.id.buttonStopService);
        Button listActiveServices = findViewById(R.id.buttonActiveServices);
        Button listInactiveServices = findViewById(R.id.buttonInactiveServices);

        // Initialize ApiHandler with the username
        String loggedInUsername = getSharedPreferences("MyPrefs", MODE_PRIVATE).getString("username", null);
        if (loggedInUsername == null) {
            Toast.makeText(ServiceActivity.this, "Username not available", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        apiHandler = new ApiHandler(this, loggedInUsername);

        fetchServiceList();

        addService.setOnClickListener(v -> addNewService());
        statusService.setOnClickListener(v -> getServiceStatus());
        startService.setOnClickListener(v -> startService());
        stopService.setOnClickListener(v -> stopService());
        listActiveServices.setOnClickListener(v -> listActiveServices());
        listInactiveServices.setOnClickListener(v -> listInactiveServices());
    }

    private void addNewService() {
        // Get the service name from the EditText
        String serviceName = newServiceName.getText().toString().trim();

        // Check if the service name is not empty
        if (serviceName.isEmpty()) {
            Toast.makeText(this, "Please fill the New Service text box !", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a JSON object with the new cron job details
        JSONObject newCronJson = new JSONObject();
        try {
            newCronJson.put("name", serviceName);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error creating JSON object", Toast.LENGTH_SHORT).show();
            return;
        }

        // Call the apiWithToken method for adding a new service
        apiHandler.apiWithToken("/service/add", ApiHandler.HttpMethod.POST, null, newCronJson.toString(), new ApiHandler.ResponseCallback() {
            @Override
            public void onSuccess(String response) {
                // Handle success, for example, refresh the cron job list
                fetchServiceList();
                Toast.makeText(ServiceActivity.this, "New Service added successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error) {
                handleApiError("Error adding new service! Please read README of project", error);
            }
        });
    }

    private void fetchServiceList() {
        // Call the apiWithToken method for fetching the list of services
        apiHandler.apiWithToken("/service/list", ApiHandler.HttpMethod.GET, null, null, new ApiHandler.ResponseCallback() {
            @Override
            public void onSuccess(String response) {
                handleFetchServiceList(response);
            }

            @Override
            public void onError(String error) {
                handleApiError("Error fetching service list: ", error);
            }
        });
    }

    private void handleFetchServiceList(String response) {
        try {
            // Parse the JSON response
            JSONObject jsonResponse = new JSONObject(response);
            JSONObject detail = jsonResponse.getJSONObject("detail");

            // Check if the service list was retrieved successfully
            if (detail.has("data")) {
                JSONArray serviceArray = detail.getJSONArray("data");

                // Extract services from the JSON array
                List<String> services = new ArrayList<>();
                for (int i = 0; i < serviceArray.length(); i++) {
                    services.add(serviceArray.getString(i));
                }

                // Create an ArrayAdapter using the string array and a default spinner layout
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, services);

                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                // Apply the adapter to the spinner
                servicesList.setAdapter(adapter);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getServiceStatus() {
        // Get the selected service from the spinner
        String selectedService = servicesList.getSelectedItem().toString();

        // Check if a service is selected
        if (selectedService.isEmpty()) {
            Toast.makeText(this, "Please select a service from the list!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Build the API path for the selected service
        String apiPath = "/service/" + selectedService;

        // Call the apiWithToken method for fetching the status of the selected service
        apiHandler.apiWithToken(apiPath, ApiHandler.HttpMethod.GET, null, null, new ApiHandler.ResponseCallback() {
            @Override
            public void onSuccess(String response) {
                handleStatusService(response);
            }

            @Override
            public void onError(String error) {
                handleApiError("Error fetching status of the service: ", error);
            }
        });
    }

    private void handleStatusService(String response) {
        // Print the response to Logcat to see what data is received
        Log.d("StatusServiceResponse", response);

        try {
            // Parse the JSON response
            JSONObject jsonResponse = new JSONObject(response);
            if (jsonResponse.has("detail")) {
                JSONObject detail = jsonResponse.getJSONObject("detail");

                // Check if the status was retrieved successfully
                if (detail.has("data")) {
                    // Get the status message and data as strings
                    String statusMessage = detail.getString("message");
                    String statusData = detail.getString("data");

                    // Display the status message to the user
                    showToastAtTop(statusMessage + " " + statusData);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void startService() {
        // Get the selected service from the spinner
        String selectedService = servicesList.getSelectedItem().toString();

        // Check if a service is selected
        if (selectedService.isEmpty()) {
            Toast.makeText(this, "Please select a service from the list!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Build the API path for starting the selected service
        String apiPath = "/service/start/" + selectedService;

        // Call the apiWithToken method for starting the selected service
        apiHandler.apiWithToken(apiPath, ApiHandler.HttpMethod.POST, null, null, new ApiHandler.ResponseCallback() {
            @Override
            public void onSuccess(String response) {
                handleStartService(response);
            }

            @Override
            public void onError(String error) {
                handleApiError("Error starting the service: ", error);
            }
        });
    }

    private void handleStartService(String response) {
        try {
            // Parse the JSON response
            JSONObject jsonResponse = new JSONObject(response);
            if (jsonResponse.has("detail")) {
                JSONObject detail = jsonResponse.getJSONObject("detail");

                // Check if the start request was successful
                if (detail.has("data")) {
                    boolean startStatus = detail.getBoolean("data");

                    // Display the status message to the user
                    showToastAtTop("Service started: " + startStatus);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void stopService() {
        // Get the selected service from the spinner
        String selectedService = servicesList.getSelectedItem().toString();

        // Check if a service is selected
        if (selectedService.isEmpty()) {
            Toast.makeText(this, "Please select a service from the list!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Build the API path for stopping the selected service
        String apiPath = "/service/stop/" + selectedService;

        // Call the apiWithToken method for stopping the selected service
        apiHandler.apiWithToken(apiPath, ApiHandler.HttpMethod.POST, null, null, new ApiHandler.ResponseCallback() {
            @Override
            public void onSuccess(String response) {
                handleStopService(response);
            }

            @Override
            public void onError(String error) {
                handleApiError("Error stopping the service: ", error);
            }
        });
    }

    private void handleStopService(String response) {
        try {
            // Parse the JSON response
            JSONObject jsonResponse = new JSONObject(response);
            if (jsonResponse.has("detail")) {
                JSONObject detail = jsonResponse.getJSONObject("detail");

                // Check if the stop request was successful
                if (detail.has("data")) {
                    boolean stopStatus = detail.getBoolean("data");

                    // Display the status message to the user
                    showToastAtTop("Service stopped: " + stopStatus);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void listActiveServices() {
        // Call the apiWithToken method for fetching the list of active services
        apiHandler.apiWithToken("/service/list?list_type=active", ApiHandler.HttpMethod.GET, null, null, new ApiHandler.ResponseCallback() {
            @Override
            public void onSuccess(String response) {
                handleListServices(response, "Active Services");
            }

            @Override
            public void onError(String error) {
                handleApiError("Error fetching list of active services: ", error);
            }
        });
    }

    private void listInactiveServices() {
        // Call the apiWithToken method for fetching the list of inactive services
        apiHandler.apiWithToken("/service/list?list_type=inactive", ApiHandler.HttpMethod.GET, null, null, new ApiHandler.ResponseCallback() {
            @Override
            public void onSuccess(String response) {
                handleListServices(response, "Inactive Services");
            }

            @Override
            public void onError(String error) {
                handleApiError("Error fetching list of inactive services: ", error);
            }
        });
    }

    private void handleListServices(String response, String title) {
        try {
            // Parse the JSON response
            JSONObject jsonResponse = new JSONObject(response);
            JSONObject detail = jsonResponse.getJSONObject("detail");

            // Check if the service list was retrieved successfully
            if (detail.has("data")) {
                JSONArray serviceArray = detail.getJSONArray("data");

                // Extract services from the JSON array
                List<String> services = new ArrayList<>();
                for (int i = 0; i < serviceArray.length(); i++) {
                    services.add(serviceArray.getString(i));
                }

                // Build the message to display in the popup
                StringBuilder messageBuilder = new StringBuilder();
                messageBuilder.append("A list of ").append(title.toLowerCase()).append(" retrieved successfully:\n");

                for (String service : services) {
                    messageBuilder.append("- ").append(service).append("\n");
                }

                // Display the result in a popup at the center of the screen
                showPopup(title, messageBuilder.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showPopup(String title, String message) {
        ServicePopupFragment popupFragment = new ServicePopupFragment(title, message);
        popupFragment.show(getSupportFragmentManager(), "service_popup");
    }

    private void handleApiError(String prefix, String error) {
        try {
            JSONObject errorJson = new JSONObject(error);
            if (errorJson.has("detail")) {
                String detail = errorJson.getString("detail");
                Toast.makeText(getApplicationContext(), prefix + ": " + detail, Toast.LENGTH_LONG).show();
            } else {
                // If "detail" field is not present, display the entire error message
                Toast.makeText(getApplicationContext(), prefix + ": " + error, Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            // If parsing as JSON fails, display the entire error message
            Toast.makeText(getApplicationContext(), prefix + ": " + error, Toast.LENGTH_LONG).show();
        }
    }

    private void showToastAtTop(String message) {
        // Inflate the custom layout
        View layout = getLayoutInflater().inflate(R.layout.service_toast, findViewById(R.id.custom_toast_container));

        // Set the message
        TextView text = layout.findViewById(R.id.text);
        text.setText(message);

        // Create the Toast
        Toast toast = new Toast(ServiceActivity.this);
        toast.setGravity(Gravity.TOP | Gravity.FILL_HORIZONTAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

}