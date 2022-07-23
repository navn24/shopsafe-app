package com.navn.safeshop.requests;

import android.content.Context;
import android.os.AsyncTask;

import com.navn.safeshop.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URLEncoder;

public class CheckIfEmailFound extends AsyncTask<Void,Void,Void> {


    private String email;
    private Boolean emailFound;
    private Context context;

    public CheckIfEmailFound(String email, Context context) {
        this.email = email;
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            email = URLEncoder.encode(email, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String accessUrl = "https://"+ context.getString(R.string.local_dns)+"/user/emailFound?email=" + email ;
        String response = "Default Response" ;
        try {

            APIRequest apiRequest= new APIRequest();
            InputStream in =  apiRequest.connectToMiddleTier(accessUrl);
            response = org.apache.commons.io.IOUtils.toString(in, "UTF-8");
            emailFound = Boolean.valueOf(response);
        } catch (MalformedURLException | ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e ) {
            e.printStackTrace();
        }
        return null;
    }

    public Boolean getEmailFound() {
        return emailFound;
    }
}
