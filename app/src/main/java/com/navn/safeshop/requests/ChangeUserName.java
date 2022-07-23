package com.navn.safeshop.requests;

import android.os.AsyncTask;

import com.navn.safeshop.GlobalAppClass;
import com.navn.safeshop.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;

public class ChangeUserName extends AsyncTask<Void,Void, Void> {
    private int user_id;
    private String user_name;

    public ChangeUserName(int user_id, String user_name) {
        this.user_id = user_id;
        this.user_name = user_name;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            user_name = URLEncoder.encode(user_name, "UTF-8");

            String url = "https://" + GlobalAppClass.context.getString(R.string.local_dns)+"/user/updateName?user_id="+user_id+ "&user_name="+ user_name;
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
    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }


}
