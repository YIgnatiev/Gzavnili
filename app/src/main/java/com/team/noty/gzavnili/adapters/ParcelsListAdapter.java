package com.team.noty.gzavnili.adapters;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.List;

public class ParcelsListAdapter extends BaseAdapter {

    ArrayList<ParcelData> mParcelData = new ArrayList<>();
    private Context mContext;
    boolean viewSelected;
    CheckBox cb_selected;
    int count = 0;

    public void isSelected(boolean visibility) {
        viewSelected = visibility;
    }

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
        return mParcelData.get(position);
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

        cb_selected = (CheckBox) convertView.findViewById(R.id.cb_select);

        if (viewSelected){
            cb_selected.setVisibility(View.VISIBLE);
        }
        else {
            cb_selected.setVisibility(View.GONE);
        }

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

        ParcelData p = getProduct(position);

        cb_selected.setOnCheckedChangeListener(myCheckChangeList);
        // пишем позицию
        cb_selected.setTag(position);
        // заполняем данными из товаров: в корзине или нет
        cb_selected.setChecked(p.box);

        return convertView;
    }
    ParcelData getProduct(int position) {
        return ((ParcelData) getItem(position));
    }

    // содержимое корзины
    public ArrayList<ParcelData> getBox() {
        ArrayList<ParcelData> box = new ArrayList<ParcelData>();
        for (ParcelData p : mParcelData) {
            // если в корзине
            if (p.box)
                box.add(p);
        }
        return box;
    }

    // обработчик для чекбоксов
    CompoundButton.OnCheckedChangeListener myCheckChangeList = new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            // меняем данные товара (в корзине или нет)

            if (isChecked == true) {
                if(mContext instanceof BottomNavActivity){
                    ((BottomNavActivity)mContext).enabledMenuGroup(true);
                    count ++;
                }
            }
            else {
                count --;
                if (count == 0){
                    if(mContext instanceof BottomNavActivity){
                        ((BottomNavActivity)mContext).enabledMenuGroup(false);
                    }
                }
            }
            getProduct((Integer) buttonView.getTag()).box = isChecked;
        }
    };

}
