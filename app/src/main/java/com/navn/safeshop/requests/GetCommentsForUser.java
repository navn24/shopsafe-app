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

public class GetCommentsForUser extends AsyncTask<Void,Void,Void> {
    private Integer user_id;
    private boolean successful;
    private Integer company_id;
    private String user_name;
    private String comment_text;
    private String dateString;

    private Context context;


    private float user_average_review;

   public GetCommentsForUser(Integer user_id, Integer company_id, Context context)
   {
       this.user_id = user_id ;
       this.company_id = company_id;
       this.context = context;
   }
    public boolean isSuccessful() {
        return successful;
    }


    public float getUser_average_review() {
        return user_average_review;
    }
    public String getUser_name() {
        return user_name;
    }

    public String getComment_text() {
        return comment_text;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        String accessUrl = "https://"+ context.getString(R.string.local_dns)+"/comments/getForUser?user_id=" + user_id + "&company_id=" + company_id;
        String response = "Default Response";
        try {
            APIRequest apiRequest= new APIRequest();
            successful=apiRequest.isSuccessful();
            InputStream in =  apiRequest.connectToMiddleTier(accessUrl);
            response = org.apache.commons.io.IOUtils.toString(in, "UTF-8");

            JSONObject jsonObject = new JSONObject(response);
            user_name = jsonObject.getString("user_name");
            comment_text = jsonObject.getString("comment_text") ;
            user_average_review = (float) jsonObject.getDouble("user_average_review") ;
            dateString = jsonObject.getString("comment_date");
            System.out.println("This is the date: " + dateString);
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
