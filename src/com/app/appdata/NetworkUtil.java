package com.app.appdata;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtil {
	
	public static int TYPE_WIFI=1;
	public static int TYPE_MOBILE=2;
	public static int TYPE_NOT_CONNECTED=0;
	public static int NETWORK_STATUS_NOT_CONNECTED=0;
	public static int NETWORK_STATUS_WIFI=1;
	public static int NETWORK_STATUS_MOBILE=2;
	
	public static int getConnectivityStatus(Context context){
		ConnectivityManager connectivity=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork=connectivity.getActiveNetworkInfo();
		if(activeNetwork!=null){
			if(activeNetwork.getType()==ConnectivityManager.TYPE_WIFI)
				return TYPE_WIFI;
			if(activeNetwork.getType()==ConnectivityManager.TYPE_MOBILE)
				return TYPE_MOBILE;
		}
		return TYPE_NOT_CONNECTED;
	}
	
	public static int getConnectivityStatusString(Context context){
		int conn=getConnectivityStatus(context);
		int status=0;
		if(conn==NetworkUtil.TYPE_WIFI)
			status=NETWORK_STATUS_WIFI;
		else if(conn==NetworkUtil.TYPE_MOBILE)
			status=NETWORK_STATUS_MOBILE;
		else if(conn==NetworkUtil.TYPE_NOT_CONNECTED)
			status=NETWORK_STATUS_NOT_CONNECTED;
		return status;
	}
}
