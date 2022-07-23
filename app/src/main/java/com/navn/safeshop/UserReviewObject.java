package com.navn.safeshop;

public class UserReviewObject {
    private String user_name;
    private String comment_text;
    private float user_average_review;
    private String date;

    public UserReviewObject(String user_name, String comment_text, float user_average_review, String date){
        this.user_name = user_name;
        this.comment_text = comment_text;
        this.user_average_review = user_average_review;
        this.date=date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getComment_text() {
        return comment_text;
    }

    public void setComment_text(String comment_text) {
        this.comment_text = comment_text;
    }

    public float getUser_average_review() {
        return user_average_review;
    }

    public void setUser_average_review(float user_average_review) {
        this.user_average_review = user_average_review;
    }

}
