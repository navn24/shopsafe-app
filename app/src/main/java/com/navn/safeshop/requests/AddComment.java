package com.navn.safeshop.requests;

import android.content.Context;
import android.os.AsyncTask;

import com.navn.safeshop.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddComment extends AsyncTask<Void,Void,Void> {
    private Integer company_id;
    private Integer user_id;
    private String user_name;
    private String comment_text;
    private boolean successful;
    private Context context;


    public void setCompany_id(Integer company_id) {
        this.company_id = company_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public void setComment_text(String comment_text) {
        this.comment_text = comment_text;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public AddComment(Integer company_id, Integer user_id, String comment_text, String user_name, Context context) {
        this.company_id = company_id;
        this.user_id = user_id;
        this.comment_text = comment_text;
        this.user_name = user_name;
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        //Encode user name and comment text to utf-8 before sending to middle tier
        try {
            comment_text = URLEncoder.encode(comment_text, "UTF-8");
            user_name = URLEncoder.encode(user_name, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Date todayDate = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String todayString = formatter.format(todayDate);
        System.out.println("Date:" + todayString );
        String accessUrl = "https://"+context.getString(R.string.local_dns)+"/comments/add?company_id=" + company_id + "&user_id=" + user_id + "&comment_text=" + comment_text + "&user_name=" + user_name + "&comment_date=" + todayString; ;
        String response = "Default Response" ;

       try {
           InputStream in =  new APIRequest().connectToMiddleTier(accessUrl, "POST");
            response = org.apache.commons.io.IOUtils.toString(in, "UTF-8");
       } catch (MalformedURLException | ProtocolException e) {
           e.printStackTrace();
       } catch (IOException e) {
           e.printStackTrace();
       }
        return null;
    }

    }

