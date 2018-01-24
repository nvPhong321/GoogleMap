package com.example.phong.googlemap.model;

/**
 * Created by Shiro on 06/12/2016.
 */

public class Review {
    private double rating;
    private String content;
    private String name;
    private String timeAgo;
    private String photo;

    public Review(){
        this.photo = "unKnow";
        this.rating = -1;
        this.content = "unKnow";
        this.name = "unKnow";
        this.timeAgo = "unKnow";
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTimeAgo() {
        return timeAgo;
    }

    public void setTimeAgo(String timeAgo) {
        this.timeAgo = timeAgo;
    }
}
