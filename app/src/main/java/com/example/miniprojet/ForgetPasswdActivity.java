package com.example.miniprojet;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ForgetPasswdActivity extends AppCompatActivity {

    private EditText emailEditText;
    private Button resetPasswordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_passwd);

        emailEditText = findViewById(R.id.editTextEmail);
        resetPasswordButton = findViewById(R.id.buttonResetPassword);

        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });
    }

    private void resetPassword() {
        String email = emailEditText.getText().toString().trim();

        // TODO: Implement your logic for password reset here
        // For example, you can use Firebase Authentication or your own backend service.

        // Display a toast message for demonstration purposes.
        showToast("Password reset instructions sent to " + email);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
