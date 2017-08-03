package com.team.noty.gzavnili.api;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by o_cure on 1/23/16.
 */
public class GetTerSetter {

    @SerializedName("SHORTNAME")
    String mShortName;

    public String getShortName() {
        return mShortName;
    }

    @SerializedName("LONGNAME")
    String mLongName;

    public String getLongName() {
        return mLongName;
    }

    @SerializedName("TRACKINGNUM")
    String mTrackingNumber;

    public String getTrackingNumber() {
        return mTrackingNumber;
    }

    @SerializedName("LOCATION")
    String mLocation;

    public String getLocation() {
        return mLocation;
    }

    @SerializedName("VALUE")
    String mValue;

    public String getmValue() {
        return mValue;
    }

    @SerializedName("CONTENTS")
    String mContents;

    public String getmContents() {
        return mContents;
    }

    @SerializedName("STORE")
    String mStore;

    public String getmStore() {
        return mStore;
    }

    @SerializedName("PAID")
    String mPaid;

    public String getmPaid() {
        return mPaid;
    }

    @SerializedName("DEBT")
    String mDept;

    public String getmDept() {
        return mDept;
    }
}