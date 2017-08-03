package com.team.noty.gzavnili.adapters;

public class ParcelData {

	String mLocation, mTrackingNumber;
	boolean mCorrect, mUnpaid;

	public ParcelData(String mLocation, String mTrackingNumber, boolean mCorrect, boolean mUnpaid) {
		this.mLocation = mLocation;
		this.mTrackingNumber = mTrackingNumber;
		this.mCorrect = mCorrect;
		this.mUnpaid = mUnpaid;
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
}
