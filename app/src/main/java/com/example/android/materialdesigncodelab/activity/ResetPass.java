package com.example.android.materialdesigncodelab.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.materialdesigncodelab.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPass extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    EditText email;
    EditText password;

    ProgressDialog loading;

    //private static final int RC_SIGN_IN = 123;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        Button btnLogin = (Button) findViewById(R.id.btn_login);
        Button btnSignUp = (Button) findViewById(R.id.btn_signup);
        Button btnReset = (Button) findViewById(R.id.btn_reset_password);

        // USERNAME INPUT
        email = (EditText) findViewById(R.id.email);
        // PASSWORD INPUT
        password = (EditText) findViewById(R.id.password);

        password.setVisibility(View.INVISIBLE);
        btnLogin.setText("Reset Password");
        btnLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                reset_pass(email.getText().toString());
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                to_login();
            }
        });
        btnSignUp.setVisibility(View.INVISIBLE);
    }

    private void reset_pass(String email) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String emailAddress = email;

        auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getBaseContext(), "Password sent to email!",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getBaseContext(), "Error! ",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void to_main_activity() {
        Intent myIntent = new Intent(this, MainActivity.class);
        //myIntent.putExtra("EXTRA_POSITION", position);
        startActivityForResult(myIntent, 0);
    }

    private void to_login() {
        Intent myIntent = new Intent(this, Login.class);
        //myIntent.putExtra("EXTRA_POSITION", position);
        startActivityForResult(myIntent, 0);
    }

    private void to_reset_pass() {
        Intent myIntent = new Intent(this, ResetPass.class);
        //myIntent.putExtra("EXTRA_POSITION", position);
        startActivityForResult(myIntent, 0);
    }
}
