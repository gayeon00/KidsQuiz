package com.roaringcat.kidsquiz;

import android.widget.EditText;

public class User {

    public String name;
    public String frndreccode;
    public String myreccode;
    public int myreserve;
    public int count;

    public int korean_count_2;
    public int math_count_2;
    public int perception_count_2;
    public int korean_total_count_2;
    public int math_total_count_2;
    public int perception_total_count_2;
    public int overall_total_count_2;
    public int overall_count_2;

    public int korean_count_3;
    public int math_count_3;
    public int perception_count_3;
    public int korean_total_count_3;
    public int math_total_count_3;
    public int perception_total_count_3;
    public int overall_total_count_3;
    public int overall_count_3;

    public int korean_count_4;
    public int math_count_4;
    public int perception_count_4;
    public int korean_total_count_4;
    public int math_total_count_4;
    public int perception_total_count_4;
    public int overall_total_count_4;
    public int overall_count_4;

    public int korean_count_5;
    public int math_count_5;
    public int perception_count_5;
    public int korean_total_count_5;
    public int math_total_count_5;
    public int perception_total_count_5;
    public int overall_total_count_5;
    public int overall_count_5;

    public int korean_count_6;
    public int math_count_6;
    public int perception_count_6;
    public int korean_total_count_6;
    public int math_total_count_6;
    public int perception_total_count_6;
    public int overall_total_count_6;
    public int overall_count_6;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String name, String frndreccode, String myreccode, int myreserve, int count,
                int korean_count_2, int math_count_2, int perception_count_2, int korean_total_count_2, int math_total_count_2, int perception_total_count_2, int overall_total_count_2, int overall_count_2,
                int korean_count_3, int math_count_3, int perception_count_3, int korean_total_count_3, int math_total_count_3, int perception_total_count_3, int overall_total_count_3, int overall_count_3,
                int korean_count_4, int math_count_4, int perception_count_4, int korean_total_count_4, int math_total_count_4, int perception_total_count_4, int overall_total_count_4, int overall_count_4,
                int korean_count_5, int math_count_5, int perception_count_5, int korean_total_count_5, int math_total_count_5, int perception_total_count_5, int overall_total_count_5, int overall_count_5,
                int korean_count_6, int math_count_6, int perception_count_6, int korean_total_count_6, int math_total_count_6, int perception_total_count_6, int overall_total_count_6, int overall_count_6
    ) {

        this.name=name;
        this.frndreccode = frndreccode;
        this.myreccode = myreccode;
        this.myreserve = myreserve;
        this.count = count;

        this.korean_count_2=korean_count_2;
        this.math_count_2=math_count_2;
        this.perception_count_2=perception_count_2;
        this.korean_total_count_2=korean_total_count_2;
        this.math_total_count_2=math_total_count_2;
        this.perception_total_count_2=perception_total_count_2;
        this.overall_count_2 = overall_count_2;
        this.overall_total_count_2 = overall_total_count_2;

        this.korean_count_3=korean_count_3;
        this.math_count_3=math_count_3;
        this.perception_count_3=perception_count_3;
        this.korean_total_count_3=korean_total_count_3;
        this.math_total_count_3=math_total_count_3;
        this.perception_total_count_3=perception_total_count_3;
        this.overall_count_3 = overall_count_3;
        this.overall_total_count_3 = overall_total_count_3;

        this.korean_count_4=korean_count_4;
        this.math_count_4=math_count_4;
        this.perception_count_4=perception_count_4;
        this.korean_total_count_4=korean_total_count_4;
        this.math_total_count_4=math_total_count_4;
        this.perception_total_count_4=perception_total_count_4;
        this.overall_count_4 = overall_count_4;
        this.overall_total_count_4 = overall_total_count_4;

        this.korean_count_5=korean_count_5;
        this.math_count_5=math_count_5;
        this.perception_count_5=perception_count_5;
        this.korean_total_count_5=korean_total_count_5;
        this.math_total_count_5=math_total_count_5;
        this.perception_total_count_5=perception_total_count_5;
        this.overall_count_5 = overall_count_5;
        this.overall_total_count_5 = overall_total_count_5;

        this.korean_count_6=korean_count_6;
        this.math_count_6=math_count_6;
        this.perception_count_6=perception_count_6;
        this.korean_total_count_6=korean_total_count_6;
        this.math_total_count_6=math_total_count_6;
        this.perception_total_count_6=perception_total_count_6;
        this.overall_count_6 = overall_count_6;
        this.overall_total_count_6 = overall_total_count_6;
    }
}