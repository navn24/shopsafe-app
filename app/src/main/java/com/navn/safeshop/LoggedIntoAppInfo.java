package com.navn.safeshop;

import java.io.Serializable;

public class LoggedIntoAppInfo implements Serializable {
   private float review_1 ;
   private float review_2 ;
   private float review_3 ;
   private String comment;
    private String searchQuery;
    private boolean fromLeaveReviewActivity;
    private boolean fromMapsActivity;
    public LoggedIntoAppInfo(String searchQuery) {
        this.searchQuery = searchQuery;
        fromMapsActivity = true;
    }

    public LoggedIntoAppInfo(float review_1, float review_2, float review_3, String comment) {
        this.review_1 = review_1;
        this.review_2 = review_2;
        this.review_3 = review_3;
        this.comment = comment;
        fromLeaveReviewActivity=true;
    }

    public float getReview_1() {
        return review_1;
    }

    public void setReview_1(float review_1) {
        this.review_1 = review_1;
    }

    public float getReview_2() {
        return review_2;
    }

    public void setReview_2(float review_2) {
        this.review_2 = review_2;
    }

    public float getReview_3() {
        return review_3;
    }

    public void setReview_3(float review_3) {
        this.review_3 = review_3;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public boolean isFromLeaveReviewActivity() {
        return fromLeaveReviewActivity;
    }

    public boolean isFromMapsActivity() {
        return fromMapsActivity;
    }
}
