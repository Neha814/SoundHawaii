package com.app.appdata;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;

import com.sound.database.Sqlite;

public class Appdata extends Application{
	
	//public static String SERVER_URL="http://phphosting.osvin.net/Jproject/hawaii/api/api.php?m=";
	public static String SERVER_URL="http://admin.sportscamhawaii.com/api/api.php?m=";
	public static String S_THUMBNAIL="thumbList";
	public static String S_VIDEOLIST="videoList";
	public static String S_GALLERY="allImage";
	public static String S_TRACK="trackScreen";
	public static boolean Network;
	public static Context context;
	public static Handler handler;
	public static ProgressDialog progressDialog;
	public static Sqlite dbSql;
	public static HashMap<String, String>hashmap=new HashMap<String, String>();
	public static ArrayList<HashMap<String, String>>arraylist=new ArrayList<HashMap<String,String>>();
	public static HashMap<String, String>hashmap1=new HashMap<String, String>();
	public static ArrayList<HashMap<String, String>>arraylist1=new ArrayList<HashMap<String,String>>();
	public static String SD_CARD_PATH;
	@Override
	public void onCreate() {
		super.onCreate();
		System.out.println("context=="+context);
		System.out.println("gett=="+getApplicationContext());
		dbSql=new Sqlite(getApplicationContext());
		SD_CARD_PATH=android.os.Environment.getExternalStorageDirectory().getAbsolutePath()+"/20track/";
		System.out.println("path==="+SD_CARD_PATH);
		handler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if(msg.what==1){
					progressDialog=ProgressDialog.show(context, "","Loading...",true);
				}
				if(msg.what==2){
					progressDialog.cancel();
				}
			}
		};	
	}	
	// progress dialog show
	public static void showProgressBar(final Context context,final String title,final String msg){
			handler.post(new Runnable() {
			
			@Override
			public void run() {
				progressDialog=ProgressDialog.show(context, title, msg,true,true,new OnCancelListener() {
					
					@Override
					public void onCancel(DialogInterface dialog) {
						
					}
				});
				
				progressDialog.setCanceledOnTouchOutside(false);
			}
		});
	}
	//progress dialog close
	public static void closeProgressBar(){
		handler.post(new Runnable() {
			
			@Override
			public void run() {
				progressDialog.dismiss();
			}
		});
	}
	
	//Method to get the json from url
	public static JSONObject GetJsonFromUrl(String url)throws Exception {
		
		System.out.println("url=="+url);
		JSONObject jsonObject=null;
		DefaultHttpClient client=new DefaultHttpClient();
		HttpGet request=new HttpGet(url);
		request.setHeader("Accept", "application/json");
		request.setHeader("Content-type","application/json");
		HttpResponse response=client.execute(request);
		BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
		StringBuilder builder = new StringBuilder();
		for (String line = null; (line = reader.readLine()) != null;) {
		    builder.append(line).append("\n");
		}
		jsonObject=new JSONObject(builder.toString());
		return jsonObject;
		
	}
	
	//method to parse the thumbnail jsonobject
	public static ArrayList<HashMap<String, String>> parseThumbnail(JSONObject jsonObject) {
		System.out.println("inside the parsethumbnail");
		Appdata.arraylist.clear();
		if(Appdata.isNetworkAvailable(context)){
			System.out.println("inside if");
			try{
				JSONArray jsonArray=jsonObject.getJSONArray("GetData");
				for(int i=0;i<jsonArray.length();i++){
					JSONObject jsonObject2=jsonArray.getJSONObject(i);
					Appdata.hashmap=new HashMap<String,String>();
					Appdata.hashmap.put("id", jsonObject2.getString("id"));
					Appdata.hashmap.put("adminID", jsonObject2.getString("adminID"));
					Appdata.hashmap.put("thumbName", jsonObject2.getString("thumbName"));
					Appdata.hashmap.put("thumbImage", jsonObject2.getString("thumbImage"));
					Appdata.arraylist.add(Appdata.hashmap);
					
					dbSql.dbOpen();
					dbSql.delete_thumbnail();
					dbSql.insert_thumbnail(jsonObject2.getString("id"),jsonObject2.getString("adminID"),
										   jsonObject2.getString("thumbName"),jsonObject2.getString("thumbImage"));
					dbSql.Close();
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		else{
			System.out.println("inside the else");
			dbSql.dbOpen();
			Cursor cur=dbSql.GetThumbnail();
			if(cur.getCount()!=0){
				if(cur.moveToFirst()){
					do{
						Appdata.hashmap=new HashMap<String,String>();
						Appdata.hashmap.put("id", cur.getString(cur.getColumnIndex("id")));
						Appdata.hashmap.put("adminID", cur.getString(cur.getColumnIndex("adminID")));
						Appdata.hashmap.put("thumbName", cur.getString(cur.getColumnIndex("thumbName")));
						Appdata.hashmap.put("thumbImage", cur.getString(cur.getColumnIndex("thumbImage")));
						Appdata.arraylist.add(Appdata.hashmap);
					}while(cur.moveToNext());
				}
			}
			cur.close();
			dbSql.Close();
		}
		
		return arraylist;
	}
	
	public static ArrayList<HashMap<String, String>>GetThumbNailFromDB() {
		dbSql.dbOpen();
		Cursor cur=dbSql.GetThumbnail();
		System.out.println("cursor lenght==="+cur.getCount());
		if(cur.getCount()!=0){
			if(cur.moveToFirst()){
				do{
					System.out.println("ii=="+cur.getString(cur.getColumnIndex("id")));
					System.out.println("ii=="+cur.getString(cur.getColumnIndex("adminID")));
					System.out.println("ii=="+cur.getString(cur.getColumnIndex("thumbName")));
					System.out.println("ii=="+cur.getString(cur.getColumnIndex("thumbImage")));
					Appdata.hashmap=new HashMap<String,String>();
					Appdata.hashmap.put("id", cur.getString(cur.getColumnIndex("id")));
					Appdata.hashmap.put("adminID", cur.getString(cur.getColumnIndex("adminID")));
					Appdata.hashmap.put("thumbName", cur.getString(cur.getColumnIndex("thumbName")));
					Appdata.hashmap.put("thumbImage", cur.getString(cur.getColumnIndex("thumbImage")));
					Appdata.arraylist.add(Appdata.hashmap);
				}while(cur.moveToNext());
			}
		}
		cur.close();
		dbSql.Close();
		return arraylist;
	}
	
	public static ArrayList<HashMap<String, String>>ParseVideoList(JSONObject jsonObject){
		Appdata.arraylist.clear();
		try {
			JSONArray jsonArray=jsonObject.getJSONArray("GetData");
			for(int i=0;i<jsonArray.length();i++){
				JSONObject jsonObject2=jsonArray.getJSONObject(i);
				Appdata.hashmap=new HashMap<String,String>();
				Appdata.hashmap.put("id", jsonObject2.getString("id"));
				Appdata.hashmap.put("videoName", jsonObject2.getString("videoName"));
				Appdata.hashmap.put("videoImage", jsonObject2.getString("videoImage"));
				Appdata.hashmap.put("file", jsonObject2.getString("file"));
				Appdata.hashmap.put("videoDesc", jsonObject2.getString("videoDesc"));
				Appdata.arraylist.add(Appdata.hashmap);
			}
		} catch (Exception e) {
		}
		return arraylist;
	}
	public static ArrayList<HashMap<String, String>>ParseGalleryImage(JSONObject jsonObject)
	{
		Appdata.arraylist.clear();
		try {
			JSONArray jsonArray=jsonObject.getJSONArray("GetData");
			for(int i=0;i<jsonArray.length();i++){
				JSONObject jsonObject2=jsonArray.getJSONObject(i);
				Appdata.hashmap=new HashMap<String,String>();
				Appdata.hashmap.put("id", jsonObject2.getString("id"));
				Appdata.hashmap.put("thumbID", jsonObject2.getString("thumbID"));
				Appdata.hashmap.put("thumbFileName", jsonObject2.getString("thumbFileName"));
				Appdata.hashmap.put("thumbImageFile", jsonObject2.getString("thumbImageFile"));
				Appdata.arraylist.add(Appdata.hashmap);
			}
		} catch (Exception e) {
		}
		return Appdata.arraylist;
	}
	
	public static ArrayList<HashMap<String, String>>ParseTrack(JSONObject jsonObject){
		Appdata.arraylist1.clear();
		try {
			JSONArray jsonArray=jsonObject.getJSONArray("GetData");
			for(int i=0;i<jsonArray.length();i++){
				JSONObject jsonObject2=jsonArray.getJSONObject(i);
				Appdata.hashmap1=new HashMap<String,String>();
				Appdata.hashmap1.put("thumbName", jsonObject2.getString("thumbName"));
				Appdata.hashmap1.put("thumbImage", jsonObject2.getString("thumbImage"));
				Appdata.hashmap1.put("trackId", jsonObject2.getString("trackId"));
				Appdata.hashmap1.put("trackName", jsonObject2.getString("trackName"));
				Appdata.hashmap1.put("trackFile", jsonObject2.getString("trackFile"));
				Appdata.hashmap1.put("PlayType", jsonObject2.getString("PlayType"));
				Appdata.hashmap1.put("description", jsonObject2.getString("description"));
				Appdata.arraylist1.add(Appdata.hashmap1);
			}
		} catch (Exception e) {
		}
		return Appdata.arraylist1;
	}
	
	
	public static  boolean isNetworkAvailable(Context context){
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
	
	public static  boolean CheckWavFile(String filename){
		File file=new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath()+"/20track/");
		File f=new File(file, filename);
		if(f.exists())
		{
			return true;
		}
		else
		{
			return false;
		}
	}
}