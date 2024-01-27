package com.example.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CronActivity extends AppCompatActivity {
    EditText cronName, minute, hour, dayOfMonth, month, dayOfWeek, cronCommand;
    ListView cronList;
    Button createCron, deleteCron, reloadCronList;

    private ApiHandler apiHandler;
    private String selectedCronName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cron);

        cronName = findViewById(R.id.editTextCronName);
        minute = findViewById(R.id.editTextMinutes);
        hour = findViewById(R.id.editTextHour);
        dayOfMonth = findViewById(R.id.editTextDayOfMonth);
        month = findViewById(R.id.editTextMonth);
        dayOfWeek = findViewById(R.id.editTextDayOfWeek);
        cronCommand = findViewById(R.id.editTextCommand);

        cronList = findViewById(R.id.listViewCrons);

        createCron = findViewById(R.id.buttonCreateCron);
        deleteCron = findViewById(R.id.buttonDeleteCron);
        reloadCronList = findViewById(R.id.buttonReloadCronList);

        // Initialize ApiHandler with the username
        String loggedInUsername = getSharedPreferences("MyPrefs", MODE_PRIVATE).getString("username", null);
        if (loggedInUsername == null) {
            Toast.makeText(CronActivity.this, "Username not available", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        apiHandler = new ApiHandler(this, loggedInUsername);

        // Disable delete cron button for initial
        deleteCron.setEnabled(false);

        cronList.setOnItemClickListener((parent, view, position, id) -> {
            // Check if any item is selected in the ListView
            if (position != ListView.INVALID_POSITION) {
                // Enable the Delete Cron button
                deleteCron.setEnabled(true);
                selectedCronName = getCronNameFromPosition(position);
            } else {
                // Disable the Delete Cron button
                deleteCron.setEnabled(false);
                selectedCronName = null;
            }
        });

        fetchCronList();
        reloadCronList.setOnClickListener(v -> fetchCronList());
        createCron.setOnClickListener(v -> createCronJob());
        deleteCron.setOnClickListener(v -> deleteCronJob());
    }

    public static class CronListAdapter extends ArrayAdapter<String> {
        public CronListAdapter(Context context, List<String> cronJobsList) {
            super(context, android.R.layout.simple_list_item_1, cronJobsList);
        }
    }


    private void fetchCronList() {
        deleteCron.setEnabled(false);
        selectedCronName = null;
        apiHandler.apiWithToken("/cronjobs", ApiHandler.HttpMethod.GET, null, null, new ApiHandler.ResponseCallback() {
            @Override
            public void onSuccess(String response) {
                updateListCronListView(response);
            }

            @Override
            public void onError(String error) {
                handleApiError("Cron list Error", error);
            }
        });
    }

    private void updateListCronListView(String response) {
        try {
            // Parse the JSON response
            JSONObject responseObject = new JSONObject(response);
            JSONArray jobsArray = responseObject.getJSONArray("jobs");

            // Create a list to store cron jobs
            List<String> cronJobsList = new ArrayList<>();

            // Iterate through the array and add cron job details to the list
            for (int i = 0; i < jobsArray.length(); i++) {
                JSONObject jobObject = jobsArray.getJSONObject(i);
                String jobDetails = "Name: " + jobObject.getString("name") +
                        "\nSchedule: " + jobObject.getString("schedule") +
                        "\nCommand: " + jobObject.getString("command");

                cronJobsList.add(jobDetails);
            }

            // Use the custom adapter
            CronListAdapter adapter = new CronListAdapter(CronActivity.this, cronJobsList);
            cronList.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
            handleApiError("Error parsing JSON response", "JSONException");
        }
    }

    private void createCronJob() {
        // Extract user input for the new cron job
        String newCronName = cronName.getText().toString();
        String newMinute = minute.getText().toString();
        String newHour = hour.getText().toString();
        String newDayOfMonth = dayOfMonth.getText().toString();
        String newMonth = month.getText().toString();
        String newDayOfWeek = dayOfWeek.getText().toString();
        String newCronCommand = cronCommand.getText().toString();

        // Create a JSON object with the new cron job details
        JSONObject newCronJson = new JSONObject();
        try {
            newCronJson.put("name", newCronName);
            newCronJson.put("schedule", String.format("%s %s %s %s %s", newMinute, newHour, newDayOfMonth, newMonth, newDayOfWeek));
            newCronJson.put("command", newCronCommand);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error creating JSON object", Toast.LENGTH_SHORT).show();
            return;
        }

        // Make a POST request to create the new cron job
        apiHandler.apiWithToken("/cronjobs", ApiHandler.HttpMethod.POST, null, newCronJson.toString(), new ApiHandler.ResponseCallback() {
            @Override
            public void onSuccess(String response) {
                // Handle success, for example, refresh the cron job list
                fetchCronList();
                Toast.makeText(CronActivity.this, "Cron job created successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error) {
                String apiError = isInvalidCronFormatError(error);
                handleApiError("Error: ", apiError);
            }
        });
    }

    private String isInvalidCronFormatError(String error) {
        try {
            JSONObject errorJson = new JSONObject(error);
            return errorJson.optString("detail", "");
        } catch (JSONException e) {
            e.printStackTrace();
            return error;
        }
    }

    // Helper method to extract cron name from the selected position in the ListView
    private String getCronNameFromPosition(int position) {
        CronListAdapter adapter = (CronListAdapter) cronList.getAdapter();
        if (adapter != null) {
            if (position >= 0 && position < adapter.getCount()) {
                String selectedCronDetails = adapter.getItem(position);

                // Assuming the cron name is the first line in the details
                assert selectedCronDetails != null;
                String[] detailsLines = selectedCronDetails.split("\n");
                if (detailsLines.length > 0) {
                    // Extract the cron name
                    return detailsLines[0].replace("Name: ", "");
                }
            }
        }
        return null;
    }

    private void deleteCronJob() {
        if (selectedCronName != null) {
            // Make a DELETE request to delete the selected cron job
            String deleteUrl = "/cronjobs/" + selectedCronName;
            apiHandler.apiWithToken(deleteUrl, ApiHandler.HttpMethod.DELETE, null, null, new ApiHandler.ResponseCallback() {
                @Override
                public void onSuccess(String response) {
                    // Handle success, for example, refresh the cron job list
                    fetchCronList();
                    Toast.makeText(CronActivity.this, "Cron job deleted successfully", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String error) {
                    // Handle the error, display a generic error message
                    handleApiError("Delete Cron Job Error", error);
                }
            });
        } else {
            // If no cron is selected, display a message or handle it accordingly
            Toast.makeText(this, "No cron selected for deletion", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleApiError(String prefix, String error) {
        Toast.makeText(getApplicationContext(), prefix + ": " + error, Toast.LENGTH_LONG).show();
    }
}