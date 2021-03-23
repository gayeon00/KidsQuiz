package com.roaringcat.kidsquiz;


public class Purchased_item {
    public int image;
    public String date;
    public String name;
    public String price;
    public String state;


    public int getProfile() {
        return image;
    }

    public String getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getState() {
        return state;
    }

    public Purchased_item(int image ,String date, String name, String price, String state) {
        this.image = image;
        this.date = date;
        this.name = name;
        this.price = price;
        this.state = state;
    }


}

