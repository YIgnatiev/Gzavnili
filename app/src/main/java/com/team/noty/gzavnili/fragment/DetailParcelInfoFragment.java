package com.team.noty.gzavnili.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.team.noty.gzavnili.R;

public class DetailParcelInfoFragment extends Fragment{

    View mView;
    TextView mTxtTrackingNumber, mTxtValue, mTxtWeight, mTxtStore, mTxtContent, mTxtServiceType,
        mTxtPrice, mTxtFirstName, mTxtLastName, mTxtCellPhone, mTxtAddress, mTxtCity, mTxtPrivate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView =  inflater.inflate(R.layout.fragment_parcel_detail, container, false);

        mTxtTrackingNumber = (TextView) mView.findViewById(R.id.txt_tracking_number);
        mTxtValue = (TextView) mView.findViewById(R.id.txt_value);
        mTxtWeight = (TextView) mView.findViewById(R.id.txt_weight);
        mTxtStore = (TextView) mView.findViewById(R.id.txt_store);
        mTxtContent = (TextView) mView.findViewById(R.id.txt_content);
        mTxtServiceType = (TextView) mView.findViewById(R.id.txt_service_type);
        mTxtPrice = (TextView) mView.findViewById(R.id.txt_price);
        mTxtFirstName = (TextView) mView.findViewById(R.id.txt_first_name);
        mTxtLastName = (TextView) mView.findViewById(R.id.txt_last_name);
        mTxtCellPhone = (TextView) mView.findViewById(R.id.txt_cell_phone);
        mTxtAddress = (TextView) mView.findViewById(R.id.txt_address);
        mTxtCity = (TextView) mView.findViewById(R.id.txt_city);
        mTxtPrivate = (TextView) mView.findViewById(R.id.txt_private);

        return mView;
    }
}
