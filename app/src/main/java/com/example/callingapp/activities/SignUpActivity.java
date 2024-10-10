package com.example.callingapp.activities;

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

import com.example.callingapp.R;
import com.example.callingapp.databinding.ActivitySignUpBinding;
import com.example.callingapp.models.UserModel;
import com.example.callingapp.utils.MailSender;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.TestOnly;

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

}