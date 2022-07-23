package com.navn.safeshop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.navn.safeshop.requests.APIRequest;
import com.navn.safeshop.requests.ChangePassword;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.ExecutionException;

public class ChangePasswordActivity extends AppCompatActivity {
    Button ConfirmPasswordChangeButton;
    EditText NewPasswordChange;
    EditText ConfirmNewPasswordChange;
    InfoToPass infoObject;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        infoObject = (InfoToPass) getIntent().getSerializableExtra("InfoToPassObj");
        prefs = getSharedPreferences("checkbox", MODE_PRIVATE);
        editor = prefs.edit();

        NewPasswordChange = findViewById(R.id.NewPasswordChange);
        ConfirmNewPasswordChange = findViewById(R.id.confirmNewPasswordChange);
        ConfirmPasswordChangeButton = findViewById(R.id.confirmChangePassword);
        ConfirmPasswordChangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!NewPasswordChange.getText().toString().equals(ConfirmNewPasswordChange.getText().toString())) {
                    Toast.makeText(ChangePasswordActivity.this,"Passwords don't match!",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(NewPasswordChange.getText().toString().equals("")){
                    Toast.makeText(ChangePasswordActivity.this,"Passwords can't be empty!",Toast.LENGTH_SHORT).show();
                    return;
                }
                    if(NewPasswordChange.getText().toString().contains(" ")){
                        Toast.makeText(ChangePasswordActivity.this,"Passwords can't contain spaces!",Toast.LENGTH_SHORT).show();
                        return;
                    }
                if(NewPasswordChange.getText().toString().length()>25){
                    Toast.makeText(ChangePasswordActivity.this, "Password can't be longer than 25 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(String.valueOf(NewPasswordChange.getText().toString()).matches("^.*[^a-zA-Z0-9!@#$%&-_].*$")){
                    Toast.makeText(getApplicationContext(), "Invalid characters in password!", Toast.LENGTH_SHORT).show();
                    return;
                }
                    try {
                       ChangePassword changePasswordReq =  new ChangePassword(infoObject.getUser_id(),NewPasswordChange.getText().toString());
                       changePasswordReq.execute().get();
                       editor.putString("password", NewPasswordChange.getText().toString());
                       editor.apply();
                        Toast.makeText(GlobalAppClass.context, "Updated Password Successfully!", Toast.LENGTH_SHORT).show();
                        ChangePasswordActivity.super.onBackPressed();
                    } catch (ExecutionException | InterruptedException e) {
                        Toast.makeText(GlobalAppClass.context, "Could not update password", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


//    private void ChangePassword(int UserId, String NewPassword) throws UnsupportedEncodingException {
//
//        String url = "https://" + getString(R.string.local_dns)+"/user/updatePassword?user_id="+UserId+ "&password="+ NewPassword;
//
//        try {
//            InputStream stream =  new APIRequest().connectToMiddleTier(url,"POST");
//            String response = org.apache.commons.io.IOUtils.toString(stream, "UTF-8");
//
//            Toast.makeText(ChangePasswordActivity.this, response, Toast.LENGTH_SHORT).show();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }

}