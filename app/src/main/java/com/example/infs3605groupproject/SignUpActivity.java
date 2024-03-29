package com.example.infs3605groupproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.infs3605groupproject.objects.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText nameField, emailField, phoneField, suburbField, passwordField;
    private Button saveButton;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private String name, email ,phone ,suburb, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(this);

        nameField = findViewById(R.id.nameField);
        emailField = findViewById(R.id.emailField);
        phoneField = findViewById(R.id.phoneField);
        suburbField = findViewById(R.id.suburbField);
        passwordField = findViewById(R.id.passwordField);

        progressBar = findViewById(R.id.progressBar);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.saveButton:
                registerUser();
        }
    }

    // Present error messages required
    private void registerUser() {
        email = emailField.getText().toString().trim();
        name = nameField.getText().toString().trim();
        phone = phoneField.getText().toString().trim();
        suburb = suburbField.getText().toString().trim();
        password = passwordField.getText().toString().trim();

        if(name.isEmpty()) {
            nameField.setError("Full name required!");
            nameField.requestFocus();
            return;
        }

        if(email.isEmpty()) {
            emailField.setError("Email required!");
            emailField.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailField.setError("Please provide valid email!");
            emailField.requestFocus();
            return;
        }

        if(password.isEmpty()) {
            passwordField.setError("Password required!");
            passwordField.requestFocus();
            return;
        }

        if(password.length() < 8) {
            passwordField.setError("Minimum password length is 8 characters");
            passwordField.requestFocus();
            return;
        }

        if(suburb.isEmpty()) {
            suburbField.setError("Suburb required!");
            suburbField.requestFocus();
            return;
        }

        if(phone.isEmpty()) {
            phone = "Not provided";
        }

        // Create user in firebase database
        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            User user = new User(name,email,suburb,phone);
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(SignUpActivity.this,"Sign up successful",Toast.LENGTH_LONG).show();
                                                progressBar.setVisibility(View.GONE);
                                                startActivity(new Intent(SignUpActivity.this,MainActivity.class));
                                            } else {
                                                Toast.makeText(SignUpActivity.this,"Sign up failed, try again",Toast.LENGTH_LONG).show();
                                                progressBar.setVisibility(View.GONE);
                                            }
                                        }
                                    });
                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            userRef.child("badge1").setValue(false);
                            userRef.child("badge2").setValue(false);
                            userRef.child("badge3").setValue(false);
                            userRef.child("badge4").setValue(false);
                            userRef.child("badge5").setValue(false);
                            userRef.child("badge6").setValue(false);
                            userRef.child("badge7").setValue(false);
                            userRef.child("badge8").setValue(false);
                        }

                        else {
                            Toast.makeText(SignUpActivity.this,"Sign up failed, try again",Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }

                    }
                });

    }

}