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

public class GetRatingsForUser extends AsyncTask<Void,Void,Void> {
    private Integer user_id ;
    private Integer company_id;
    private float category1_rating;
    private float category2_rating;
    private float category3_rating;
    private boolean hasLeftReview;
    private boolean successful;

    private Context context;
    public GetRatingsForUser(Integer user_id, Integer company_id, Context context){
        this.user_id = user_id ;
        this.company_id = company_id;
        this.context = context;
    }

    public boolean HasLeftReview() {
        return hasLeftReview;
    }

    public float getCategory1_rating() {
        return category1_rating;
    }

    public float getCategory2_rating() {
        return category2_rating;
    }

    public float getCategory3_rating() {
        return category3_rating;
    }

    public boolean isSuccessful() {
        return successful;
    }

    protected Void doInBackground(Void... voids) {
        String accessUrl = "https://"+ context.getString(R.string.local_dns)+"/review/getForUser?user_id=" + user_id + "&company_id=" + company_id;
        String response = "Default Response";
        try {
            APIRequest apiRequest= new APIRequest();
            successful=apiRequest.isSuccessful();
            InputStream in =  apiRequest.connectToMiddleTier(accessUrl);
            response = org.apache.commons.io.IOUtils.toString(in, "UTF-8");

            JSONObject jsonObject = new JSONObject(response);
            category1_rating = (float) jsonObject.getDouble("category1_rating") ;
            category2_rating = (float) jsonObject.getDouble("category2_rating") ;
            category3_rating = (float) jsonObject.getDouble("category3_rating") ;
            if(category1_rating !=0.0f && category2_rating != 0.0f && category3_rating != 0.0f){
                hasLeftReview = true;
            }else{
                hasLeftReview = false;
            }
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
