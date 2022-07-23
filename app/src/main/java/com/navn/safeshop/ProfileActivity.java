package com.navn.safeshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.navn.safeshop.requests.APIRequest;
import com.navn.safeshop.requests.ChangeUserName;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.ExecutionException;

public class ProfileActivity extends AppCompatActivity {
    GoogleSignInClient mGoogleSignInClient;
    InfoToPass infoObject ;
    EditText UserNameEditText;
    EditText EmailEditText;
    Button PasswordChangeButton;
    Button logOutButton;
    ImageButton EditUsernameButton;
    Button SaveChangesButton;
    ImageView profile_view;
    private RequestQueue mQueue;
    private Integer google_login;
    private Uri picCapUri;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("My Profile");

        prefs = getSharedPreferences("checkbox", MODE_PRIVATE);
        editor = prefs.edit();

        infoObject = (InfoToPass) getIntent().getSerializableExtra("InfoToPassObj");

        if(infoObject.getGoogle_login()!=null){
            google_login = infoObject.getGoogle_login();
        }else{
            google_login = 0;
        }
        mQueue = Volley.newRequestQueue(this);
        EmailEditText = findViewById(R.id.EmailEditText);
        UserNameEditText = findViewById(R.id.UsernameEditText);
        PasswordChangeButton = findViewById(R.id.PasswordChange);
        EditUsernameButton = findViewById(R.id.EditUserNameButton);
        logOutButton = findViewById(R.id.logOutButton);
        SaveChangesButton = findViewById(R.id.SaveButton);
        UserNameEditText.setText(infoObject.getUser_name());
        EmailEditText.setText(infoObject.getEmail());

        if(google_login==0) {

        }else if(google_login==1){
            PasswordChangeButton.setVisibility(View.INVISIBLE);
        }

        PasswordChangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, ChangePasswordActivity.class);
                intent.putExtra("InfoToPassObj", infoObject);
                startActivity(intent);
                finish();
            }
        });
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(google_login==1){
                    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(getString(R.string.server_client_id))
                            .requestEmail()
                            .build();

                    // Build a GoogleSignInClient with the options specified by gso.
                    mGoogleSignInClient = GoogleSignIn.getClient(ProfileActivity.this, gso);
                    mGoogleSignInClient.signOut()
                            .addOnCompleteListener(ProfileActivity.this, new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    editor.putBoolean("googleRememberMe", true);
                                    editor.apply();
                                    Toast.makeText(ProfileActivity.this,"Successfully signed out",Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(ProfileActivity.this, MapsActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                            Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                            Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(i);
                                    finish();
                                }
                            });
                }else{
                    //If user has not signed in with google, sign them out like usual (e.g. bring them back to login activity and destroy infoObject)
                    editor.putBoolean("rememberMe", false);
                    editor.apply();
                    Toast.makeText(ProfileActivity.this,"Successfully signed out",Toast.LENGTH_SHORT).show();
                    infoObject = null;
                    Intent i = new Intent(ProfileActivity.this, MapsActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK |
                            Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    finish();
                }
            }
        });


        UserNameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    SaveChangesButton.setVisibility(View.VISIBLE);
                }else{
                    SaveChangesButton.setVisibility(View.INVISIBLE);
                }
            }
        });
        SaveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(infoObject.getUser_name().equals(UserNameEditText.getText().toString())){
                    Toast.makeText(ProfileActivity.this, "Username has not been changed!", Toast.LENGTH_SHORT).show();
                    UserNameEditText.clearFocus();
                    return;
                }
                if(UserNameEditText.getText().toString().equals("")){
                    Toast.makeText(ProfileActivity.this,"Username can't be empty!",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(UserNameEditText.getText().toString().length()>15){
                    Toast.makeText(ProfileActivity.this, "Username can't be longer than 15 characters!", Toast.LENGTH_SHORT).show();
                    UserNameEditText.clearFocus();
                    return;
                }
                if(String.valueOf(UserNameEditText.getText().toString()).matches("^.*[^a-zA-Z0-9!@#$%&-_].*$")){
                    Toast.makeText(getApplicationContext(), "Invalid characters in user name!", Toast.LENGTH_SHORT).show();
                    return;
                }
                ChangeUserName changeUserNameReq = new ChangeUserName(infoObject.getUser_id(),UserNameEditText.getText().toString());
                try {
                    changeUserNameReq.execute().get();
                    Toast.makeText(ProfileActivity.this,"Updated Name Successfully",Toast.LENGTH_SHORT).show();

                    infoObject.setUser_name(UserNameEditText.getText().toString());
                    Intent intent = new Intent(ProfileActivity.this, MapsActivity.class);
                    intent.putExtra("InfoToPassObj", infoObject);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK |
                            Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } catch (ExecutionException | InterruptedException e) {
                    Toast.makeText(ProfileActivity.this,"Could not update name",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                UserNameEditText.clearFocus();
            }
        });



    }
    private void ChangePassword(int UserId, String NewPassword)  {
        //  to add %20 for spaces

        String url = "https://" + getString(R.string.local_dns)+"/user/updatePassword?user_id="+UserId+ "&password="+ NewPassword;

        try {
            InputStream stream =  new APIRequest().connectToMiddleTier(url,"POST");
        } catch (IOException e) {
            e.printStackTrace();
        }
        //HTTP Get Request to Json file
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // remove existing markers from previous search

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        StringRequest request1 = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // remove existing markers from previous search
                        System.out.println(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue.add(request1);
    }
    private void ChangeUsername(int UserId, String NewUsername) {


        String url = "https://" + getString(R.string.local_dns)+"/user/updateName?user_id="+ UserId+"&user_name="+NewUsername;

        try {
            InputStream stream =  new APIRequest().connectToMiddleTier(url,"POST");
            String response = org.apache.commons.io.IOUtils.toString(stream, "UTF-8");

            Toast.makeText(ProfileActivity.this, response, Toast.LENGTH_SHORT).show();

            Intent i = new Intent(this,MapsActivity.class);
            infoObject.setUser_name(NewUsername);
            i.putExtra("InfoToPassObj", infoObject);
            startActivity(i);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    //-----POTENTIAL PROFILE PICTURE FEATURE TO BE ADDED IN FUTURE UPDATES-----------
//    private void selectImage(Context context) {
//        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setTitle("Choose your profile picture");
//
//        builder.setItems(options, new DialogInterface.OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface dialog, int item) {
//
//                if (options[item].equals("Take Photo")) {
//                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                    picCapUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "pic_"+ String.valueOf(System.currentTimeMillis()) + ".jpg"));
//                    takePicture.putExtra(MediaStore.EXTRA_OUTPUT,
//                            FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID +".provider",
//                                    new File("pic_"+ String.valueOf(System.currentTimeMillis()) + ".jpg")));
//                    takePicture.putExtra(MediaStore.EXTRA_OUTPUT, picCapUri);
//                    startActivityForResult(takePicture, 0);
//
//                } else if (options[item].equals("Choose from Gallery")) {
//                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                    startActivityForResult(pickPhoto , 1);
//
//                } else if (options[item].equals("Cancel")) {
//                    dialog.dismiss();
//                }
//            }
//        });
//        builder.show();
//    }
//    private void performCrop(Uri picUri) {
//        try {
//            Intent cropIntent = new Intent("com.android.camera.action.CROP");
//            // indicate image type and Uri
//            cropIntent.setDataAndType(picUri, "image/*");
//            // set crop properties here
//            cropIntent.putExtra("crop", true);
//            // indicate aspect of desired crop
//            cropIntent.putExtra("aspectX", 1);
//            cropIntent.putExtra("aspectY", 1);
//            // indicate output X and Y
//            cropIntent.putExtra("outputX", 128);
//            cropIntent.putExtra("outputY", 128);
//            // retrieve data on return
//            cropIntent.putExtra("return-data", true);
//            // start the activity - we handle returning in onActivityResult
//            startActivityForResult(cropIntent, 5);
//        }
//        // respond to users whose devices do not support the crop action
//        catch (ActivityNotFoundException anfe) {
//            // display an error message
//            String errorMessage = "Whoops - your device doesn't support the crop action!";
//            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
//            toast.show();
//        }
//    }
//    public Uri getImageUri(Context inContext, Bitmap inImage) {
//        Bitmap OutImage = Bitmap.createScaledBitmap(inImage, 1000, 1000,true);
//        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), OutImage, "Image " + Calendar.getInstance().getTime(), null);
//        return Uri.parse(path);
//    }
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode != RESULT_CANCELED) {
//            switch (requestCode) {
//                case 0:
//                    if (resultCode == RESULT_OK && data != null) {
//                       // Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
//////                        profile_view.setImageBitmap(selectedImage);
//                       // Uri selectedImageUri = getImageUri(getApplicationContext(),selectedImage) ;
//                        performCrop(picCapUri);
//                    }
//
//                    break;
//                case 1:
//                    if (resultCode == RESULT_OK && data != null) {
//                        Uri selectedImage = data.getData();
//                        performCrop(selectedImage);
////                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
////                        if (selectedImage != null) {
////                            Cursor cursor = getContentResolver().query(selectedImage,
////                                    filePathColumn, null, null, null);
////                            if (cursor != null) {
////                                cursor.moveToFirst();
////
////                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
////                                String picturePath = cursor.getString(columnIndex);
////                                try {
////                                    profile_view.setImageBitmap(BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage)));
////                                } catch (FileNotFoundException e) {
////                                    e.printStackTrace();
////                                }
////                                //BitmapFactory.decodeFile(picturePath)
////                                cursor.close();
////                            }
////                        }
//
//                    }
//                    break;
//            }
//
//        }
//        if(requestCode==5){
//            if(data!=null){
//                Bundle extras = data.getExtras();
//                // get the cropped bitmap
//                Bitmap selectedBitmap = extras.getParcelable("data");
//                profile_view.setImageBitmap(selectedBitmap);
//            }
//        }
//    }
@Override
public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {

        case android.R.id.home:
            onBackPressed();

        default:
            //Default action
            return super.onOptionsItemSelected(item);

    }
}
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}