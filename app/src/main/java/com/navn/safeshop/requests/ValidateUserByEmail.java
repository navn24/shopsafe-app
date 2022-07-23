package com.navn.safeshop.requests;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.navn.safeshop.R;
import com.navn.safeshop.ValidateUserObject;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URLEncoder;

public class ValidateUserByEmail extends AsyncTask<Void, Void, Void> {



    private String userFound;
    private String passwordMatches;
    private String errorMessage;
    private String userEmail;
    private String password;
    private Integer user_id;
    private Integer google_login;
    private String user_name;
    private Context context;
    private ValidateUserObject validateUserObject;

    public ValidateUserByEmail(String userEmail, String password, Context context) {
        this.userEmail = userEmail;
        this.password = password;
        this.context = context;
    }
    @Override
    protected Void doInBackground(Void... voids) {
        try {
            password = URLEncoder.encode(password, "UTF-8");
            userEmail = URLEncoder.encode(userEmail, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String accessUrl = "https://"+ context.getString(R.string.local_dns)+"/user/validateByEmail?email=" + userEmail + "&password=" + password;
        Log.d("URL:", accessUrl);
        String response = "Default Response" ;
        try {

            APIRequest apiRequest= new APIRequest();
            InputStream in =  apiRequest.connectToMiddleTier(accessUrl);
            response = org.apache.commons.io.IOUtils.toString(in, "UTF-8");
        } catch (MalformedURLException | ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("URL:", response);


        ObjectMapper objectMapper = new ObjectMapper();
        try {
            validateUserObject = objectMapper.readValue(response, ValidateUserObject.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        userFound = validateUserObject.getUserFound();
        passwordMatches = validateUserObject.getPasswordMatches();
        errorMessage = validateUserObject.getErrorMessage();
        user_id = validateUserObject.getUser_id();
        user_name = validateUserObject.getUser_name();
        google_login = validateUserObject.getGoogle_login();

        return null;
    }
    //Getters and Setters
    public Integer getGoogle_login() {
        return google_login;
    }
    public String getUser_name() {
        return user_name;
    }
    public Integer getUser_id() {
        return user_id;
    }
    public String isUserFound() {
        return userFound;
    }
    public String isPasswordMatches() {
        return passwordMatches;
    }
    public String getErrorMessage() {
        return errorMessage;
    }
    public void setUserEmail(String userEmail){ this.userEmail=userEmail;}
    public void setPassword(String password) {
        this.password = password;
    }


}

