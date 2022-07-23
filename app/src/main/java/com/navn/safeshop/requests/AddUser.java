package com.navn.safeshop.requests;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.navn.safeshop.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URLEncoder;

public class AddUser extends AsyncTask<Void,Void, Void> {
    private String name;
    private String email;
    private String password;
    private boolean successful;
    private Context context;
    @Override
    protected Void doInBackground(Void... voids) {

        try {
            name = URLEncoder.encode(name, "UTF-8");
            email = URLEncoder.encode(email, "UTF-8");
            email = URLEncoder.encode(email, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String accessUrl = "https://"+ context.getString(R.string.local_dns)+"/user/add?user_name=" + name + "&email=" + email + "&password=" + password ;
        String response = "Default Response" ;
        try {

            APIRequest apiRequest= new APIRequest();
            successful=apiRequest.isSuccessful();
            InputStream in =  apiRequest.connectToMiddleTier(accessUrl, "POST");
            response = org.apache.commons.io.IOUtils.toString(in, "UTF-8");
        } catch (MalformedURLException | ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public AddUser(String name, String email, String password, Context context) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.context = context;
    }

}
