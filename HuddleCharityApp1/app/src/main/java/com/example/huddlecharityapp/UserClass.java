package com.example.huddlecharityapp;

public class UserClass {

    private String username, first_name, last_name, profile_picture, fundraiserID;
    private int total_donations;

    String admin;

    public UserClass() {
    }

    public UserClass(String username, String first_name, String last_name, String profile_picture, int total_donations, String admin, String fundraiserID) {
        this.username = username;
        this.first_name = first_name;
        this.last_name = last_name;
        this.profile_picture = profile_picture;
        this.total_donations = total_donations;
        this.admin = admin;
        this.fundraiserID = fundraiserID;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

//--
    public String getFirst_name() {return first_name;}
    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    //--

    public String getLast_name() {
        return last_name;
    }
    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    //--

    public String getProfile_picture() {
        return profile_picture;
    }
    public void setProfile_picture(String profile_picture) { this.profile_picture = profile_picture;}

    //--

    public Number getTotal_donations() {
        return total_donations;
    }
    public void setTotal_donations(int total_donations) { this.total_donations = total_donations;}

    //--

    public String getAdmin() {
        return admin;
    }
    public void setAdmin(String admin) { this.admin = admin;}

    //---

    public String getFundraiserID() {
        return fundraiserID;
    }

    public void setFundraiserID(String fundraiserID) {
        this.fundraiserID = fundraiserID;
    }
}
