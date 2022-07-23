package com.navn.safeshop.requests;

import android.content.Context;
import android.os.AsyncTask;

import com.navn.safeshop.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class GooglePostIdToken extends AsyncTask<Void,Void,Void> {
    private boolean successful ;
    private String idTokenString;



    private Integer user_id;
    private String user_name;
    private String email;
    private String error_message;
    private Boolean new_account;

    private Context context;

    public GooglePostIdToken(String idTokenString, Context context) {
        this.idTokenString = idTokenString;
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Date todayDate = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String todayString = formatter.format(todayDate);
        System.out.println("Date:" + todayString );
        try {
            idTokenString = URLEncoder.encode(idTokenString, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String accessUrl = "https://"+ context.getString(R.string.local_dns)+"/user/googleSignIn?idTokenString=" + idTokenString ;
        String response = "Default Response" ;
        try {
            APIRequest apiRequest= new APIRequest();
            successful=apiRequest.isSuccessful();
            InputStream in =  apiRequest.connectToMiddleTier(accessUrl);
            response = org.apache.commons.io.IOUtils.toString(in, "UTF-8");
            JSONObject jsonObject = new JSONObject(response);
            user_id = jsonObject.getInt("user_id");
            user_name = jsonObject.getString("user_name");
            email = jsonObject.getString("email");
            error_message = jsonObject.getString("error_message");
            new_account = jsonObject.getBoolean("new_account");
            System.out.println("User ID: " + user_id + " User Name: " + user_name + " New Account " + new_account);
        } catch (MalformedURLException | ProtocolException e) {
            e.printStackTrace();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isSuccessful() {
        return successful;
    }
    public void setIdTokenString(String idTokenString) {
        this.idTokenString = idTokenString;
    }
    public Integer getUser_id() {
        return user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getEmail() {
        return email;
    }

    public String getError_message() {
        return error_message;
    }

    public Boolean getNew_account() {
        return new_account;
    }
}
