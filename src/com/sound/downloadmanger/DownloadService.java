package com.sound.downloadmanger;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import com.app.appdata.Appdata;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class DownloadService extends IntentService {
	Thread dx;
	public static final String URL = "urlpath";
	private int Result = Activity.RESULT_CANCELED;
	public static final String NOTIFICATION = "service receiver";
	public static final String FILENAME = "filename";
	public static final String FILEPATH = "filepath";
	public static final String FILEID = "fileid";
	public static final String FILETHUMB = "filethumb";
	public static final String RESULT = "result";
	public static String[] TRACK_URL;
	public static String[] TRACK_NAME;
	public static String[] TRACK_ID;
	public static String[] TRACK_THUMB;

	public DownloadService() {
		super("DownloadService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// String urlPath=intent.getStringExtra(URL);
		// String filename=intent.getStringExtra(FILENAME);
		TRACK_URL = intent.getStringArrayExtra(URL);
		TRACK_NAME = intent.getStringArrayExtra(FILENAME);
		TRACK_ID = intent.getStringArrayExtra(FILEID);
		TRACK_THUMB = intent.getStringArrayExtra(FILETHUMB);
		Log.e("comTo download","comeTo downlaod");
		downloadFile();
	}

	public void publishResult(String outputPath, int result) {
		Intent intent = new Intent(NOTIFICATION);
		intent.putExtra(FILEPATH, outputPath);
		intent.putExtra(RESULT, result);
		sendBroadcast(intent);
	}

	public void downloadFile() {
		dx = new Thread() {
			public void run() {
				for (int i = 0; i < TRACK_NAME.length; i++) {
					Log.e("length=====>>", "" + TRACK_NAME.length);
					Log.e("fileNAME to chk=====>>", "" + TRACK_THUMB[i] + " "
							+ TRACK_NAME[i] + TRACK_ID[i]);
					if (Appdata.CheckWavFile("track" + TRACK_THUMB[i] + " "
							+ TRACK_NAME[i] + TRACK_ID[i])) {
						System.out.println("file is already exits "
								+ TRACK_THUMB[i] + " " + TRACK_NAME[i]
								+ TRACK_ID[i]);
						Toast.makeText(getApplicationContext(),
								"file already exist", Toast.LENGTH_LONG).show();
					} else {
						File root = android.os.Environment
								.getExternalStorageDirectory();
						File dir = new File(root.getAbsolutePath()
								+ "/20track/");
						if (dir.exists() == false) {
							dir.mkdirs();
						}
						try {
							URL url = new URL(TRACK_URL[i].replace(" ", "%20"));
							Log.i("FILE_NAME", "File name is " + TRACK_NAME[i]
									+ "wav");
							Log.i("FILE_URLLINK", "File URL is " + TRACK_URL[i]);
							Log.i("File _ID", "File id is " + TRACK_ID[i]);
							Log.i("FILE_THUMB", "File thumb is"
									+ TRACK_THUMB[i]);
							URLConnection connection = url.openConnection();
							connection.connect();
							int fileLength = connection.getContentLength();
							InputStream input = new BufferedInputStream(
									url.openStream());
							OutputStream output = new FileOutputStream(dir
									+ "/" + TRACK_THUMB[i] + " "
									+ TRACK_NAME[i] + TRACK_ID[i] + ".wav");
							Log.e("fileNAME to save=====>>", "" + dir + "/"
									+ TRACK_THUMB[i] + " " + TRACK_NAME[i]
									+ TRACK_ID[i] + ".wav");
							byte data[] = new byte[1024];
							long total = 0;
							int count;
							while ((count = input.read(data)) != -1) {
								total += count;

								output.write(data, 0, count);
							}
							Result = Activity.RESULT_OK;
							output.flush();
							output.close();
							input.close();
							publishResult(dir.getAbsolutePath(), Result);
						} catch (Exception e) {
							e.printStackTrace();
							Log.i("ERROR ON DOWNLOADING FILES", "ERROR IS" + e);
						}
					}
				}
			}
		};
		dx.start();
	}
}