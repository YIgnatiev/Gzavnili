package com.team.noty.gzavnili.adapters;

public class NotificationData {

	String mMessage, mDate;

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
}
