package com.example.chat_app_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {


    private Button ResetPasswordSendEmailButton;
    private EditText ResetEmailInput;
    private TextView MoveToLoginPage;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        mAuth = FirebaseAuth.getInstance();


        InitializeFields();


        MoveToLoginPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToLoginActivity();
            }
        });

        ResetPasswordSendEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String UserEmail = ResetEmailInput.getText().toString();

                if(TextUtils.isEmpty(UserEmail)){
                    Toast.makeText(ResetPasswordActivity.this, "Please Enter your Email to get Reset Password Link", Toast.LENGTH_SHORT).show();
                }
                else{
                    mAuth.sendPasswordResetEmail(UserEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(ResetPasswordActivity.this, "Please Check your Email for Reset Password Link", Toast.LENGTH_SHORT).show();
                                SendUserToLoginActivity();
                            }
                            else{
                                String errorMessage = task.getException().getMessage();
                                Toast.makeText(ResetPasswordActivity.this, "Error : " + errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }



    private void InitializeFields() {

        ResetPasswordSendEmailButton = (Button) findViewById(R.id.reset_password_button);
        ResetEmailInput = (EditText) findViewById(R.id.reset_email);
        MoveToLoginPage = (TextView) findViewById(R.id.reset_password_goto_login);

    }

    private void SendUserToLoginActivity() {
        Intent LoginIntent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
        startActivity(LoginIntent);
    }
}