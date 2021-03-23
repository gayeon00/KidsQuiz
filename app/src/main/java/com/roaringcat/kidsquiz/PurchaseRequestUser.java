package com.roaringcat.kidsquiz;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class PurchaseRequestUser {
    public String email;
    public String phonenumber;
    public String itemname;
    public String itemprice;
    public String time;

    public PurchaseRequestUser() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public PurchaseRequestUser(String email, String phonenumber,String itemname,String itemprice,String time) {
        this.email = email;
        this.phonenumber = phonenumber;
        this.itemname = itemname;
        this.itemprice = itemprice;
        this.time = time;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("email", email);
        result.put("phonenumber", phonenumber);
        result.put("itemname", itemname);
        result.put("itemprice", itemprice);
        result.put("time", time);

        return result;
    }
}
