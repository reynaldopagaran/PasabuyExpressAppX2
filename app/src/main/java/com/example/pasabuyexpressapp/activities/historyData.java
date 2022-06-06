package com.example.pasabuyexpressapp.activities;

public class historyData {

    private String item;
    private String qty;
    private String date;

    public historyData(String item, String qty, String date){
        this.item = item;
        this.qty = qty;
        this.date = date;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
