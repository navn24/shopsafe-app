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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class UpdateRating extends AsyncTask<Void,Void,Void> {
    private Integer company_id;
    private Integer user_id;
    private String user_name;
    private float category1_rating;
    private float category2_rating;
    private float category3_rating;
    private boolean successful;
    private Context context;

    public UpdateRating(Integer user_id, String user_name, Integer company_id, float category1_rating, float category2_rating, float category3_rating, Context context){
        this.company_id = company_id ;
        this.user_id = user_id;
        this.user_name = user_name;
        this.category1_rating = category1_rating;
        this.category2_rating = category2_rating;
        this.category3_rating = category3_rating;
        this.context = context;
    }

    public boolean isSuccessful() {
        return successful;
    }


    @Override
    protected Void doInBackground(Void... voids) {
        Date todayDate = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String todayString = formatter.format(todayDate);
        System.out.println("Date:" + todayString );
        try {
            user_name = URLEncoder.encode(user_name, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String accessUrl = "https://"+ context.getString(R.string.local_dns)+"/review/update?company_id=" + company_id + "&user_id=" + user_id + "&user_name=" + user_name + "&comment_date=" + todayString + "&category1_rating=" + category1_rating
        + "&category2_rating=" + category2_rating + "&category3_rating=" + category3_rating;
        String response = "Default Response" ;
        try {

            APIRequest apiRequest= new APIRequest();
            successful=apiRequest.isSuccessful();
            InputStream in =  apiRequest.connectToMiddleTier(accessUrl,"POST");
            response = org.apache.commons.io.IOUtils.toString(in, "UTF-8");
        } catch (MalformedURLException | ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
