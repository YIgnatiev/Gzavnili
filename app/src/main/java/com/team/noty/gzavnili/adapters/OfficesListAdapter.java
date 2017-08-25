package com.team.noty.gzavnili.adapters;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.team.noty.gzavnili.R;

import java.util.ArrayList;

public class OfficesListAdapter extends BaseAdapter {

    ArrayList<OfficeData> mOfficesData = new ArrayList<>();
    private Context mContext;
    LatLng latLng;

    public OfficesListAdapter(Context mContext, ArrayList<OfficeData> mOfficesData) {
        this.mContext = mContext;
        this.mOfficesData = mOfficesData;
    }


    @Override
    public int getCount() {
        return mOfficesData.size();
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
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.item_for_list_offices, null);
        }

        TextView mTxtName = (TextView) convertView.findViewById(R.id.txt_name_office);
        TextView mTxtAddress = (TextView) convertView.findViewById(R.id.txt_address);
        TextView mTxtTel = (TextView) convertView.findViewById(R.id.txt_phone);
        TextView mTxtFax = (TextView) convertView.findViewById(R.id.txt_fax);
        TextView mEmail = (TextView) convertView.findViewById(R.id.txt_email);
        TextView mWorkingHours = (TextView) convertView.findViewById(R.id.txt_working_hours);

        ImageView btn_open_map = (ImageView) convertView.findViewById(R.id.btn_open_map);

        mTxtName.setText(mOfficesData.get(position).getName());
        mTxtAddress.setText(mOfficesData.get(position).getAddress());
        mTxtTel.setText(mOfficesData.get(position).getTel());
        mTxtFax.setText(mOfficesData.get(position).getFax());
        mEmail.setText(mOfficesData.get(position).getEmail());
        mWorkingHours.setText(mOfficesData.get(position).getWorkingHours());

        latLng = new LatLng(Double.parseDouble(mOfficesData.get(position).getLat()),
                Double.parseDouble(mOfficesData.get(position).getLon()));
        btn_open_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMaps(latLng, mOfficesData.get(position).getName());
            }
        });

        return convertView;
    }

    public void openMaps(LatLng latLng, String label){
        String packageName = "com.google.android.apps.maps";
        PackageManager pm = mContext.getPackageManager();
        if (isPackageInstalled(packageName, pm)) {
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(
                    "geo:" + latLng.latitude + ", " + latLng.longitude + "?q=" + latLng.latitude + ", "
                            + latLng.longitude + "(" + label + ")"));
            intent.setPackage("com.google.android.apps.maps");
            mContext.startActivity(intent);
        }
        else {
            String uri = "http://maps.google.com/maps?q=loc:" + latLng.latitude +", " +
                    latLng.longitude;
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
            mContext.startActivity(intent);
        }
    }

    private boolean isPackageInstalled(String packagename, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packagename, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

}
