package com.navn.safeshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.navn.safeshop.requests.GooglePostIdToken;
import com.navn.safeshop.requests.LinkAccounts;
import com.navn.safeshop.requests.ValidateUserByEmail;
import com.navn.safeshop.requests.ValidateUserByName;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;


import java.io.ByteArrayInputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.concurrent.ExecutionException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class LoginActivity extends AppCompatActivity {
private Button loginButton;
//private Button registerButton;
private TextView registerButton;
private EditText usernameText;
private EditText passwordText;
private CheckBox rememberCheckBox;
private SharedPreferences prefs;
private SharedPreferences.Editor editor;
private boolean rememberMe;
    private Requests requestsClass = new Requests();

    private String userNameInput;
    private String passwordInput;
    private String userInfoType;
   // SignInButton signInButton;

    Button signInButton;
    int RC_SIGN_IN = 0;
    GoogleSignInClient mGoogleSignInClient;
    private InfoToPass infoObject;
    private LoggedIntoAppInfo loginObject;
    private SecretKey secretKey;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_new);
        getSupportActionBar().hide();
        signInButton = findViewById(R.id.google_sign_in);

        infoObject = (InfoToPass) getIntent().getSerializableExtra("InfoToPassObj");
        loginObject = (LoggedIntoAppInfo) getIntent().getSerializableExtra("LoggedIntoAppInfo");

        if(infoObject==null){
            Toast.makeText(getApplicationContext(), "Something went wrong! Please try opening and closing the app", Toast.LENGTH_LONG);
            Log.d("Debug", "InfoObject is null");
            return;
        }
        if(loginObject==null){
            Toast.makeText(getApplicationContext(), "Something went wrong! Please try opening and closing the app", Toast.LENGTH_LONG);
            Log.d("Debug", "LoginObject is null");
            return;
        }
        try {
            secretKey = getKeyFromPassword(getApplicationContext().getString(R.string.service3),getApplicationContext().getString(R.string.service4));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();
System.out.println("Server Id: +" + R.string.server_client_id);
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        //Check if user has clicked remember me, and if so, log the user in automatically
         prefs = getSharedPreferences("checkbox", MODE_PRIVATE);
         editor = prefs.edit();
        rememberMe = prefs.getBoolean("rememberMe",false);


        //If user has clicked remember me from their last session, check if the stored user_id and user_name's are not null, and then log them in
        if(rememberMe){
            String storedPassword = prefs.getString("password","");
            String storedUser_name = prefs.getString("user_name", "");

            if(   (!storedPassword.equals(""))  &&   (!storedUser_name.equals(""))   ){
                if(secretKey!=null){
                        try {
                            ValidateLoginInfo(decrypt(storedUser_name, secretKey), decrypt(storedPassword, secretKey));
                        }catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | InvalidAlgorithmParameterException e) {
                            e.printStackTrace();
                        }
                    }else{
                    Log.d("Debug", "Secret key was null, so user wasn't logged in via remember me!");
                }

            }else{
            Log.d("Debug", "No credentials were found for the user!");
            }
        }

        loginButton = (Button) findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
        passwordText = (EditText) findViewById(R.id.passwordInput);
        usernameText = (EditText) findViewById(R.id.emailInput);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenRegisterActivity();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //Remove Spaces and Tabs from User Name/Email and Password Fields
                userNameInput = usernameText.getText().toString();
                passwordInput = passwordText.getText().toString();
                userNameInput = userNameInput.trim();
                passwordInput = passwordInput.trim();

                ValidateLoginInfo(userNameInput,passwordInput );
              System.out.println(usernameText.getText().toString());
                System.out.println(passwordText.getText().toString());
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

    private void handleSignInResult(@NonNull Task<GoogleSignInAccount> completedTask) {
        try {

            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String idToken = account.getIdToken();
            GooglePostIdToken googlePostIdToken = new GooglePostIdToken(idToken, getApplicationContext());
            googlePostIdToken.execute().get();
            checkErrorMessages(googlePostIdToken, idToken);

            // Signed in successfully, show authenticated UI.
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Google Sign In Error", "signInResult:failed code=" + e.getStatusCode());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void checkErrorMessages(GooglePostIdToken googlePostIdToken, final String idTokenString){
        String error_message = googlePostIdToken.getError_message();
        Boolean new_account = googlePostIdToken.getNew_account();
        if(error_message.equals("")){
            //Check for new_account being true, but log the user in regardless of new account being true or false- since there is no error message
            if(new_account == true){
                //Do something later - maybe welcome them?
            }
            Integer user_id = googlePostIdToken.getUser_id();
            String user_name = googlePostIdToken.getUser_name();
            String email = googlePostIdToken.getEmail();
            Integer google_login = 1; // Set login with google variable equal to 1;
            editor.putBoolean("googleRememberMe", true);
            editor.putBoolean("rememberMe", false);
            editor.apply();
            if(loginObject.isFromMapsActivity()){
                OpenMapsActivity(user_id,user_name, email,google_login);
            }else if(loginObject.isFromLeaveReviewActivity()){
                OpenLeaveReviewActivity(user_id,user_name, email,google_login);
            }
        } else{
            //If Invalid ID Token - show toast message with error
            if(error_message.equals("Invalid ID Token")){
                Toast.makeText(LoginActivity.this, "Couldn't sign in with Google - Invalid ID Token", Toast.LENGTH_LONG) ;
            }
            if(error_message.equals("Would you like to link accounts")) {
                //Display a dialog asking if user would like to link accounts
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this,R.style.Theme_AppCompat_Dialog_Alert);
                builder.setTitle("Link Account");
                builder.setMessage("An account with the same email was found in our database, would you like to link google sign in with the " +
                        "previously found account?");
                // add the buttons and their OnClickListeners
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        LinkAccounts linkAccounts = new LinkAccounts(idTokenString, getApplicationContext());
                        try {
                            linkAccounts.execute().get();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                // create and show the alert dialog
                AlertDialog linkAccountDialog = builder.create();
                linkAccountDialog.show();
            }
        }

    }

    public void ValidateLoginInfo(String usernameInput, String passwordInput){
        System.out.println (usernameInput);
        if(!(userNameInput.equals(""))&&!(passwordInput.equals(""))) {

            //Create validate request class objects
            ValidateUserByName validateUserByName = new ValidateUserByName(usernameInput, passwordInput, getApplicationContext());
            ValidateUserByEmail validateUserByEmail = new ValidateUserByEmail(usernameInput, passwordInput, getApplicationContext());

            //Check if username text is an email or a user name
            if (usernameInput.contains("@")) {
                userInfoType = "Email";
                System.out.println("Its an Email!!!!");
            } else {
                userInfoType = "Name";
                System.out.println("Its a Name!!!!");
            }
            if (userInfoType == "Name") {
                validateUserByName.setUserName(usernameInput);
                validateUserByName.setPassword(passwordInput);
                try {
                    validateUserByName.execute().get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (validateUserByName.isUserFound().equals("true") && validateUserByName.isPasswordMatches().equals("true")) {
                    Toast.makeText(this, "Logged in Successfully", Toast.LENGTH_SHORT).show();

                    //Get User Id from validation response, and then pass user id onto next activity
                    Integer user_id = validateUserByName.getUser_id();
                    String user_name = validateUserByName.getUserName();
                    String email = validateUserByName.getEmail();
                    Integer google_login = validateUserByName.getGoogle_login();

                    //Put credentials into sharedPreferences for remember me functionality
                    putIntoSharedPrefs(email,passwordInput);



                    if (loginObject.isFromMapsActivity()) {
                        OpenMapsActivity(user_id, user_name, email, google_login);
                    } else if (loginObject.isFromLeaveReviewActivity()) {
                        OpenLeaveReviewActivity(user_id, user_name, email, google_login);
                    }
                } else {
                    Toast.makeText(this, "Username or password is incorrect", Toast.LENGTH_SHORT).show();
                }
            }

            if (userInfoType == "Email") {
                validateUserByEmail.setUserEmail(usernameInput);
                validateUserByEmail.setPassword(passwordInput);
                try {
                    validateUserByEmail.execute().get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (validateUserByEmail.isUserFound().equals("true") && validateUserByEmail.isPasswordMatches().equals("true")) {
                    Toast.makeText(this, "Logged in Successfully", Toast.LENGTH_SHORT).show();

                    //Get User Id from validation response, and then pass user id onto next activity
                    Integer user_id = validateUserByEmail.getUser_id();
                    String user_name = validateUserByEmail.getUser_name();
                    String email = usernameInput;
                    Integer google_login = validateUserByEmail.getGoogle_login();

                    //Put credentials into sharedPreferences for remember me functionality
                    putIntoSharedPrefs(email,passwordInput);


                    if (loginObject.isFromMapsActivity()) {
                        OpenMapsActivity(user_id, user_name, email, google_login);
                    } else if (loginObject.isFromLeaveReviewActivity()) {
                        OpenLeaveReviewActivity(user_id, user_name, email, google_login);
                    }
                } else {
                    Toast.makeText(this, "Username or password is incorrect", Toast.LENGTH_SHORT).show();
                }
            }
        }else{
            Toast.makeText(getApplicationContext(), "Username or password fields cannot be empty", Toast.LENGTH_SHORT).show();

        }
    }
    public void OpenMapsActivity(Integer user_id, String user_name, String email, Integer google_login){
        Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        InfoToPass infoToPassInstance = new InfoToPass();
        infoToPassInstance.setUser_id(user_id);
        infoToPassInstance.setUser_name(user_name);
        infoToPassInstance.setEmail(email);
        infoToPassInstance.setGoogle_login(google_login);
        infoToPassInstance.setLoggedIn(true);
        System.out.println("EMAIL" + email);
        intent.putExtra("InfoToPassObj", infoToPassInstance);
        intent.putExtra("LoggedIntoAppInfo", loginObject);
        Log.d("TEST: ", ""+user_name) ;
        startActivity(intent);
        finish();
    }
    public void OpenLeaveReviewActivity(Integer user_id, String user_name, String email, Integer google_login){
        Intent intent =  new Intent(this,LeaveReviewActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        infoObject.setUser_id(user_id);
        infoObject.setUser_name(user_name);
        infoObject.setEmail(email);
        infoObject.setGoogle_login(google_login);
        infoObject.setLoggedIn(true);
        intent.putExtra("InfoToPassObj", infoObject);
        intent.putExtra("LoggedIntoAppInfo", loginObject);
        startActivity(intent);
        finish();
    }
    public void OpenRegisterActivity(){
        Intent intent = new Intent( this, SignUpActivity.class) ;
        ActivityOptions options = ActivityOptions
                .makeSceneTransitionAnimation( this, registerButton, "shared_element_container");
        startActivity(intent, options.toBundle());
    }
    public static SecretKey getKeyFromPassword(String password, String salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 256);
        SecretKey secret = new SecretKeySpec(factory.generateSecret(spec)
                .getEncoded(), "AES");
        return secret;
    }

    public static String encrypt(String input, SecretKey key) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        String algorithm = "AES";
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec("************".getBytes()));
        byte[] cipherText = cipher.doFinal(input.getBytes());
        return Base64.encodeToString(cipherText, Base64.DEFAULT);
    }
    public static String decrypt( String cipherText, SecretKey key) throws NoSuchPaddingException, NoSuchAlgorithmException,
            BadPaddingException, IllegalBlockSizeException, InvalidKeyException, InvalidAlgorithmParameterException {
        String algorithm = "AES";
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec("************".getBytes()));
        byte[] plainText = cipher.doFinal(Base64
                .decode(cipherText,Base64.DEFAULT));
        return new String(plainText);
    }
    public void putIntoSharedPrefs(String email, String password){
        if(secretKey!=null){
            try {
                editor.putString("password", encrypt(passwordInput, secretKey));
                editor.putString("email", encrypt(email, secretKey));

                editor.putBoolean("rememberMe", true);
                editor.putBoolean("googleRememberMe", false);
                editor.apply();
                Log.d("Debug", "Creds saved successfully");

            } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | InvalidAlgorithmParameterException e) {
                e.printStackTrace();
            }
        }else{
            Log.d("Debug", "secret key is null, so the user will not be remembered next time");
        }
    }


}