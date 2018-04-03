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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class Login extends AppCompatActivity {
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

        // set loading

        loading = new ProgressDialog(this);
        loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loading.setMessage("Checking data... Please wait...");
        loading.setCancelable(false);

        Button btnLogin = (Button) findViewById(R.id.btn_login);
        Button btnSignUp = (Button) findViewById(R.id.btn_signup);
        Button btnReset = (Button) findViewById(R.id.btn_reset_password);

        // USERNAME INPUT
        email = (EditText) findViewById(R.id.email);
        // PASSWORD INPUT
        password = (EditText) findViewById(R.id.password);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                check_login();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                to_register();
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                to_reset_pass();
            }
        });

        btnSignUp.setVisibility(View.INVISIBLE);
        /*
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(Arrays.asList(
                                new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build(),
                                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                        .build(),
                RC_SIGN_IN);
         */
    }
    private void check_login(){
        loading.show();
        // TODO Auto-generated method stub
        String Email_check = email.getText().toString();
        String Pass_check = password.getText().toString();
        mAuth.signInWithEmailAndPassword(Email_check, Pass_check)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
                    if (currentFirebaseUser != null) {
                        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        // Last message
                        DatabaseReference user_login = database.getReference("users").child("user_"+ currentFirebaseUser.getUid()).child("Token");
                        user_login.setValue(refreshedToken);
                        to_main_activity();
                        loading.cancel();
                    }


                    if (!task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Error login try again!",
                                Toast.LENGTH_SHORT).show();
                        loading.cancel();
                    }
                }
            });
    }
    private void to_main_activity(){
        Intent myIntent = new Intent(this, MainActivity.class);
        //myIntent.putExtra("EXTRA_POSITION", position);
        startActivityForResult(myIntent, 0);
    }
    private void to_register(){
        Intent myIntent = new Intent(this, Register.class);
        //myIntent.putExtra("EXTRA_POSITION", position);
        startActivityForResult(myIntent, 0);
    }
    private void to_reset_pass(){
        Intent myIntent = new Intent(this, ResetPass.class);
        //myIntent.putExtra("EXTRA_POSITION", position);
        startActivityForResult(myIntent, 0);
    }
}
