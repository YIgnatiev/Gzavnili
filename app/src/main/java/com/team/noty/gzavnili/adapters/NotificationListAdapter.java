package com.team.noty.gzavnili.adapters;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.layout_item_for_notification_list, null);
        }

        TextView mTxtMessage = (TextView) convertView.findViewById(R.id.txt_message);
        TextView mTxtDate = (TextView) convertView.findViewById(R.id.txt_date);

        ImageView btn_call = (ImageView) convertView.findViewById(R.id.btn_call);
        ImageView btn_mail = (ImageView) convertView.findViewById(R.id.btn_mail);


        mTxtMessage.setText(Html.fromHtml(notificationDatas.get(position).getMessage()));
        mTxtMessage.setMovementMethod(LinkMovementMethod.getInstance());

        mTxtDate.setText(splitTime(notificationDatas.get(position).getDate()));

        btn_mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notificationDatas.get(position).getEmail() != null){
                    sendEmail(notificationDatas.get(position).getEmail());
                }
            }
        });

        btn_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notificationDatas.get(position).getPhone() != null) {
                    callPhone(notificationDatas.get(position).getPhone());
                }
            }
        });


        return convertView;
    }

    public void clearAllMessage(){
        notificationDatas.clear();
        notifyDataSetChanged();
    }
    public void callPhone(String text)
    {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + text));
        try {
            callIntent.setPackage("com.android.phone");
            mContext.startActivity(callIntent);
        } catch (Exception e) {
            callIntent.setPackage("com.android.server.telecom");
            mContext.startActivity(callIntent);
        }
    }
    public void sendEmail(String email) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        i.putExtra(Intent.EXTRA_SUBJECT, "Help");

        try {
            mContext.startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(mContext, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
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
