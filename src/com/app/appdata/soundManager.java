package com.app.appdata;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.os.Environment;

import com.sound.database.Sqlite;

public class soundManager {
	
	public final String MEDIA_PATH=Environment.getExternalStorageDirectory().getPath()+"/20track/";
	public String mp3Pattern=".wav";
	public ArrayList<HashMap<String, String>>arraylist=new ArrayList<HashMap<String,String>>();
	Sqlite dbconn;
	private Context context;
	public soundManager(Context c) {
		this.context=c;
		dbconn=new Sqlite(context);
	}
	
	
	public void ScanSound(File file){
		if(file!=null){
			File[] f=file.listFiles();
			if(f!=null & f.length>0){
				for(File ff:f){
					if(ff.isDirectory()){
						ScanSound(ff);
					}
					else{
						addSongToList(ff);
					}
				}
			}
		}
	}

	public ArrayList<HashMap<String, String>>getSound(){
		arraylist.clear();
		System.out.println("Media Path=="+MEDIA_PATH);
		if(MEDIA_PATH!=null){
			File home=new File(MEDIA_PATH);
			File[] listfile=home.listFiles();
			if(listfile!=null && listfile.length>0){
				for(File file:listfile){
					if(file.isDirectory())
						ScanSound(file);
					else
						addSongToList(file);
				}
			}
		}
		return arraylist;
	}
	
	private void addSongToList(File file) {
		if(file.getName().endsWith(mp3Pattern)){
			HashMap<String, String>hashmap=new HashMap<String, String>();
			hashmap.put("title", file.getName().substring(0, (file.getName().length()-7)));
			hashmap.put("path", file.getPath());
			arraylist.add(hashmap);
		}
	}
}