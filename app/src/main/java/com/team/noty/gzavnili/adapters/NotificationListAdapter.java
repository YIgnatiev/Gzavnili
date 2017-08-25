package com.team.noty.gzavnili.adapters;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.team.noty.gzavnili.BottomNavActivity;
import com.team.noty.gzavnili.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class NotificationListAdapter extends BaseAdapter {

    ArrayList<NotificationData> notificationDatas = new ArrayList<>();
    private Context mContext;

    public NotificationListAdapter(Context mContext, ArrayList<NotificationData> notificationDatas) {
        this.mContext = mContext;
        this.notificationDatas = notificationDatas;
    }


    @Override
    public int getCount() {
        return notificationDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return notificationDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.layout_item_for_notification_list, null);
        }

        TextView mTxtMessage = (TextView) convertView.findViewById(R.id.txt_message);
        TextView mTxtDate = (TextView) convertView.findViewById(R.id.txt_date);


        mTxtMessage.setText(notificationDatas.get(position).getMessage());

        mTxtDate.setText(splitTime(notificationDatas.get(position).getDate()));


        return convertView;
    }

    public String splitTime(String time) {
        String splitTime = "";
        SimpleDateFormat format = new SimpleDateFormat("MMMMM, dd yyyy HH:mm:ss",
                Locale.US);

        SimpleDateFormat formatView = new SimpleDateFormat("HH:mm dd MMM yyyy",
                Locale.US);

        try {
            Date newDate = format.parse(time);
            splitTime = formatView.format(newDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return splitTime;
    }

}
