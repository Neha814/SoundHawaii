package com.app.appdata;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;



public class InternetConnectionReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		isNetworkAvailable(context);
	}
	private boolean isNetworkAvailable(Context context){
		ConnectivityManager connectivityManager=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if(connectivityManager!=null){
			NetworkInfo[] info=connectivityManager.getAllNetworkInfo();
			if(info!=null){
				for(int i=0;i<info.length;i++){
					if(info[i].getState()== NetworkInfo.State.CONNECTED){
						if(!Appdata.Network){
							System.out.println("Connected to the internent");
							Appdata.Network=true;
						}
						return true;
					}
				}
			}
		}
		Appdata.Network=false;
		System.out.println("Not connected to the network");
		return false;
	}
}