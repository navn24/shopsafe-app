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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class PostReview extends AsyncTask<Void,Void,Void>
{
    private Integer user_id;
    private String user_name;
    private Integer companyId;
    private float review_1;
    private float review_2;
    private float review_3;
    private Context context;

    public PostReview(Integer user_id, String user_name, Integer companyId, float review_1, float review_2, float review_3, Context context)
    {
        this.user_id = user_id ;
        this.user_name = user_name ;
        this.companyId = companyId ;
        this.review_1 = review_1;
        this.review_2 = review_2;
        this.review_3 = review_3 ;
        this.context = context;
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
        String accessUrl = "https://"+ context.getString(R.string.local_dns)+"/review/add?user_id=" + user_id + "&user_name=" + user_name + "&companyId=" +
                companyId+"&comment_date=" + todayString+"&category1_rating="+review_1+"&category2_rating="+review_2+"&category3_rating="+review_3;
        String response = new String("Default Message");
        Log.d("Access URL:", accessUrl) ;
        try {

            APIRequest apiRequest= new APIRequest();
            InputStream in =  apiRequest.connectToMiddleTier(accessUrl,"POST");
            response = org.apache.commons.io.IOUtils.toString(in, "UTF-8");
        } catch (MalformedURLException | ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d("OptionsInfo", response) ;

        return null;

    }






}
