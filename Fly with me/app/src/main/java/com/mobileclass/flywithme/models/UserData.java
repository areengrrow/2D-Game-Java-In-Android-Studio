package com.mobileclass.flywithme.models;


public class UserData {
    private String name;
    private String imageUrl;
    private Integer singleMatch;
    private Integer singleScore;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getSingleMatch() {
        return singleMatch;
    }

    public void setSingleMatch(Integer singleMatch) {
        this.singleMatch = singleMatch;
    }

    public Integer getSingleScore() {
        return singleScore;
    }

    public void setSingleScore(Integer singleScore) {
        this.singleScore = singleScore;
    }
}
