package com.navn.safeshop.requests;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.navn.safeshop.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URLEncoder;

public class GetCompanyId extends AsyncTask<Void, Void, Void> {
    private String companyName;
    private boolean isNewCompany;

    private String companyAddress;
    private Integer companyId;

    private Context context;
    public GetCompanyId(String companyName, String companyAddress, Context context){
        this.companyName = companyName ;
        this.companyAddress = companyAddress;
        this.context = context;
    }
    @Override
    protected Void doInBackground(Void... voids) {
        try {
            companyName = URLEncoder.encode(companyName, "UTF-8");
            companyAddress = URLEncoder.encode(companyAddress, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String accessUrl = "https://"+ context.getString(R.string.local_dns)+"/company/getCompanyId?company_name=" + companyName + "&company_address=" + companyAddress;
        String response = "Default Response" ;
        try {
            APIRequest apiRequest= new APIRequest();
            InputStream in =  apiRequest.connectToMiddleTier(accessUrl);
            response = org.apache.commons.io.IOUtils.toString(in, "UTF-8");
            JSONObject jsonObject = new JSONObject(response);
            companyId = jsonObject.getInt("company_id") ;
            isNewCompany = jsonObject.getBoolean("newCompany");
        } catch (MalformedURLException | ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return null;
    }
    public Integer getCompanyId() {
        return companyId;
    }
    public boolean isNewCompany() {
        return isNewCompany;
    }
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }
}
