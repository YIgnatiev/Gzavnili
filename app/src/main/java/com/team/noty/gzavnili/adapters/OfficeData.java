package com.team.noty.gzavnili.adapters;

public class OfficeData {

	private String mName, mAddress, mTel, mFax, mEmail, mWorkingHours, mLat, mLon;

	public OfficeData(String mName, String mAddress, String mTel, String mFax, String mEmail,
					  String mWorkingHours, String mLat, String mLon) {
		this.mName = mName;
		this.mAddress = mAddress;
		this.mTel = mTel;
		this.mFax = mFax;
		this.mEmail = mEmail;
		this.mWorkingHours = mWorkingHours;
		this.mLat = mLat;
		this.mLon = mLon;
	}

	public String getAddress() {
		return mAddress;
	}

	public String getEmail() {
		return mEmail;
	}

	public String getFax() {
		return mFax;
	}

	public String getLat() {
		return mLat;
	}

	public String getLon() {
		return mLon;
	}

	public String getName() {
		return mName;
	}

	public String getTel() {
		return mTel;
	}

	public String getWorkingHours() {
		return mWorkingHours;
	}
}
