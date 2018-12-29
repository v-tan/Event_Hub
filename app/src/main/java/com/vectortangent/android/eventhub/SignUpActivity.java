package com.vectortangent.android.eventhub;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    private Button signupButton;
    private TextView loginLinkText;
    private EditText nameText;
    private EditText emailText;
    private EditText passwordText;
    private FirebaseAuth firebaseAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        firebaseAuth = FirebaseAuth.getInstance();

        nameText = findViewById(R.id.input_name);
//        EditText addressText = findViewById(R.id.input_address);
        emailText = findViewById(R.id.input_email);
//        EditText mobileText = findViewById(R.id.input_mobile);
        passwordText = findViewById(R.id.input_password);
//        EditText reEnterPasswordText = findViewById(R.id.input_reEnterPassword);
        signupButton = findViewById(R.id.btn_signup);
        loginLinkText = findViewById(R.id.link_login);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        loginLinkText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.enter_left_in, R.anim.exit_left_out);
            }
        });
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignUpActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        String name = nameText.getText().toString();
//        String address = addressText.getText().toString();
        String email = emailText.getText().toString();
//        String mobile = mobileText.getText().toString();
        String password = passwordText.getText().toString();
//        String reEnterPassword = reEnterPasswordText.getText().toString();

        // TODO: Implement your own signup logic here.
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Log.d(TAG, "createUserWithEmail: success");
                            onSignupSuccess();
                        } else {
                            Toast.makeText(SignUpActivity.this, "Authentication failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                }).addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "createUserWithEmail: failure", e);
                        onSignupFailed();
                        progressDialog.dismiss();
                    }
                });
    }

    public void onSignupSuccess() {
        signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Sign Up failed", Toast.LENGTH_LONG).show();
        signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = nameText.getText().toString();
//        String address = addressText.getText().toString();
        String email = emailText.getText().toString();
//        String mobile = mobileText.getText().toString();
        String password = passwordText.getText().toString();
//        String reEnterPassword = reEnterPasswordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            nameText.setError("at least 3 characters");
            valid = false;
        } else {
            nameText.setError(null);
        }

//                if (address.isEmpty()) {
//            addressText.setError("Enter Valid Address");
//            valid = false;
//        } else {
//            addressText.setError(null);
//        }


        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError("enter a valid email address");
            valid = false;
        } else {
            emailText.setError(null);
        }

//        if (mobile.isEmpty() || mobile.length()!=10) {
//            mobileText.setError("Enter Valid Mobile Number");
//            valid = false;
//        } else {
//            mobileText.setError(null);
//        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            passwordText.setError(null);
        }

//        if (reEnterPassword.isEmpty() || reEnterPassword.length() < 4 || reEnterPassword.length() > 10 || !(reEnterPassword.equals(password))) {
//            reEnterPasswordText.setError("Password Do not match");
//            valid = false;
//        } else {
//            reEnterPasswordText.setError(null);
//        }

        return valid;
    }
}