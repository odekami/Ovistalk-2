package com.example.android.materialdesigncodelab.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.materialdesigncodelab.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class Register extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    EditText email;
    EditText password;

    ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        // set loading

        loading = new ProgressDialog(this);
        loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loading.setMessage("Checking data... Please wait...");
        loading.setCancelable(false);

        Button btnLogin = (Button) findViewById(R.id.btn_login);
        Button btnSignUp = (Button) findViewById(R.id.btn_signup);

        // USERNAME INPUT
        email = (EditText) findViewById(R.id.email);
        // PASSWORD INPUT
        password = (EditText) findViewById(R.id.password);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                check_login();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                to_login();
            }
        });

    }

    private void check_login() {
        loading.show();
        // TODO Auto-generated method stub
        String Email_check = email.getText().toString();
        String Pass_check = password.getText().toString();
        mAuth.createUserWithEmailAndPassword(Email_check, Pass_check)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("success", "signInWithEmail:onComplete:" + task.isSuccessful());

                        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef1 = database.getReference("users").child("user_" + currentFirebaseUser.getUid());
                        if (currentFirebaseUser != null) {
                            for (UserInfo profile : currentFirebaseUser.getProviderData()) {
                                // Id of the provider (ex: google.com)
                                String providerId = profile.getProviderId();

                                // Name, email address, and profile photo Url
                                String refreshedToken = FirebaseInstanceId.getInstance().getToken();
                                String name = profile.getDisplayName();
                                String email = profile.getEmail();
                                myRef1.setValue(new User_data(currentFirebaseUser.getEmail(), currentFirebaseUser.getUid(), refreshedToken, currentFirebaseUser.getUid(), ""));
                            }
                            ;
                        }

                        to_main_activity();

                        if (!task.isSuccessful()) {
                            Log.d("error", "signInWithEmail:error:");
                            Toast.makeText(getApplicationContext(), "Your toast message.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        loading.cancel();
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
}
