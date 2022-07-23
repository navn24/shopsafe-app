package com.navn.safeshop;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.navn.safeshop.requests.AddComment;
import com.navn.safeshop.requests.GetCommentsForUser;
import com.navn.safeshop.requests.GetRatingsForUser;
import com.navn.safeshop.requests.PostReview;
import com.navn.safeshop.requests.UpdateComment;
import com.navn.safeshop.requests.UpdateRating;

public class LeaveReviewActivity extends AppCompatActivity {
    Integer companyId ;
    Integer user_id;
    String user_name;
    String companyName;
    String shortCompanyName;
    RatingBar ratingBar1 ;
    RatingBar ratingBar2 ;
    RatingBar ratingBar3 ;
    float review_1 ;
    float review_2 ;
    float review_3 ;
    EditText commentTextField;
    boolean hasAlreadyLeftComment = false ;
    boolean hasAlreadyLeftRating = false;
    boolean isNewCompany;
    InfoToPass infoObject;
    LoggedIntoAppInfo loginObject;
    boolean isLoggedIn;
    boolean restoreSessionInfo;
     GetCommentsForUser getCommentsForUserInstance;

     // @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_review);
       // getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Get Extras from intent that called this activity
        Bundle extras = getIntent().getExtras();
        infoObject = (InfoToPass) getIntent().getSerializableExtra("InfoToPassObj");
        loginObject = (LoggedIntoAppInfo) getIntent().getSerializableExtra("LoggedIntoAppInfo");

        isLoggedIn = infoObject.isLoggedIn();


        companyId = infoObject.getCompany_id();
        companyName = infoObject.getCompany_name();
        isNewCompany = infoObject.getIsNewCompany();
        //Shorten the company name for use in the database and to set the title of the action bar
        if(companyName.contains("\n")){
            shortCompanyName = companyName.substring(0, companyName.indexOf('\n')) ;
        }else{
            shortCompanyName = companyName;
        }
        getSupportActionBar().setTitle(shortCompanyName);
        if(isNewCompany && loginObject==null){
            //If login object isnt null, it means that the user came from the login activity, meaning that they have already seen the pop before logging in,
            //so the popup doesn't need to be shown if they have already seen it
            MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(LeaveReviewActivity.this, R.style.MyThemeOverlayAlertDialog);

            dialogBuilder.setTitle("You are the first one to leave a review!").setMessage("Please consider leaving a review")
                    .setPositiveButton("Will do!", /* listener = */ new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface currentDialog, int which) {
                            currentDialog.dismiss();
                        }
                    });
            AlertDialog alertDialog = dialogBuilder.create();
            dialogBuilder.show();
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(alertDialog.getWindow().getAttributes());
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            alertDialog.getWindow().setAttributes(layoutParams);
        }

        Log.d("COMPANY Id = ", Integer.toString(companyId));


        //Get references to objects in the activity layout
        ratingBar1 = (RatingBar) findViewById(R.id.ratingBar1);
        ratingBar2 = (RatingBar) findViewById(R.id.ratingBar2);
        ratingBar3 = (RatingBar) findViewById(R.id.ratingBar3);
         commentTextField = (EditText) findViewById(R.id.commentTextField) ;
        Button submitCommentButton = (Button) findViewById(R.id.submit_comment);

         if(isLoggedIn) {
             //Only execute the following code if user is logged in
             Log.d("Debug", "LOGGED IN!!!");

             user_id = infoObject.getUser_id();
             user_name = infoObject.getUser_name();
             Log.d("Debug", "User_Id: "+user_id);
             Log.d("Debug", "User_name: " + user_name);

             if (loginObject != null) {
                 if (loginObject.isFromLeaveReviewActivity()) {

                     restoreSessionInfo = true;


                 } else {
                     Log.d("Debug", "Why did LeaveReviewActivity open?");
                 }
             }
             getCommentsForUserInstance = new GetCommentsForUser(user_id, companyId, getApplicationContext());
             try {
                 getCommentsForUserInstance.execute().get();
             } catch (ExecutionException e) {
                 e.printStackTrace();
             } catch (InterruptedException e) {
                 e.printStackTrace();
             }

                     //Get Comments that the user left for the company using the users id and the company id

                 if (!getCommentsForUserInstance.getComment_text().equals("")) {
                     //User has already left comment if comment text is ANYTHING other than "", which should only happen if comments have never been left
                     //before
                     if(!restoreSessionInfo) {


                         if (getCommentsForUserInstance.getComment_text().equals(getApplicationContext().getString(R.string.comment_default_value))) {
                             commentTextField.setText("");
                         } else {
                             commentTextField.setText(getCommentsForUserInstance.getComment_text());
                         }
                     }
                     submitCommentButton.setText("Change Review");
                     //commentTextView.setText("Change Comment");
                     hasAlreadyLeftComment = true;
                     Log.d("Comment Text BOIS!", getCommentsForUserInstance.getComment_text());
                     Log.d("Comment Text", "YO THERE IS NO COMMENT RETURNED BY THE SERVER");
                 } else {
                     hasAlreadyLeftComment = false;
                     if(!restoreSessionInfo){
                         commentTextField.setText("");
                     }
                     System.out.println("PERSON hasn't left a comment before!! ");
                 }
                 if(restoreSessionInfo){
                     commentTextField.setText(loginObject.getComment());
                     Log.d("Debug", "Restore comment successfull:" + loginObject.getComment() + "!");
                 }


             final GetRatingsForUser getRatingsForUser = new GetRatingsForUser(user_id, companyId, getApplicationContext());
             try {
                 getRatingsForUser.execute().get();
             } catch (ExecutionException | InterruptedException e) {
                 e.printStackTrace();
             }
                     //Get Ratings that the user left for the company using the users id and the company id

                     TextView ratingTextView = (TextView) findViewById(R.id.ratingTextView);
                     //If user has already left a rating, set boolean to true and set rating bars to previously given ratings
                     if (getRatingsForUser.HasLeftReview()) {
                         if(!restoreSessionInfo){
                             ratingBar1.setRating(getRatingsForUser.getCategory1_rating());
                             ratingBar2.setRating(getRatingsForUser.getCategory2_rating());
                             ratingBar3.setRating(getRatingsForUser.getCategory3_rating());
                         }

                         hasAlreadyLeftRating = true;
                         Log.d("Review Text BOIS!", Float.toString(getRatingsForUser.getCategory1_rating()));
                     }
                if(restoreSessionInfo){

                 ratingBar1.setRating(loginObject.getReview_1());
                     ratingBar2.setRating(loginObject.getReview_2());
                     ratingBar3.setRating(loginObject.getReview_3());
                     Log.d("Debug", "HERE IS REVIEW 1!!!" + loginObject.getReview_1());


                 }
             //Set On Click Listener to submit comment button
             submitCommentButton.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                     //If user has already left comment, allow them to update their previously left comment, else let them add a comment
                     if (getCommentsForUserInstance.getComment_text().equals(commentTextField.getText().toString())
                             && getRatingsForUser.getCategory1_rating() == ratingBar1.getRating()
                             && getRatingsForUser.getCategory2_rating() == ratingBar2.getRating()
                             && getRatingsForUser.getCategory3_rating() == ratingBar3.getRating()) {
                         Toast.makeText(getApplicationContext(), "No part of the review has been changed!", Toast.LENGTH_LONG).show();
                         return;
                     }
                     if (String.valueOf(commentTextField.getText().toString()).matches("^.*[^a-zA-Z0-9!@#$%&-_ ].*$")) {
                         Toast.makeText(getApplicationContext(), "Invalid characters in comment!", Toast.LENGTH_SHORT).show();
                         return;
                     }
                     if (commentTextField.getText().toString().length() > 100) {
                         Toast.makeText(LeaveReviewActivity.this, "Comment can't be longer than 100 characters!", Toast.LENGTH_SHORT).show();
                         commentTextField.clearFocus();
                         return;
                     }
                     if (!getCommentsForUserInstance.getComment_text().equals(commentTextField.getText().toString())) {
                         //If comment has been changed from previous comment, then execute the following code
                         if (hasAlreadyLeftComment == false) {

                             System.out.println("Has not left a comment!");

                             AddComment addCommentInstance = new AddComment(companyId, user_id, commentTextField.getText().toString(), user_name, getApplicationContext());
                             //Post comment to database
                             try {
                                 addCommentInstance.execute().get();
                             } catch (ExecutionException e) {
                                 e.printStackTrace();
                             } catch (InterruptedException e) {
                                 e.printStackTrace();
                             }

                         } else {
                             //If the user has already left a comment, update their comment instead
                             UpdateComment updateCommentInstance = new UpdateComment(companyId, user_id, commentTextField.getText().toString(), user_name, getApplicationContext());
                             try {
                                 updateCommentInstance.execute().get();
                             } catch (ExecutionException e) {
                                 e.printStackTrace();
                             } catch (InterruptedException e) {
                                 e.printStackTrace();
                             }
                         }
                     } else {
                         Log.d("Debug", "None of the comments have changed!");
                     }


                     //Get Ratings from rating bars, and post review to database
                     review_1 = ratingBar1.getRating();
                     review_2 = ratingBar2.getRating();
                     review_3 = ratingBar3.getRating();
                     if (!(getRatingsForUser.getCategory1_rating() == review_1) || !(getRatingsForUser.getCategory2_rating() == review_2) || !(getRatingsForUser.getCategory3_rating() == review_3)) {

                         //If at least one of the ratings have changed, execute this code
                         if (review_1 != 0 && review_2 != 0 && review_3 != 0) {
                             if (hasAlreadyLeftRating) {

                                 UpdateRating updateRatingInstance = new UpdateRating(user_id, user_name, companyId, review_1, review_2, review_3, getApplicationContext());
                                 try {
                                     updateRatingInstance.execute().get();
                                 } catch (ExecutionException e) {
                                     e.printStackTrace();
                                 } catch (InterruptedException e) {
                                     e.printStackTrace();
                                 }

                             } else {
                                 PostReview postReviewInstance = new PostReview(user_id, user_name, companyId, review_1, review_2, review_3, getApplicationContext());
                                 //Post Review to Database
                                 try {
                                     postReviewInstance.execute().get();
                                 } catch (ExecutionException e) {
                                     e.printStackTrace();
                                 } catch (InterruptedException e) {
                                     e.printStackTrace();
                                 }

                             }
                             Log.d("Reviews:", review_1 + "" + review_2 + "" + review_3 + "CompId " + companyId);
                             //Open the previous activity (activity containing average reviews)  - e.g. MainActivity.java
                         } else {
                             Toast.makeText(getApplicationContext(), "You must leave a rating for all categories ", Toast.LENGTH_LONG).show();
                             return;
                         }
                     } else {
                         Log.d("Debug", "None of the ratings have changed!");
                     }
                     //Open the previous activity (activity containing average reviews)  - e.g. MainActivity.java
                     OpenPreviousActivity();
                 }
             });
         }else{
             submitCommentButton.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     review_1 = ratingBar1.getRating();
                     review_2 = ratingBar2.getRating();
                     review_3 = ratingBar3.getRating();
                     LoggedIntoAppInfo info = new LoggedIntoAppInfo(review_1, review_2,review_3, commentTextField.getText().toString());
                     OpenLoginActivity(infoObject,info);
                     //If user successfully logs in, open previous activity, but set LoggedIntoAppInfo.rating = what user had so they can continue where they left off
                    //Remember to check for LoggedIntoAppInfo in login activity, and also maps activity, and also leaveReviewActivity
                 }
             });
         }
    }
    public void OpenLoginActivity(InfoToPass infoObject, LoggedIntoAppInfo loggedIntoAppInfo){
        Intent intent  = new Intent(this, LoginActivity.class);
        intent.putExtra("InfoToPassObj", infoObject);
        intent.putExtra("LoggedIntoAppInfo", loggedIntoAppInfo);
        startActivity(intent);
    }
    public void OpenPreviousActivity(){
        //Close previous running instance of CompanyInfo:
        Intent closeActivityIntent = new Intent(CompanyInfo.FINISH_ALERT);
        LeaveReviewActivity.this.sendBroadcast(closeActivityIntent);
        //Open new instance of CompanyInfo
        Intent intent  = new Intent(this, CompanyInfo.class);

        intent.putExtra("user_id", user_id)  ;
        infoObject.setIsNewCompany(false);
        intent.putExtra("InfoToPassObj", infoObject) ;
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                if(!isTaskRoot()){
                    onBackPressed();
                }else{
                    Intent intent  = new Intent(this, MapsActivity.class);
                    intent.putExtra("InfoToPassObj", infoObject);
                    startActivity(intent);
                    finish();
                }

            default:
                //Default action
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}