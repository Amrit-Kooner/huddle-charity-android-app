package com.example.huddlecharityapp;

import android.graphics.Paint;

public class EventClass {

   private String title, bio, description, street, city, postCode, country, link, image;

    public EventClass() {
    }

    public EventClass(String title, String bio, String description, String street, String city, String postCode, String country, String link, String image) {
        this.title = title;
        this.bio = bio;
        this.description = description;
        this.street = street;
        this.city = city;
        this.link = link;
        this.postCode = postCode;
        this.country = country;
        this.image = image;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
