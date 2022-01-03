package com.example.chat_app_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetingsActivity extends AppCompatActivity {

    private Button UpdateAccountSettings;
    private EditText Username, UserStatus;
    private CircleImageView UserProfileImage;

    private FirebaseAuth mAuth;
    private String CurrentUserID;
    private DatabaseReference RootRef;
    private Toolbar SettingsToolBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setings);


        mAuth = FirebaseAuth.getInstance();
        CurrentUserID = mAuth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();


        InitializeFields();


        Username.setVisibility(View.VISIBLE);
        UserStatus.setVisibility(View.INVISIBLE);

        UpdateAccountSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateSettings();
            }
        });

        RetrieveUserInfo();
    }




    private void InitializeFields() {

        UpdateAccountSettings = (Button) findViewById(R.id.update_settings_button);
        Username = (EditText) findViewById(R.id.set_user_name);
        UserStatus = (EditText) findViewById(R.id.set_profile_status);
        UserProfileImage = (CircleImageView) findViewById(R.id.set_profile_image);
        SettingsToolBar = (Toolbar) findViewById(R.id.settings_toolbar);
        setSupportActionBar(SettingsToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle("Account Settings");

    }


    private void UpdateSettings() {

        String setUserName = Username.getText().toString();
        String setStatus = UserStatus.getText().toString();

        if(TextUtils.isEmpty(setUserName)){
            Toast.makeText(this, "Please write your Username", Toast.LENGTH_SHORT).show();
        }
//        if(TextUtils.isEmpty(setStatus)){
//            Toast.makeText(this, "Please write your Status", Toast.LENGTH_SHORT).show();
//        }
        else{
            HashMap<String,String> profileMap = new HashMap<>();
                profileMap.put("uid", CurrentUserID);
                profileMap.put("name", setUserName);
                profileMap.put("status", setStatus);
            RootRef.child("Users").child(CurrentUserID).setValue(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                SendUserToMainActivity();
                                Toast.makeText(SetingsActivity.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                String message = task.getException().toString();
                                Toast.makeText(SetingsActivity.this, "Error : " + message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }



    private void RetrieveUserInfo() {
        RootRef.child("Users").child(CurrentUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if((dataSnapshot.exists()) && (dataSnapshot.hasChild("name") && (dataSnapshot.hasChild("image")))){
                            String retrieveUserName = dataSnapshot.child("name").getValue().toString();
                            String retrieveStatus = dataSnapshot.child("status").getValue().toString();
                            String retrieveProfileImage = dataSnapshot.child("image").getValue().toString();

                            Username.setText(retrieveUserName);
                            UserStatus.setText(retrieveStatus);

                        }
                        else if((dataSnapshot.exists()) && (dataSnapshot.hasChild("name"))){
                            String retrieveUserName = dataSnapshot.child("name").getValue().toString();
                            String retrieveStatus = dataSnapshot.child("status").getValue().toString();

                            Username.setText(retrieveUserName);
                            UserStatus.setText(retrieveStatus);
                        }
                        else{
                            Username.setVisibility(View.VISIBLE);
                            Toast.makeText(SetingsActivity.this, "Please Update your Profile Information", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }



    private void SendUserToMainActivity() {

        Intent mainIntent = new Intent(SetingsActivity.this, StartActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(mainIntent);
        finish();
    }


}