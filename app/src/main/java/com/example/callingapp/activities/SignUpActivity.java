package com.example.callingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.loader.content.Loader;

import com.example.callingapp.R;
import com.example.callingapp.databinding.ActivitySignUpBinding;
import com.example.callingapp.models.UserModel;
import com.example.callingapp.utils.MailSender;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.TestOnly;

import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class SignUpActivity extends AppCompatActivity {
    ActivitySignUpBinding binding;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseAuth auth;
    private String userName, userEmail, userPassword;
    String verificationId;
    String generatedOtp;
    String userEnteredOtp;
    FirebaseUser currentUser;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Initializing variables
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        auth = FirebaseAuth.getInstance();

        binding.sendOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateOtp();
            }
        });

        binding.verifyOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!binding.signUpActivityOtpEditText.getText().toString().isEmpty()) {
                    userEnteredOtp = binding.signUpActivityOtpEditText.getText().toString();
                    userName = binding.signUpActivityNameEditText.getText().toString();
                    userEmail = binding.signUpActivityEmailEditText.getText().toString();
                    userPassword = binding.signUpActivityPasswordEditText.getText().toString();
                    verifyOtp(userEnteredOtp);
                } else {
                    Toast.makeText(SignUpActivity.this, "Please enter the OTP", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Method for generating the otp
    private void generateOtp() {
        generatedOtp = String.valueOf(new Random().nextInt((999999 - 10000) + 10000));
        Toast.makeText(this, generatedOtp, Toast.LENGTH_SHORT).show();
        //Getting user details from the edit text views
        userEmail = binding.signUpActivityEmailEditText.getText().toString();
        sendOtpToEmail(userEmail, generatedOtp);
    }

    //Method for saving the generated otp to the firebase
    private void saveOtpToFirebase() {
        databaseReference.child("otp").setValue(generatedOtp);
        binding.sendOtpButton.setClickable(false);
        binding.enterOtpLayout.setVisibility(View.VISIBLE);
        binding.verifyOtpButton.setVisibility(View.VISIBLE);
    }

    //Method for sending the otp to the email
    private void sendOtpToEmail(String userEmail, String generatedOtp) {
        String emailSubject = "Your OTP Code";
        String messageBody = "Your OTP code is: " + generatedOtp;

        new Thread(() -> {
            Looper.prepare();
            try {
                MailSender.sendEmail(SignUpActivity.this, userEmail, emailSubject, messageBody);
                runOnUiThread(() -> {
                    saveOtpToFirebase();
                    Toast.makeText(SignUpActivity.this, "OTP sent successfully", Toast.LENGTH_SHORT).show();
                });
            } catch (Exception e) {
                Log.e("otp error", e.getMessage(), e); // Log the full error
                runOnUiThread(() -> {
                    Toast.makeText(SignUpActivity.this, "Failed to send OTP to this email", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    //Method for verifying the otp entered by the user
    private void verifyOtp(String userEnteredOtp) {
        databaseReference.child("otp").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (userEnteredOtp.equals(snapshot.getValue(String.class))) {
                        Toast.makeText(SignUpActivity.this, "OTP verified successfully", Toast.LENGTH_SHORT).show();
                        createUser(userEmail, userPassword);
                    } else {
                        Toast.makeText(SignUpActivity.this, "Incorrect OTP entered!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SignUpActivity.this, "Database error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SignUpActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Method for creating user account
    private void createUser(String userEmail, String userPassword) {
        if (auth != null) {
            auth.createUserWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        currentUser = auth.getCurrentUser();
                        Toast.makeText(SignUpActivity.this, "Account created successfully", Toast.LENGTH_SHORT).show();
                        addUserToDatabase(userName, userEmail, userPassword);
                    } else {
                        Toast.makeText(SignUpActivity.this, "Failed to create account", Toast.LENGTH_SHORT).show();
                        Log.d("account creation error : ", task.getException().getMessage());
                    }
                }
            }).addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    //Method for adding the user to the database
    private void addUserToDatabase(String userName, String userEmail, String userPassword) {
        userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        UserModel userModel = new UserModel(userId, userName, userEmail, userPassword);
        databaseReference.child("users").child(userId).setValue(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    startActivity(new Intent(SignUpActivity.this, HomeActivity.class));
                    finish();
                } else {
                    Toast.makeText(SignUpActivity.this, "Failed to add user to the database", Toast.LENGTH_SHORT).show();
                    Log.d("user add error : ", task.getException().getMessage());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}