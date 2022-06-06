package com.example.pasabuyexpressapp.activities;

public class AddItems {

    String itemsId;
    String addItem;
    String addQuantity;
    String email;
    String date;


    public AddItems(String itemsId, String addItem, String addQuantity, String email,String date_) {

        this.itemsId = itemsId;
        this.addItem = addItem;
        this.addQuantity = addQuantity;
        this.email = email;
        this.date = date_;
    }

    public String getItemsId() {
        return itemsId;
    }

    public String getAddItem() {
        return addItem;
    }

    public String getAddQuantity() {
        return addQuantity;
    }

    public String getEmail() {
        return email;
    }

    public String getDate() {
        return date;
    }
}



