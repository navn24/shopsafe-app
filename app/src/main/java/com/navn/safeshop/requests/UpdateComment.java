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
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;


public class UpdateComment extends AsyncTask<Void,Void,Void> {
    private Integer company_id;
    private Integer user_id;
    private String user_name;
    private String comment_text;
    private boolean successful;

    private Context context;

    public boolean isSuccessful() {
        return successful;
    }

    public UpdateComment(Integer company_id, Integer user_id, String comment_text, String user_name, Context context) {
        this.company_id = company_id;
        this.user_id = user_id;
        this.comment_text = comment_text;
        this.user_name = user_name;
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Date todayDate = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String todayString = formatter.format(todayDate);
        try {
            comment_text = URLEncoder.encode(comment_text, "UTF-8");
            user_name = URLEncoder.encode(user_name, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String accessUrl = "https://"+ context.getString(R.string.local_dns)+"/comments/update?company_id=" + company_id + "&user_id=" + user_id + "&comment_text=" + comment_text + "&user_name=" + user_name + "&comment_date=" + todayString;
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
