package com.navn.safeshop.requests;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.navn.safeshop.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URLEncoder;

public class LinkAccounts extends AsyncTask<Void,Void,Void> {
    private boolean successful;
    private String idTokenString;
    private Context context;

    public LinkAccounts(String idTokenString, Context context){
        this.idTokenString = idTokenString;
        this.context = context;
    }


    @Override
    protected Void doInBackground(Void... voids) {
        try {
            idTokenString = URLEncoder.encode(idTokenString, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String accessUrl = "https://"+context.getString(R.string.local_dns)+"/user/linkAccount?idTokenString=" + idTokenString;
        String response = "Default Response" ;
        try {

            APIRequest apiRequest= new APIRequest();
            successful=apiRequest.isSuccessful();
            InputStream in =  apiRequest.connectToMiddleTier(accessUrl, "POST");
            response = org.apache.commons.io.IOUtils.toString(in, "UTF-8");
            Log.d("Response: ", response);
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
}
