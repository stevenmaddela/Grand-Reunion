package com.syfn.grandreunion;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassword extends AppCompatActivity {

    private Button ResetPasButton;
    private EditText ResetEmailInput;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        ResetPasButton = findViewById(R.id.resetBtn);
        ResetEmailInput = findViewById(R.id.emailInput);
        mAuth = FirebaseAuth.getInstance();

        ResetPasButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = ResetEmailInput.getText().toString();
                if(TextUtils.isEmpty(userEmail)){
                    Toast.makeText(ResetPassword.this, "Please Enter Your Account Email", Toast.LENGTH_SHORT).show();
                }
                else{
                    mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(ResetPassword.this, "Email Sent", Toast.LENGTH_SHORT).show();
                                Intent login = new Intent(ResetPassword.this, LoginActivity.class);
                                startActivity(login);
                            }
                            else{
                                String message = task.getException().toString();
                                Toast.makeText(ResetPassword.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }
}
