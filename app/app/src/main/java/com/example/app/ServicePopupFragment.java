package com.example.app;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class ServicePopupFragment extends DialogFragment {

    private String title;
    private String message;

    public ServicePopupFragment(String title, String message) {
        this.title = title;
        this.message = message;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.service_popup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set the title and message
        TextView titleTextView = view.findViewById(R.id.popup_title);
        titleTextView.setText(title);

        TextView messageTextView = view.findViewById(R.id.popup_message);
        messageTextView.setText(message);

        // Close Button
        Button closeButton = view.findViewById(R.id.close_button);
        closeButton.setOnClickListener(v -> dismiss());
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new Dialog(requireContext(), getTheme());
    }
}
