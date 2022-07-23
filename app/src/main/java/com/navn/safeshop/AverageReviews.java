package com.navn.safeshop;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AverageReviews {
    Integer company_id;
    
   //Just a class for getting the response data from the server
   private  float average_review_1;
   private float average_review_2;
   private float average_review_3;

    private float company_average_review;

    public Integer getCompany_id() {
        return company_id;
    }

    public void setCompany_id(Integer company_id) {
        this.company_id = company_id;
    }

    public float getAverage_review_1() {
        return average_review_1;
    }

    public void setAverage_review_1(float average_review_1) {
        this.average_review_1 = average_review_1;
    }

    public float getAverage_review_2() {
        return average_review_2;
    }

    public void setAverage_review_2(float average_review_2) {
        this.average_review_2 = average_review_2;
    }

    public float getAverage_review_3() {
        return average_review_3;
    }

    public void setAverage_review_3(float average_review_3) {
        this.average_review_3 = average_review_3;
    }
    public float getCompany_average_review() {
        return company_average_review;
    }

    public void setCompany_average_review(float company_average_review) {
        this.company_average_review = company_average_review;
    }



}
