package com.example.soundhawaiiapp;

import java.util.Calendar;
import java.util.Locale;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TimePicker;

import com.app.adapter.AlarmAdapter;
import com.app.appdata.AlarmReceiver;
import com.app.appdata.soundManager;
import com.example.soundhawaiiapp.R;
import com.sound.database.Sqlite;

public class SounAlarmAcitivity extends ActionBarActivity implements  OnItemClickListener{
	Sqlite dbconn=new Sqlite(this);
	ListView mListView;
	Toolbar toolbar;
	soundManager sound=new soundManager(this);
	AlarmAdapter maAdapter;
	TimePickerDialog timePickerDialog;
	private String SOUND_PATH;
	private String SOUND_NAME;
	String[] time;
	Integer[] positin;
	int pos;
	SharedPreferences sp;
	String language;
	Locale myLocale;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.soundalarmscreen);
		toolbar=(Toolbar)findViewById(R.id.toolbar);
		mListView=(ListView)findViewById(R.id.listView1);
		mListView.setOnItemClickListener(this);
		
		sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		language = sp.getString("language", "en");
		setlocale(language);
		
		if(toolbar!=null){
			try {
				setSupportActionBar(toolbar);
				getSupportActionBar().setTitle(getResources().getString(R.string.set_alarm));
				getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	
		
		time=new String[sound.getSound().size()];
		positin=new Integer[sound.getSound().size()];
		dbconn.dbOpen();
		Cursor cur=dbconn.GetTime();
		if(cur.getCount()!=0){
			if(cur.moveToFirst()){
				do{
					time[cur.getPosition()]=cur.getString(cur.getColumnIndex("time"));
					positin[cur.getPosition()]=cur.getInt(cur.getColumnIndex("position"));
				}while(cur.moveToNext());
			}
		}
		else{
			for(int i=0;i<time.length;i++){
				time[i]="";
			}
			for(int i=0;i<positin.length;i++){
				positin[i]=i;
			}
		}
		cur.close();
		dbconn.Close();
		maAdapter=new AlarmAdapter(SounAlarmAcitivity.this,sound.getSound(),time,positin);
		mListView.setAdapter(maAdapter);
	}	
	
	public void setlocale(String lang) {
		myLocale = new Locale(lang);
		Resources res = getResources();
		DisplayMetrics dm = res.getDisplayMetrics();
		Configuration conf = res.getConfiguration();
		conf.locale = myLocale;
		res.updateConfiguration(conf, dm);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId()==android.R.id.home){
			onBackPressed();
		}
		return super.onOptionsItemSelected(item);
	}
 
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		SOUND_PATH=sound.getSound().get(position).get("path");
		SOUND_NAME=sound.getSound().get(position).get("title");
		System.out.println("SOUND NAME==="+SOUND_NAME);
		pos=position;
		OpenTimePickerDialog();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		System.out.println("inside the on resume");
	}
	
	public void  OpenTimePickerDialog(){
		Calendar calendar=Calendar.getInstance();
		timePickerDialog=new TimePickerDialog(SounAlarmAcitivity.this, onTimeSetListener,
				calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
		timePickerDialog.setTitle("Set Alarm Time");
		timePickerDialog.show();
	}
	OnTimeSetListener onTimeSetListener=new OnTimeSetListener() {
		
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			Calendar calNow=Calendar.getInstance();
			Calendar calSet=(Calendar)calNow.clone();
			calSet.set(Calendar.HOUR_OF_DAY,hourOfDay);
			calSet.set(Calendar.MINUTE,minute);
			calSet.set(Calendar.SECOND,0);
			calSet.set(Calendar.MILLISECOND,0);
			if(calSet.compareTo(calNow)<=0){
				calSet.add(Calendar.DATE, 1);
			}
			SetAlarm(calSet);
		}
	};

	  public void SetAlarm(Calendar targetCal){
		  System.out.println("pos=="+pos);
		  System.out.println("time=="+targetCal.get(Calendar.HOUR_OF_DAY));
		  System.out.println("time=="+targetCal.get(Calendar.MINUTE));
		  String t=String.valueOf(targetCal.get(Calendar.HOUR_OF_DAY))+":"+targetCal.get(Calendar.MINUTE);
		 
		 
		 int val=dbconn.check_time(pos);
		 if(val==0){
			 dbconn.dbOpen();
			 //fill(t,pos);
			 dbconn.insert_time(t, pos);
			 dbconn.Close();
		 }
		 else{
			 dbconn.dbOpen();
			 dbconn.update_time(pos, t);
			 dbconn.Close();
		 }
		 //dbconn.delete_time(pos);
		 
		 
		  Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);
		  intent.putExtra("sound", SOUND_PATH);
		  intent.putExtra("pos", pos);
		  intent.putExtra("name", SOUND_NAME);
		  PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), pos, intent, 0);
		  AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		  alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);
		  dbconn.dbOpen();
		  Cursor cur=dbconn.GetTime();
			if(cur.getCount()!=0){
				if(cur.moveToFirst()){
					do{
						time[cur.getPosition()]=cur.getString(cur.getColumnIndex("time"));
						positin[cur.getPosition()]=cur.getInt(cur.getColumnIndex("position"));
					}while(cur.moveToNext());
				}
			}
			else{
				for(int i=0;i<time.length;i++){
					time[i]="";
				}
				for(int i=0;i<positin.length;i++){
					positin[i]=i;
				}
			}
			cur.close();
			dbconn.Close();
			
			for(int i=0;i<time.length;i++){
				System.out.println("time=="+time[i]);
			}
			for(int i=0;i<positin.length;i++){
				System.out.println("pos=="+positin[i]);
			}
			
			maAdapter=new AlarmAdapter(SounAlarmAcitivity.this,sound.getSound(),time,positin);
			mListView.setAdapter(maAdapter);
		 }
	  
	  public void fill(String time,int pos){
		  if(pos==0){
			  dbconn.insert_time(time, pos);
		  }
		  if(pos==1){
			  dbconn.insert_time("", pos);
			  dbconn.insert_time(time, pos);
		  }
		  if(pos==2){
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
		  	  dbconn.insert_time(time, pos);
		  }
		  if(pos==3){
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
		  	  dbconn.insert_time(time, pos);
		  }
		  if(pos==4){
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
		  	  dbconn.insert_time(time, pos);
		  }
		  if(pos==5){
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
		  	  dbconn.insert_time(time, pos);
		  }
		  if(pos==6){
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
		  	  dbconn.insert_time(time, pos);
		  }
		  if(pos==7){
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
		  	  dbconn.insert_time(time, pos);
		  }
		  if(pos==8){
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
		  	  dbconn.insert_time(time, pos);
		  }
		  if(pos==9){
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
		  	  dbconn.insert_time(time, pos);
		  }
		  if(pos==10){
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
		  	  dbconn.insert_time(time, pos);
		  }
		  if(pos==11){
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
		  	  dbconn.insert_time(time, pos);
		  }
		  if(pos==12){
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
		  	  dbconn.insert_time(time, pos);
		  }
		  if(pos==13){
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
		  	  dbconn.insert_time(time, pos);
	  
		  }
		  if(pos==14){
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
		  	  dbconn.insert_time(time, pos);
		  }
		  if(pos==15){
			 
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
		  	  dbconn.insert_time(time, pos);
		  }
		  if(pos==16){
			  
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
		  	  dbconn.insert_time(time, pos);
		  }
		  if(pos==17){
			 
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
		  	  dbconn.insert_time(time, pos);
		  }
		  if(pos==18){
			  
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
		  	  dbconn.insert_time(time, pos);
		  }
		  if(pos==19){
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
			  dbconn.insert_time("", pos);
		  	  dbconn.insert_time(time, pos);
		  }
	  }
}