package com.team.noty.gzavnili.adapters;

import android.util.Log;

public class ParcelData {

	String mLocation, mTrackingNumber, parcelId;
	public boolean mCorrect, mUnpaid, box;

	public ParcelData(String mLocation, String mTrackingNumber, boolean mCorrect, boolean mUnpaid, String parcelId, boolean box) {
		this.mLocation = mLocation;
		this.mTrackingNumber = mTrackingNumber;
		this.mCorrect = mCorrect;
		this.mUnpaid = mUnpaid;
		this.parcelId = parcelId;
		this.box = box;
	}

	public String getmLocation() {
		return mLocation;
	}

	public String getmTrackingNumber() {
		return mTrackingNumber;
	}

	public boolean ismCorrect() {
		return mCorrect;
	}

	public boolean ismUnpaid() {
		return mUnpaid;
	}

	public String getParcelId() {
		return parcelId;
	}
}
