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

public class ValidateUserByName extends AsyncTask<Void, Void, Void> {


    private ValidateUserObject validateUserObject;
    private String userFound;
    private String passwordMatches;
    private String errorMessage;
    private String userName;
    private String password;
    private Integer user_id ;
    private String email;

    public ValidateUserByName(String userName, String password, Context context) {
        this.userName = userName;
        this.password = password;
        this.context = context;
    }

    private Integer google_login;
    private Context context;

    @Override
    protected Void doInBackground(Void... voids) {

        try {
            userName = URLEncoder.encode(userName, "UTF-8");
            password = URLEncoder.encode(password, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String accessUrl = "https://"+ context.getString(R.string.local_dns)+"/user/validateByName?name=" + userName + "&password=" + password;

        System.out.println(accessUrl);
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
        userName = validateUserObject.getUser_name();
        email = validateUserObject.getEmail();
        google_login = validateUserObject.getGoogle_login();


        return null;
    }

    public String getEmail() {
        return email;
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
    public void setUserName(String userName){ this.userName=userName;}
    public String getUserName() {
        return userName;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public Integer getGoogle_login() {
        return google_login;
    }
    public Integer getUser_id() {
        return user_id;
    }

}
