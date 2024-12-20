package com.example.todotitans;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * The {@code SignInActivity} class handles the user sign-in process.
 * <p>
 * This activity provides functionality for signing in an existing user,
 * navigating to the sign-up screen, and recovering forgotten passwords.
 * If the user is already signed in, they are redirected to the {@code HomeActivity}.
 * </p>
 */
public class SignInActivity extends AppCompatActivity {
    private EditText inputEmail, inputPassword;
    private Button buttonLogin;
    private FirebaseAuth authentication;
    private ProgressBar progressBar;

    /**
     * Called when the activity is starting. Initializes the layout, checks for an already signed-in user,
     * and sets up event listeners for sign-in, sign-up, and forgot password actions.
     *
     * @param savedInstanceState If the activity is being reinitialized after being previously shut down,
     *                           this Bundle contains the most recent data supplied; otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        authentication = FirebaseAuth.getInstance();

        FirebaseUser user = authentication.getCurrentUser();
        if (user != null) {
            // If user is already signed in, navigate to HomeActivity
            Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
            intent.putExtra("USER_ID", user.getUid());
            startActivity(intent);
            return; // Prevent further execution
        }


        // provided a link for UI elements
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        progressBar = findViewById(R.id.progressBar);

        buttonLogin.setOnClickListener(v -> signInUser());


        MaterialButton signUpButton = findViewById(R.id.buttonSignUp);
        MaterialButton forgotPasswordButton = findViewById(R.id.buttonForgotPassword);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to SignUpActivity
                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });


        forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });


    }

    /**
     * Handles the user sign-in process.
     * <p>
     * This method validates the user's email and password, then uses Firebase Authentication to
     * sign in the user. If successful, the user is redirected to the {@code HomeActivity}; otherwise,
     * an error message is displayed.
     * </p>
     */
    private void signInUser() {
        loading(true);
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        // Check for valid input
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
            loading(false);
            return;
        }

        // Sign in the user with Firebase Authentication
        authentication.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    loading(false);
                    if (task.isSuccessful()) {
                        FirebaseUser user = authentication.getCurrentUser();
                        if (user != null) {
                            Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
                            intent.putExtra("USER_ID", user.getUid());
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        // login failure
                        Toast.makeText(this, "Login Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    /**
     * Toggles the loading state of the sign-in process.
     * <p>
     * This method hides or shows the login button and progress bar based on the loading state.
     * </p>
     *
     * @param isLoading {@code true} to show the progress bar and hide the button, {@code false} to do the reverse.
     */
    private void loading(Boolean isLoading) {
        if (isLoading) {
            buttonLogin.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            buttonLogin.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

}
