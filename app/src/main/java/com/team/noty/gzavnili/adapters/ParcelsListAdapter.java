package com.team.noty.gzavnili.adapters;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.team.noty.gzavnili.R;

import java.util.ArrayList;

public class ParcelsListAdapter extends BaseAdapter {

    ArrayList<ParcelData> mParcelData = new ArrayList<>();
    private Context mContext;

    public ParcelsListAdapter(Context mContext, ArrayList<ParcelData> mParcelData) {
        this.mContext = mContext;
        this.mParcelData = mParcelData;
    }


    @Override
    public int getCount() {
        return mParcelData.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
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
            convertView = mInflater.inflate(R.layout.item_for_list_parcels, null);
        }

        TextView mTxtLocation = (TextView) convertView.findViewById(R.id.txt_location);
        TextView mTxtTrackingNumber = (TextView) convertView.findViewById(R.id.txt_tracking_number);

        LinearLayout mLineCorrect = (LinearLayout) convertView.findViewById(R.id.line_correct);
        LinearLayout mLinePaid = (LinearLayout) convertView.findViewById(R.id.line_paid);

        mTxtLocation.setText(mParcelData.get(position).getmLocation());
        mTxtTrackingNumber.setText(mParcelData.get(position).getmTrackingNumber());

        if (!mParcelData.get(position).ismCorrect()){
            mLineCorrect.setVisibility(View.GONE);
        }
        else {
            mLineCorrect.setVisibility(View.VISIBLE);
        }

        if (!mParcelData.get(position).ismUnpaid()){
            mLinePaid.setVisibility(View.GONE);
        }
        else {
            mLinePaid.setVisibility(View.VISIBLE);
        }

        return convertView;
    }


}
