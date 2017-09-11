package com.team.noty.gzavnili.adapters;

public class NotificationData {

	String mMessage, mDate, mPhone, mEmail;

	public NotificationData(String mMessage, String mDate, String mPhone, String mEmail){
		this.mMessage = mMessage;
		this.mDate = mDate;
		this.mPhone = mPhone;
		this.mEmail = mEmail;
	}

	public NotificationData(String mMessage, String mDate){
		this.mMessage = mMessage;
		this.mDate = mDate;
	}

	public String getMessage() {
		return mMessage;
	}

	public String getDate() {
		return mDate;
	}

	public String getPhone() {
		return mPhone;
	}

	public String getEmail() {
		return mEmail;
	}
}
