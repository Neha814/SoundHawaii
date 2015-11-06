package com.app.appdata;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.example.soundhawaiiapp.MainActivity;
import com.example.soundhawaiiapp.R;
import com.sound.database.Sqlite;

public class AlarmReceiver extends BroadcastReceiver{

	NotificationManager manager;
	Notification notification;
	Bundle extra;
	String sound_path;
	String sound_name;
	int pos;
	Sqlite dbconn;
	@Override
	public void onReceive(Context context, Intent intent) {
		dbconn=new Sqlite(context);
		extra=intent.getExtras();
		sound_path=extra.getString("sound");
		pos=extra.getInt("pos");
		sound_name=extra.getString("name");
		System.out.println("sound=="+sound_path);
		System.out.println("pos==in alarm"+pos);
		System.out.println("soud name=="+sound_name);
		Intent in = new Intent(context, MainActivity.class);
	    in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    PendingIntent Sender = PendingIntent.getActivity(context, 0, in, PendingIntent.FLAG_UPDATE_CURRENT);
	    manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
	    notification = new Notification(R.drawable.ic_launcher, "Sound of Hawaii", System.currentTimeMillis());
	    notification.setLatestEventInfo(context, "Sounds of Hawaii", sound_name, Sender);
	    notification.flags = Notification.FLAG_INSISTENT;
	    notification.sound = Uri.parse(sound_path);
	    manager.notify(1, notification); 
	    
	    dbconn.dbOpen();
	    dbconn.delete_time(pos);
	    dbconn.Close();
	}
}