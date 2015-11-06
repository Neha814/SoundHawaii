package com.sound.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class Sqlite {

	private static String DATABASE_NAME="hawaii";
	private static int DATABASE_VERSION=1;
	public SQLiteDatabase db;
	public SQDbhelper dbHelper;
	public Context context;
	
	public static final String tb_thumbnail="create table tb_thumbnail(_id integer primary key autoincrement,"
			+"id text,adminID text,thumbName text,thumbImage text);";
	public static final String tb_videolist="create table tb_videolist(_id integer primary key autoincrement,"
			+"id text,videoName text,videoImage text,file text,videoDesc text);";
	public static final String tb_track="create table tb_track(_id integer primary key autoincrement,"
			+"id text,thumbName text,thumbImage text,trackId text,trackName text,trackFile text,PlayType text,description text);";
	public static final String tb_gallery="create table tb_gallery(_id integer primary key autoincrement,"
			+"id text,thumbFileName text,thumbImageFile text);";
	
	public static final String tb_time="create table tb_time(_id integer primary key autoincrement,"
			+"time text , position text);";
	public Sqlite(Context context){
		System.out.println("come in context");
		this.context=context;
		dbHelper=new SQDbhelper(context);
	}
	
	public static class SQDbhelper extends SQLiteOpenHelper{

		public SQDbhelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(tb_thumbnail);
			db.execSQL(tb_videolist);
			db.execSQL(tb_track);
			db.execSQL(tb_gallery);
			db.execSQL(tb_time);
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			
		}
	}
	
	public Sqlite dbOpen(){
		synchronized (this) {
			db=dbHelper.getWritableDatabase();
			return this;
		}
	}
	public void Close(){
		synchronized(this){
			dbHelper.close();
		}
	}
	
	public long insert_thumbnail(String id,String adminid,String thumbname,String thumbimage){
		ContentValues values=new ContentValues();
		values.put("id", id);
		values.put("adminID", adminid);
		values.put("thumbName", thumbname);
		values.put("thumbImage", thumbimage);
		return db.insert("tb_thumbnail", null, values);
	}
	
	public long insert_video(String id,String videoname,String videoiamge,String file,String videodesc){
		ContentValues values=new ContentValues();
		values.put("id", id);
		values.put("videoName", videoname);
		values.put("videoImage", videoiamge);
		values.put("file", file);
		values.put("videoDesc", videodesc);
		return db.insert("tb_videolist", null, values);
	}
	public long insert_track(String id,String thumbName,String thumbImage,String trackId,String trackName,
			String trackFile,String PlayType,String description){
		ContentValues values=new ContentValues();
		values.put("id", id);
		values.put("thumbName", thumbName);
		values.put("thumbImage", thumbImage);
		values.put("trackId", trackId);
		values.put("trackName", trackName);
		values.put("trackFile", trackFile);
		values.put("PlayType", PlayType);
		values.put("description", description);
		return db.insert("tb_track", null, values);
	}
	
	public long insert_gallery(String id,String thumbFileName,String thumbImageFile){
		ContentValues values=new ContentValues();
		values.put("id", id);
		values.put("thumbFileName", thumbFileName);
		values.put("thumbImageFile", thumbImageFile);
		return db.insert("tb_gallery", null, values);
	}
	
	public long insert_time(String time,int position){
		ContentValues values=new ContentValues();
		values.put("time", time);
		values.put("position", position);
		return db.insert("tb_time", null, values);
	}
	
	public long update_time(int pos,String time){
		ContentValues values=new ContentValues();
		values.put("time", time);
		return db.update("tb_time", values, "position='"+pos+"'", null);
	}
	
	public Cursor GetThumbnail(){
		String qry="select * from tb_thumbnail";
		return db.rawQuery(qry, null);
	}
	
	public Cursor GetVideolist(){
		String qry="select * from tb_videolist";
		return db.rawQuery(qry, null);
	}
	
	public Cursor GetTrack(String id){
		String qry="select * from tb_track where id='"+id+"'";
		return db.rawQuery(qry, null);
	}
	
	public Cursor GetGallery(String id){
		String qry="select * from tb_gallery where id='"+id+"'";
		return db.rawQuery(qry, null);
	}
	public Cursor GetTime(){
		String qry="select * from tb_time";
		return db.rawQuery(qry, null);
	}
	public int check_time(int pos){
		int val=0;
		dbOpen();
		String qry="select * from tb_time where position='"+pos+"'";
		Cursor cur=db.rawQuery(qry, null);
		val=cur.getCount();
		cur.close();
		Close();
		return val;
	}
	
	
	public long delete_thumbnail(){
		return db.delete("tb_thumbnail", null, null);
	}
	public long delete_videlist(){
		return db.delete("tb_videolist", null, null);
	}
	public long delete_track(String id){
		return db.delete("tb_track", "id='"+id+"'", null);
	}
	public long delete_gallery(String id){
		return db.delete("tb_gallery", "id='"+id+"'", null);
	}
	public  long delete_time(int position){
		return db.delete("tb_time", "position='"+position+"'", null);
	}
}