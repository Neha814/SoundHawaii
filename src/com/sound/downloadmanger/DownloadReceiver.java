package com.sound.downloadmanger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.ParcelFileDescriptor;
import android.preference.PreferenceManager;

public class DownloadReceiver extends BroadcastReceiver{
	private PreferenceManager preferenceManager;
	public DownloadReceiver(PreferenceManager preferenceManager){
		this.preferenceManager=preferenceManager;
	}
	@Override
	public void onReceive(Context context, Intent intent) {
		
		
	}
}
