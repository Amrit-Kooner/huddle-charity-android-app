package com.example.huddlecharityapp;

public class CharityClass {

    private String title, bio, description, link, image;

    public CharityClass() {
    }

    public CharityClass(String title, String bio, String description, String link, String image) {
        this.image = image;
        this.title = title;
        this.description = description;
        this.bio = bio;
        this.link = link;
    }

    public String getTitle() {
        return title;
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

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}
