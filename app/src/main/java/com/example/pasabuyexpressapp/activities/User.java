package com.example.pasabuyexpressapp.activities;


public class User {
    private String FirstName;
    private String FavFood;
    private String id;

    public User(String fName, String fFood, String id_){
        FirstName = fName;
        FavFood = fFood;
        setId(id_);
    }

    public String getFirstName() {
        return FirstName;
    }



    public void setFirstName(String firstName) {
        FirstName = firstName;

    }

    public String getFavFood() {
        return FavFood;
    }

    public void setFavFood(String favFood) {
        FavFood = favFood;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}