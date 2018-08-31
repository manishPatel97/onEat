package com.example.dell.oneat.Model;

public class Rating {
    private String userphone;
    private String foodid;
    private String foodRating;
    private String comment;

    public String getUserphone() {
        return userphone;
    }

    public void setUserphone(String userphone) {
        this.userphone = userphone;
    }

    public String getFoodid() {
        return foodid;
    }

    public void setFoodid(String foodid) {
        this.foodid = foodid;
    }

    public String getFoodRating() {
        return foodRating;
    }

    public void setFoodRating(String foodRating) {
        this.foodRating = foodRating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Rating(String userphone, String foodid, String foodRating, String comment) {

        this.userphone = userphone;
        this.foodid = foodid;
        this.foodRating = foodRating;
        this.comment = comment;
    }
    public Rating(){}
}
