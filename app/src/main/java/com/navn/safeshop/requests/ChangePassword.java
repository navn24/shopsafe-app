package com.navn.safeshop.requests;

import android.os.AsyncTask;
import android.widget.Toast;

import com.navn.safeshop.ChangePasswordActivity;
import com.navn.safeshop.GlobalAppClass;
import com.navn.safeshop.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;

public class ChangePassword extends AsyncTask<Void,Void, Void>  {
    private int user_id;
    private String password;

    public ChangePassword(int user_id, String password) {
        this.user_id = user_id;
        this.password = password;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            password = URLEncoder.encode(password, "UTF-8");

            String url = "https://" + GlobalAppClass.context.getString(R.string.local_dns)+"/user/updatePassword?user_id="+user_id+ "&password="+ password;
        APIRequest apiRequest= new APIRequest();
        InputStream in =  apiRequest.connectToMiddleTier(url, "POST");
        String response = org.apache.commons.io.IOUtils.toString(in, "UTF-8");

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
