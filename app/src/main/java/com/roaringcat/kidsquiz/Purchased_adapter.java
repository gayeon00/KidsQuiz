package com.roaringcat.kidsquiz;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
public class Purchased_adapter extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<Purchased_item> data; //Item 목록을 담을 배열
    private int layout;
    public Purchased_adapter(Context context, int layout, ArrayList<Purchased_item> data) {
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
        return data.get(position).getName();
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(layout, parent, false);
            ViewGroup.LayoutParams layoutParams = convertView.getLayoutParams();
            convertView.setLayoutParams(layoutParams);
        }
        Purchased_item friendsItem = data.get(position);
        //이미지 파일 연동
        ImageView itemimage = (ImageView) convertView.findViewById(R.id.itemimage);
        itemimage.setImageResource(friendsItem.getProfile());
        //이름
        TextView itemname = (TextView) convertView.findViewById(R.id.itemname);
        itemname.setText(friendsItem.getName());
        //가격
        TextView itemprice = (TextView) convertView.findViewById(R.id.itemprice);
        itemprice.setText(friendsItem.getPrice());
        //구매일자
        TextView purchaseddate = (TextView) convertView.findViewById(R.id.purchaseddate);
        purchaseddate.setText(friendsItem.getDate());
        //처리상태
        TextView state = (TextView) convertView.findViewById(R.id.state);
        state.setText(friendsItem.getState());
        return convertView;
    }
}