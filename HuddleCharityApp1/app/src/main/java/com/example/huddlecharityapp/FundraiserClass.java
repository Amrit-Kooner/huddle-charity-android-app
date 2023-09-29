package com.example.huddlecharityapp;
import java.util.Date;

public class FundraiserClass {

    private String title, description, image, target, bio, startDate, endDate, userId;
    private int current;

    public FundraiserClass() {
    }

    public FundraiserClass(String image, String title, String description, String bio, String target, int current, String startDate, String endDate, String userId) {
        this.image = image;
        this.title = title;
        this.description = description;
        this.bio = bio;
        this.target = target;
        this.startDate = startDate;
        this.endDate = endDate;
        this.current = current;
        this.userId = userId;
    }

    //-------

    public String getTitle() { return title;}

    public void setTitle(String title) { this.title = title; }

    //-------

    public String getDescription() { return description;}
    public void setDescription(String description) { this.description = description;}

    //-------

    public String getImage() { return image;}
    public void setImage(String image) { this.image = image;}

    //-------

    public String getTarget() { return target;}
    public void setTarget(String target) { this.target = target;}

    //-------

    public String getBio() { return bio;}
    public void setBio(String bio) { this.bio = bio;}

    //-------

    public String getStartDate() { return startDate;}
    public void setStartDate(String startDate) { this.startDate = startDate;}

    //-------

    public String getEndDate() { return endDate;}
    public void setEndDate(String endDate) { this.endDate = endDate;}

    //-------

    public int getCurrent() { return current;}
    public void setCurrent(int current) { this.current = current;}

    //-------

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId;}
}

