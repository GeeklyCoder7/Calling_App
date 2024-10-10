package com.example.callingapp.activities;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.UserManager;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.util.Log;
import android.widget.Toast;

import com.example.callingapp.R;
import com.example.callingapp.adapters.UsersListAdapter;
import com.example.callingapp.databinding.ActivityHomeBinding;
import com.example.callingapp.models.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {
    ActivityHomeBinding binding;
    FirebaseAuth auth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    ArrayList<UserModel> userModelArrayList;
    String[] permissions = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
    };

    private int requestCode = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Initializing variables
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
        userModelArrayList = new ArrayList<>();

        //Calling necessary functions below
        if (!isPermissionsGranted()) {
            askPermissions();
        }
        fetchAllUsers();
    }

    //Method for fetching all the users
    private void fetchAllUsers() {
        databaseReference.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userModelArrayList.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    UserModel userModel = userSnapshot.getValue(UserModel.class);
                    if (userModel != null) {
                        userModelArrayList.add(userModel);
                    }
                }
                setUpUsersRecyclerView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("user fetching error : ", "message : " + error.getMessage() + "\n details : " + error.getDetails());
            }
        });
    }

    //Method for checking if the permissions are granted
    public boolean isPermissionsGranted() {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    //Method for requesting permissions
    public void askPermissions() {
        ActivityCompat.requestPermissions(this, permissions, requestCode);
    }

    //Method for setting up the users recycler view
    private void setUpUsersRecyclerView() {
        UsersListAdapter usersListAdapter = new UsersListAdapter(HomeActivity.this, userModelArrayList);
        binding.usersListRecyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
        binding.usersListRecyclerView.setAdapter(usersListAdapter);
    }
}