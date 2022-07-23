package com.navn.safeshop.requests;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.navn.safeshop.AverageReviews;
import com.navn.safeshop.R;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.ProtocolException;

public class GetAverageReviews extends AsyncTask<Void, Void, Void> {
    private Integer company_Id;
    private AverageReviews avgReviewObj;
    private float average_review_1;
    private float average_review_2;
    private float average_review_3;
    private float company_average_review;

    private Context context;
    public GetAverageReviews(Integer company_Id, Context context) {
        this.company_Id = company_Id;
        this.context = context;
    }


    public float getCompany_average_review() {
        return company_average_review;
    }

    public float getAverage_review__1() {
        return average_review_1;
    }

    public float getAverage_review__2() {
        return average_review_2;
    }

    public float getAverage_review__3() {
        return average_review_3;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        String accessUrl = "https://"+ context.getString(R.string.local_dns)+"/review/getaverage?companyId=" + company_Id.toString() ;


        String response = new String("Default Message");
        try {
            APIRequest apiRequest= new APIRequest();
            InputStream in =  apiRequest.connectToMiddleTier(accessUrl);
            response = org.apache.commons.io.IOUtils.toString(in, "UTF-8");
        } catch (MalformedURLException | ProtocolException e ) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            avgReviewObj = objectMapper.readValue(response, AverageReviews.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        average_review_1 = avgReviewObj.getAverage_review_1();
        average_review_2 = avgReviewObj.getAverage_review_2();
        average_review_3 = avgReviewObj.getAverage_review_3();
        company_average_review = avgReviewObj.getCompany_average_review();
        Log.println(Log.VERBOSE,"Company Average Review",Float.toString(company_average_review));
        Log.d("OptionsInfo", response);
        return null;
    }

    public void setCompany_Id(Integer company_Id) {
        this.company_Id = company_Id;
    }
}

