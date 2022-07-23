package com.navn.safeshop;

public class ValidateUserObject {
    private String user_found;
    private String pass_matches;
    private String error;
    private Integer user_id;
    private String user_name;
    private String email;
    private Integer google_login;
    public String getUser_name() {
        return user_name;
    }
    public Integer getUser_id() {
        return user_id;
    }
    public void setUser_found(String user_found) {
        this.user_found = user_found;
    }

    public void setPass_matches(String pass_matches) {
        this.pass_matches = pass_matches;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getUserFound() {
        return user_found;
    }

    public String getPasswordMatches() {
        return pass_matches;
    }

    public String getErrorMessage() {
        return error;
    }

    public Integer getGoogle_login() {
        return google_login;
    }

    public String getEmail() {
        return email;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setGoogle_login(Integer google_login) {
        this.google_login = google_login;
    }
}
