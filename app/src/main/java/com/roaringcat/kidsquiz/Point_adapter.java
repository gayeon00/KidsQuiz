package com.roaringcat.kidsquiz;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

public class Point_adapter extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<Point_item> data; //Item 목록을 담을 배열
    private int layout;

    public Point_adapter(Context context, int layout, ArrayList<Point_item> data) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.data = data;
        this.layout = layout;
    }

    @Override
    public int getCount() { //리스트 안 Item의 개수를 센다.
        return data.size();
    }

    @Override
    public String getItem(int position) {
        return data.get(position).getInfo();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(layout, parent, false);
        }
        Point_item friendsItem = data.get(position);

        //이미지 파일 연동
        ImageView profile = (ImageView) convertView.findViewById(R.id.itemimage);
        profile.setImageResource(friendsItem.getProfile());

        //이름 등 정보 연동
        TextView info = (TextView) convertView.findViewById(R.id.itemname);
        info.setText(friendsItem.getInfo());

        //전화번호 연동
        TextView phone = (TextView) convertView.findViewById(R.id.itemprice);
        phone.setText(friendsItem.getPhone());

        return convertView;
    }
}