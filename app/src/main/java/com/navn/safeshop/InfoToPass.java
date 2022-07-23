package com.navn.safeshop;

import java.io.Serializable;
import java.util.ArrayList;

public class InfoToPass implements Serializable {
    private Integer user_id;
    private String user_name;
    private Integer company_id;
    private String company_name;
    private String company_address;
    private String email;
    private Integer google_login;
    private ArrayList<String> html_attrs;
    private String photo_reference;
    private boolean isNewCompany;
    private boolean loggedIn;
    public Integer getGoogle_login() {
        return google_login;
    }

    public void setGoogle_login(Integer google_login) {
        this.google_login = google_login;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public Integer getCompany_id() {
        return company_id;
    }

    public void setCompany_id(Integer company_id) {
        this.company_id = company_id;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getCompany_address() {
        return company_address;
    }

    public void setCompany_address(String company_address) {
        this.company_address = company_address;
    }
    public ArrayList<String> getHtml_attrs() {
        return html_attrs;
    }

    public void setHtml_attrs(ArrayList<String> html_attrs) {
        this.html_attrs = html_attrs;
    }

    public String getPhoto_reference() {
        return photo_reference;
    }

    public void setPhoto_reference(String photo_reference) {
        this.photo_reference = photo_reference;
    }

    public boolean getIsNewCompany() {
        return isNewCompany;
    }

    public void setIsNewCompany(boolean isNewCompany) {
        this.isNewCompany = isNewCompany;
    }


    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }


}
