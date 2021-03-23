package com.roaringcat.kidsquiz;

public class Point_item {
    private int image;
    private String name;
    private String price;

    public int getProfile() {
        return image;
    }

    public String getInfo() {
        return name;
    }

    public String getPhone() {
        return price;
    }

    public Point_item(int profile, String info, String phone) {
        this.image = profile;
        this.name = info;
        this.price = phone;
    }
}
