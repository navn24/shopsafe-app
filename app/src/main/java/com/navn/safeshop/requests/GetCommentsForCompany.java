package com.navn.safeshop.requests;

import android.content.Context;
import android.os.AsyncTask;

import com.navn.safeshop.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.ProtocolException;

public class GetCommentsForCompany extends AsyncTask<Void,Void,Void> {
    private Integer company_id;

    private Context context;

    @Override
    protected Void doInBackground(Void... voids) {
        String accessUrl = "https://"+ context.getString(R.string.local_dns)+"/comments/get?company_id=" + company_id ;
        String response = "Default Response" ;
        try {
            APIRequest apiRequest= new APIRequest();
            InputStream in =  apiRequest.connectToMiddleTier(accessUrl);
            response = org.apache.commons.io.IOUtils.toString(in, "UTF-8");

            JSONObject jsonObject = new JSONObject(response) ;

        } catch (MalformedURLException | ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return null;
    }
}
