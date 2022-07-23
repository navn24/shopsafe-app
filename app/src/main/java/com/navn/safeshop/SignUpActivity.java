package com.navn.safeshop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.navn.safeshop.requests.AddUser;
import com.navn.safeshop.requests.CheckIfEmailFound;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;


import java.util.concurrent.ExecutionException;

public class SignUpActivity extends AppCompatActivity {
    private EditText userName;
    private EditText email;
    private EditText password;
    private EditText passwordConfirm;
    private Button confirmRegistrationButton;
    private TextView alreadyHaveAccount;
    private TextView privacyPolicyText;
    private boolean successfulRegistration;
    int RC_SIGN_IN = 0;

    GoogleSignInClient mGoogleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();

        // set an exit transition

        privacyPolicyText = findViewById(R.id.privacyPolicyText);
        userName = (EditText) findViewById(R.id.userNameInput);
        email = (EditText) findViewById(R.id.emailInput);
        password = (EditText) findViewById(R.id.passwordInput);
        alreadyHaveAccount = findViewById(R.id.alreadyHaveAccount);
        passwordConfirm =  (EditText) findViewById(R.id.passwordConfirmInput);
        confirmRegistrationButton = (Button) findViewById(R.id.confirmRegistrationButton) ;

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        alreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignUpActivity.super.onBackPressed();
            }
        });
        privacyPolicyText.setMovementMethod(LinkMovementMethod.getInstance());
        confirmRegistrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(userName.getText().toString().equals("")||email.getText().toString().equals("")||password.getText().toString().equals("")||passwordConfirm.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "Make sure to enter values for all fields!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(userName.getText().toString().length()>15){
                    Toast.makeText(SignUpActivity.this, "Username can't be longer than 15 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(email.getText().toString().length()>25){
                    Toast.makeText(SignUpActivity.this, "Email can't be longer than 25 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(password.getText().toString().length()>25){
                    Toast.makeText(SignUpActivity.this, "Password can't be longer than 25 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!email.getText().toString().contains("@")||email.getText().toString().contains("")){
                    Toast.makeText(getApplicationContext(), "Please enter a valid email!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!password.getText().toString().equals(passwordConfirm.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Passwords don't match!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(password.getText().toString().equals("")){
                    Toast.makeText(SignUpActivity.this,"Passwords can't be empty!",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(password.getText().toString().contains(" ")){
                    Toast.makeText(SignUpActivity.this,"Passwords can't contain spaces!",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(String.valueOf(userName.getText().toString()).matches("^.*[^a-zA-Z0-9!@#$%&-_].*$")){
                    Toast.makeText(getApplicationContext(), "Invalid characters in user name!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(String.valueOf(email.getText().toString()).matches("^.*[^a-zA-Z0-9!@#$%&-_].*$")){
                    Toast.makeText(getApplicationContext(), "Invalid characters in email!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(String.valueOf(password.getText().toString()).matches("^.*[^a-zA-Z0-9!@#$%&-_].*$")){
                    Toast.makeText(getApplicationContext(), "Invalid characters in password!", Toast.LENGTH_SHORT).show();
                    return;
                }
                CheckIfEmailFound checkIfEmailFound = new CheckIfEmailFound(email.getText().toString(), getApplicationContext());
                try {
                    checkIfEmailFound.execute().get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(!checkIfEmailFound.getEmailFound()) {
                        AddUser addUserObj = new AddUser(userName.getText().toString(),email.getText().toString(),password.getText().toString(),getApplicationContext());
                        addUserObj.setName(userName.getText().toString());
                        addUserObj.setEmail(email.getText().toString());
                        addUserObj.setPassword(password.getText().toString());

                        try {
                            addUserObj.execute().get();
                            successfulRegistration = addUserObj.isSuccessful();

                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        Toast.makeText(getApplicationContext(), "Registered Successfully!", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getApplicationContext(), "The given email has been found in the database", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {

            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String idToken = account.getIdToken();
            // Signed in successfully, show authenticated UI.
            startActivity(new Intent(this, LoginInfo.class));
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Google Sign In Error", "signInResult:failed code=" + e.getStatusCode());
            //Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStart() {
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account != null) {
           // startActivity(new Intent(MainActivity.this, Main2Activity.class));
        }
        super.onStart();
    }

}