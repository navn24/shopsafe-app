package com.navn.safeshop;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RatingBar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.navn.safeshop.requests.GetAverageReviews;
import com.navn.safeshop.requests.GetCommentsForUser;
import com.navn.safeshop.requests.GetCompanyId;

import java.io.UnsupportedEncodingException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends Fragment {
    public Requests requestsClass = new Requests();
    public GetAverageReviews getAverageReviewData;
    public GetCompanyId getCompanyId;
    Integer companyId = 2;
    String companyName = "Default Company Name" ;
    private RequestQueue mQueue;
    String shortCompanyName = "Default Short Company Name" ;
    AverageReviews avgReviewObj ;
    RatingBar ratingBar1 ;
    RatingBar ratingBar2 ;
    RatingBar ratingBar3 ;
    RatingBar averageRatingBar;
    TextView averageRatingText;
    TextView numberOfReviewsText;
    Integer numOfReviews = 0;
    float average_review_1 ;
    float average_review_2 ;
    float average_review_3 ;
    InfoToPass infoObject ;
    private ArrayList<String> listOfAddresses = new ArrayList<>();
    private ArrayList<UserReviewObject> listOfReviews;
    private ArrayList<String> html_attrs_list;
    private String photoreference;
    private UserReviewAdapter arrayAdapter;
    private ListView listViewOfReviews;
    private boolean comingFromLeaveReview;
    private RatingBar leaveRatingBar;
    ViewPager2 viewPager;
    Button leaveReviewButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (getArguments() != null) {
            infoObject = (InfoToPass) getArguments().getSerializable("InfoToPassObj");
            comingFromLeaveReview = getArguments().getBoolean("comingFromLeaveReview");
        }

        mQueue = Volley.newRequestQueue(getContext());

        companyId = infoObject.getCompany_id();
        companyName = infoObject.getCompany_name();
        html_attrs_list = infoObject.getHtml_attrs();
        photoreference = infoObject.getPhoto_reference();

        System.out.println("ATTRS LIST: "+photoreference);
        System.out.println("BEFORE: "+companyName );
        shortCompanyName = companyName;





        if(comingFromLeaveReview){

        }else{
            getCompanyId = new GetCompanyId(shortCompanyName, infoObject.getCompany_address(), getContext());
            getCompanyId.setCompanyName(shortCompanyName);
            try {
                getCompanyId.execute().get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            companyId =  getCompanyId.getCompanyId() ;
            infoObject.setCompany_id(companyId);
            if(getCompanyId.isNewCompany()){

                final Dialog newCompanyDialog = new Dialog(getContext());

                newCompanyDialog.setContentView(R.layout.newbusiness_dialog);
                newCompanyDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                newCompanyDialog.show();
                Button continueButton = newCompanyDialog.findViewById(R.id.continue_button);
                continueButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        newCompanyDialog.dismiss();
                    }
                });
                infoObject.setIsNewCompany(true);
                Intent intent = new Intent(getActivity(),LeaveReviewActivity.class);
                intent.putExtra("InfoToPassObj", infoObject) ;
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                getActivity().finish();
            }
        }



        //Get the Average Review Data Class and execute the http request to get average review values for the specified company
        getAverageReviewData = new GetAverageReviews(companyId,getContext());
        try {
            getAverageReviewData.execute().get();
            System.out.println("Company Average Review: " + getAverageReviewData.getCompany_average_review());
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        listOfReviews = new ArrayList<>();
        arrayAdapter = new UserReviewAdapter(getContext(),R.layout.user_review_list, listOfReviews) ;


    }

    private void showCommentsForCompany() throws UnsupportedEncodingException {

        String url ="https://"+getString(R.string.local_dns)+"/comments/get?company_id=" + companyId ;


        //HTTP Get Request to Json file
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                arrayAdapter.clear();
                // remove existing markers from previous search

                try {
                    JSONArray jsonArray = response;
                    System.out.println("This is " +
                            "" + jsonArray);
                    numOfReviews = jsonArray.length();
                    numberOfReviewsText.setText("("+Integer.toString(numOfReviews)+")");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject CommentInfoObjects = jsonArray.getJSONObject(i);
                        System.out.println("his is the object " +
                                "" + CommentInfoObjects);
                        float user_average_review =(float) CommentInfoObjects.getDouble("user_average_rating");
                        String  UserName = CommentInfoObjects.getString("user_name");
                        String CommentText = CommentInfoObjects.getString("comment_text");
                        String commentDate = CommentInfoObjects.getString("comment_date");
                        System.out.println("This is the date: " + commentDate);

                        String str[] = CommentInfoObjects.getString("comment_date").split("-");
                        int day = Integer.parseInt(str[2]);
                        int month = Integer.parseInt(str[1]);
                        int year = Integer.parseInt(str[0]);
                        String formattedDate = Integer.toString(month)+"/"+Integer.toString(day)+"/"+Integer.toString(year);

                        

                        System.out.println("Day " + String.valueOf(day) + " Month " + String.valueOf(month) + " Year " + String.valueOf(year));

                        System.out.println(UserName+ CommentText);
                        if(user_average_review != 0 && !CommentText.equals("") && !CommentText.equals(GlobalAppClass.context.getString(R.string.comment_default_value))){
                            addReview(UserName,CommentText,user_average_review,formattedDate);
                        }else{
                            Log.d("Didn't Add to List", "User Name: " + UserName + "Review: " + user_average_review + "Comment: " + CommentText) ;
                        }


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) { @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String> params = new HashMap<String, String>();

            String auth = GlobalAppClass.context.getString(R.string.service1)+":"+GlobalAppClass.context.getString(R.string.service2);

            String encodedAuth = Base64.encodeToString(auth.getBytes(), android.util.Base64.DEFAULT);

            String authHeader = "Basic " + new String(encodedAuth);
            params.put("Authorization", authHeader);
            return params;
        }};
        mQueue.add(request);
    }
    private void addReview(String userName, String CommentText, float user_average_review, String date){
        UserReviewObject obj = new UserReviewObject(userName, CommentText, user_average_review, date);
        listOfReviews.add(obj);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflatedView =  inflater.inflate(R.layout.activity_main, container, false);
        listViewOfReviews = inflatedView.findViewById(R.id.CommentList);
        numberOfReviewsText = inflatedView.findViewById(R.id.numberOfReviewsText);
        averageRatingText = inflatedView.findViewById(R.id.average_rating_text);
        averageRatingBar = inflatedView.findViewById(R.id.average_rating_bar);
        leaveRatingBar = inflatedView.findViewById(R.id.leaveRatingBar);
        leaveReviewButton = inflatedView.findViewById(R.id.mainLeaveReview);
        leaveReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),LeaveReviewActivity.class);
                intent.putExtra("InfoToPassObj", infoObject) ;
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        listViewOfReviews.setAdapter(arrayAdapter);


        averageRatingBar.setRating(getAverageReviewData.getCompany_average_review());
        averageRatingText.setText(Float.toString(averageRatingBar.getRating()));


        try {
            showCommentsForCompany();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        leaveRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                Intent intent = new Intent(getActivity(),LeaveReviewActivity.class);
                intent.putExtra("InfoToPassObj", infoObject) ;
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        return  inflatedView;
    }
    public static MainActivity newInstance( InfoToPass infoObject, boolean comingFromLeaveReview) {
        MainActivity fragment = new MainActivity();
        Bundle args = new Bundle();
        args.putSerializable("InfoToPassObj", infoObject );
        args.putBoolean("comingFromLeaveReview", comingFromLeaveReview);
        fragment.setArguments(args);
        return fragment;
    }




}