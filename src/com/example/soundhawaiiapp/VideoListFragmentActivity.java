package com.example.soundhawaiiapp;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.app.adapter.videolistAdapter;
import com.app.appdata.Appdata;
import com.example.soundhawaiiapp.R;
import com.sound.database.Sqlite;

public class VideoListFragmentActivity extends ActionBarActivity implements OnItemClickListener{
	
	Toolbar toolbar;
	ListView mListView;
	videolistAdapter mAdapter;
	public  ArrayList<HashMap<String, String>>arraylist=new ArrayList<HashMap<String, String>>();
	public HashMap<String, String>hashmap=new HashMap<String,String>();
	Sqlite dbconn;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.videolistscreen);
		toolbar=(Toolbar)findViewById(R.id.toolbar);
		mListView=(ListView)findViewById(R.id.listView1);
		mListView.setOnItemClickListener(this);
		dbconn=new Sqlite(VideoListFragmentActivity.this);
		if (toolbar != null) {
            try {
				setSupportActionBar(toolbar);
				getSupportActionBar().setTitle(getResources().getString(R.string.sound_hawaii));
				getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
		GetVideoList();
		
	}
	 
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId()==android.R.id.home){
			onBackPressed();
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
		System.out.println("video url=="+arraylist.get(position).get("file"));
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(arraylist.get(position).get("file")));
		intent.setDataAndType(Uri.parse(arraylist.get(position).get("file").replace(" ", "%20")), "video/*");
		startActivity(intent);
	}
	
	
	public void GetVideoList(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Appdata.showProgressBar(VideoListFragmentActivity.this, "", "Loading...");
				try {
					if(Appdata.isNetworkAvailable(VideoListFragmentActivity.this)){
						arraylist=ParseVideoList(Appdata.GetJsonFromUrl(Appdata.SERVER_URL+Appdata.S_VIDEOLIST));	
					}
					else{
						arraylist=ParseVideoListFromDB();
					}
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							mAdapter=new videolistAdapter(VideoListFragmentActivity.this,arraylist);
							mListView.setAdapter(mAdapter);
						}
					});
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				Appdata.closeProgressBar();
			}
		}).start();
	}
	
	public  ArrayList<HashMap<String, String>>ParseVideoList(JSONObject jsonObject){
		
			try {
				if(jsonObject.getString("Response").equalsIgnoreCase("true")){
					JSONArray jsonArray=jsonObject.getJSONArray("GetData");
					if(jsonArray.length()!=0) {
						dbconn.dbOpen();
						dbconn.delete_videlist();
						dbconn.Close();
					}
					for(int i=0;i<jsonArray.length();i++) {
						JSONObject jsonObject2=jsonArray.getJSONObject(i);
						hashmap=new HashMap<String,String>();
						hashmap.put("id", jsonObject2.getString("id"));
						hashmap.put("videoName", jsonObject2.getString("videoName"));
						hashmap.put("videoImage", jsonObject2.getString("videoImage"));
						hashmap.put("file", jsonObject2.getString("file"));
						hashmap.put("videoDesc", jsonObject2.getString("videoDesc"));
						arraylist.add(hashmap);
						
						dbconn.dbOpen();
						dbconn.insert_video(jsonObject2.getString("id"),jsonObject2.getString("videoName"),
											jsonObject2.getString("videoImage"),jsonObject2.getString("file"),
											jsonObject2.getString("videoDesc"));
						dbconn.Close();
					}	
				}
			} catch (Exception e) {
			}
		return arraylist;
	}
	public  ArrayList<HashMap<String, String>>ParseVideoListFromDB() {
		dbconn.dbOpen();
		Cursor cur=dbconn.GetVideolist();
		if(cur.getCount()!=0){
			if(cur.moveToFirst()){
				do{
					hashmap=new HashMap<String,String>();
					hashmap.put("id", cur.getString(cur.getColumnIndex("id")));
					hashmap.put("videoName", cur.getString(cur.getColumnIndex("videoName")));
					hashmap.put("videoImage", cur.getString(cur.getColumnIndex("videoImage")));
					hashmap.put("file", cur.getString(cur.getColumnIndex("file")));
					hashmap.put("videoDesc", cur.getString(cur.getColumnIndex("videoDesc")));
					arraylist.add(hashmap);
				}while(cur.moveToNext());
			}
		}
		else{
			
		}
		cur.close();
		dbconn.Close();
		return arraylist;
	}
}