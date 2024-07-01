package com.example.miniprojet;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        EditText emailEditText = findViewById(R.id.emailEditText);
        EditText passwordEditText = findViewById(R.id.passwordEditText);
        Button signupButton = findViewById(R.id.signupButton);

        signupButton.setOnClickListener(view -> {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if (password.length() < 6) {
                // Display an error message to the user indicating that the password is too short
                Toast.makeText(SignUpActivity.this, "Password should be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            // Use mAuth instead of FirebaseAuth.getInstance() for consistency
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this, "The account has been successfully created.", Toast.LENGTH_SHORT).show();

                            // Start LoginActivity when signup is successful
                            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish(); // Close the SignUpActivity to prevent going back
                        } else {
                            // If account creation fails, display a message with the specific error
                            Toast.makeText(SignUpActivity.this, "Account creation failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}
