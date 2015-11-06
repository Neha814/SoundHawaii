package com.sound.downloadmanger;

import java.io.IOException;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.IBinder;
import android.os.PowerManager;

public class MusicService extends Service implements OnPreparedListener, OnCompletionListener, OnErrorListener
{
	MediaPlayer mediaPlayer;
	String currentSong;

	@Override
	public void onCreate() {
		super.onCreate();
		mediaPlayer=new MediaPlayer(); 
		intitMusicPlayer();
	}
	public void intitMusicPlayer(){
		mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mediaPlayer.setOnPreparedListener(this);
		mediaPlayer.setOnCompletionListener(this);
		mediaPlayer.setOnErrorListener(this);
	}
	public void SetSong(String song){
		currentSong=song;
	}
	public void playSong(){
		mediaPlayer.reset();
		try {
			mediaPlayer.setDataSource(currentSong);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mediaPlayer.prepareAsync();
	}
	@Override
	public boolean onUnbind(Intent intent) {
		mediaPlayer.stop();
		mediaPlayer.release();
		return false;
	}
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		return false;
	}
	@Override
	public void onCompletion(MediaPlayer mp) {
		
	}
	@Override
	public void onPrepared(MediaPlayer mp) {
		mp.start();
	}
}