package com.mobileclass.flywithme.models;


public class UserData {
    private String name;
    private String imageUrl;
    private Integer singleMatch;
    private Integer singleScore;
    private Integer win;
    private Integer lost;
    private Integer total;


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

    public Integer getWin() {
        return win;
    }

    public void setWin(Integer win) {
        this.win = win;
    }

    public Integer getLost() {
        return lost;
    }

    public void setLost(Integer lost) {
        this.lost = lost;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}
