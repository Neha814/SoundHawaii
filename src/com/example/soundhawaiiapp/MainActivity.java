package com.example.soundhawaiiapp;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.app.adapter.GridAdapter;
import com.app.appdata.Appdata;
import com.app.appdata.Constant;
import com.app.appdata.InternetConnectionReceiver;
import com.example.soundhawaiiapp.R;
import com.sound.database.Sqlite;

public class MainActivity extends ActionBarActivity implements
		OnItemClickListener, OnClickListener, OnRefreshListener {

	GridView mGridView;
	public ArrayList<HashMap<String, String>> arraylist = new ArrayList<HashMap<String, String>>();

	public HashMap<String, String> hashmap = new HashMap<String, String>();
	GridAdapter mAdapter;
	ImageView mMenuImage;
	Spinner spinner;
	InternetConnectionReceiver receiver;
	Sqlite dbconn;
	TextView mtxtNoResult;
	SwipeRefreshLayout mSwipeRefreshLayout;
	SharedPreferences sp;
	Locale myLocale;
	RelativeLayout background_layout;
	String language;
	String lng;
	private Locale srcLanguage = Locale.ENGLISH;
	private Locale dstLanguage = Locale.JAPANESE;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.e("11111", "11111");
		setContentView(R.layout.homescreen);
		Log.e("22222", "22222");
		dbconn = new Sqlite(MainActivity.this);
		mGridView = (GridView) findViewById(R.id.gridView1);
		mMenuImage = (ImageView) findViewById(R.id.btnmenu_new);
		spinner = (Spinner) findViewById(R.id.menu2);
		mtxtNoResult = (TextView) findViewById(R.id.txt_empty);
		mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
		background_layout = (RelativeLayout) findViewById(R.id.hhh);
		mSwipeRefreshLayout.setColorSchemeColors(Color.RED, Color.GREEN,
				Color.BLUE);
		mGridView.setOnItemClickListener(this);
		mMenuImage.setOnClickListener(this);
		mSwipeRefreshLayout.setOnRefreshListener(this);
		// spinner.setOnItemSelectedListener(clicker);
		IntentFilter filter = new IntentFilter(
				ConnectivityManager.CONNECTIVITY_ACTION);
		receiver = new InternetConnectionReceiver();
		registerReceiver(receiver, filter);

		sp = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());

		if (arraylist.equals(null) || arraylist.size() <= 0) {
			Log.e("if if", "if if");
			LoadThumbnail();
		} else {
			Log.e("else else", "else else");
			mAdapter = new GridAdapter(MainActivity.this, arraylist, language);
			mGridView.setAdapter(mAdapter);
		}

		language = sp.getString("language", "en");
		setLocale(language);

		if (language.equalsIgnoreCase("ja")) {
			background_layout.setBackgroundResource(R.drawable.header_jap);
		} else {
			background_layout.setBackgroundResource(R.drawable.header_eng);
		}

	}
	
	/**
	 * Method to translate English language to Japanese dynamically using
	 * myMemory API
	 * @param lang
	 */

	/*
	 * public String translate(String text) { String translated = null; try {
	 * String query = URLEncoder.encode(text, "UTF-8"); String langpair =
	 * URLEncoder.encode(srcLanguage.getLanguage() + "|" +
	 * dstLanguage.getLanguage(), "UTF-8"); String url =
	 * "http://mymemory.translated.net/api/get?q=" + query + "&langpair=" +
	 * langpair; HttpClient hc = new DefaultHttpClient(); HttpGet hg = new
	 * HttpGet(url); HttpResponse hr = hc.execute(hg); if
	 * (hr.getStatusLine().getStatusCode() == HttpStatus.SC_OK) { JSONObject
	 * response = new JSONObject(EntityUtils.toString(hr .getEntity()));
	 * translated = response.getJSONObject("responseData").getString(
	 * "translatedText"); } } catch (Exception e) { e.printStackTrace(); }
	 * return translated;
	 * 
	 * 
	 * }
	 */

	public void setLocale(String lang) {
		myLocale = new Locale(lang);
		Resources res = getResources();
		DisplayMetrics dm = res.getDisplayMetrics();
		Configuration conf = res.getConfiguration();
		conf.locale = myLocale;
		res.updateConfiguration(conf, dm);

	}

	@Override
	protected void onResume() {
		super.onResume();
		System.out.println("inside the on resume");
		if (language.equalsIgnoreCase("ja")) {
			spinner.setAdapter(new MyAdapter(this, R.layout.row,
					Constant.spinnerValues_ja));
		} else {
			spinner.setAdapter(new MyAdapter(this, R.layout.row,
					Constant.spinnerValues_en));
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menuu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.pop) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		System.out.println("track id=="
				+ arraylist.get(position).get("thumbName"));
		Intent i = new Intent(MainActivity.this, VideoTrackActivity.class);
		i.putExtra("id", arraylist.get(position).get("id"));
		i.putExtra("name", arraylist.get(position).get("thumbName"));
		i.putExtra("nameJap", arraylist.get(position).get("thumbNameJap"));
		i.putExtra(Constant.TRACK_HEADER_COLOR, getColorForHeader(position));
		startActivity(i);
	}

	@Override
	public void onClick(View v) {
		if (v == mMenuImage) {

			spinner.performClick();

		}
	}

	OnItemSelectedListener clicker = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {

		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {

		}
	};

	public class MyAdapter extends ArrayAdapter<String> {

		public MyAdapter(Context ctx, int txtViewResourceId, String[] objects) {
			super(ctx, txtViewResourceId, objects);
		}

		@Override
		public View getDropDownView(int position, View cnvtView, ViewGroup prnt) {
			return getCustomView(position, cnvtView, prnt);
		}

		@Override
		public View getView(int pos, View cnvtView, ViewGroup prnt) {
			return getCustomView(pos, cnvtView, prnt);
		}

		public View getCustomView(final int position, View convertView,
				ViewGroup parent) {
			LayoutInflater inflater = getLayoutInflater();
			View mySpinner = inflater.inflate(R.layout.row, parent, false);
			LinearLayout layout = (LinearLayout) mySpinner
					.findViewById(R.id.layout);
			TextView main_text = (TextView) mySpinner
					.findViewById(R.id.textView1);
			ImageView left_icon = (ImageView) mySpinner
					.findViewById(R.id.imageView1);
			if (language.equalsIgnoreCase("ja")) {
				main_text.setText(Constant.spinnerValues_ja[position]);
			} else {
				main_text.setText(Constant.spinnerValues_en[position]);
			}
			left_icon.setImageResource(Constant.images[position]);
			layout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (position == 1) {
						Intent i = new Intent(MainActivity.this,
								VideoListFragmentActivity.class);
						startActivity(i);
					}
					if (position == 2) {
						Intent i = new Intent(MainActivity.this,
								VocabularActivity.class);
						startActivity(i);
					}
					if (position == 3) {
						Intent i = new Intent(MainActivity.this,
								SpecialThanks.class);
						startActivity(i);
					}
					if (position == 0) {
						Intent i = new Intent(MainActivity.this,
								SounAlarmAcitivity.class);
						startActivity(i);
					}
					if (position == 4) {
						Intent i = new Intent(Intent.ACTION_VIEW);
						i.setData(Uri.parse("http://www.inscentshawaii.com/"));
						startActivity(i);
					}
					if (position == 5) {
						Intent i = new Intent(MainActivity.this,
								SetLanguage.class);
						startActivity(i);
					}
				}
			});
			return mySpinner;
		}
	}

	/****************************************************************************************************/
	public void LoadThumbnail() {
		Appdata.context = this;
		arraylist.clear();
		new Thread(new Runnable() {

			@Override
			public void run() {
				Appdata.showProgressBar(MainActivity.this, "", "Loading...");
				System.out.println("network===" + Appdata.Network);
				try {
					if (Appdata.isNetworkAvailable(MainActivity.this)) {
						arraylist = parseThumbnail(Appdata
								.GetJsonFromUrl(Appdata.SERVER_URL
										+ Appdata.S_THUMBNAIL));
					} else {
						arraylist = GetThumbNailFromDB();
					}

					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							if (arraylist.size() == 0) {
								mGridView.setVisibility(View.GONE);
								mtxtNoResult.setVisibility(View.VISIBLE);
							}
							mAdapter = new GridAdapter(MainActivity.this,
									arraylist, language);
							mGridView.setAdapter(mAdapter);
						}
					});
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				Appdata.closeProgressBar();
			}
		}).start();
	}

	// Method to parse the thumbnail jsonobject
	public ArrayList<HashMap<String, String>> parseThumbnail(
			JSONObject jsonObject) {
		if (Appdata.isNetworkAvailable(MainActivity.this)) {
			try {
				if (jsonObject.getString("Response").equalsIgnoreCase("true")) {
					JSONArray jsonArray = jsonObject.getJSONArray("GetData");
					
					if (jsonArray.length() != 0) {
						dbconn.dbOpen();
						dbconn.delete_thumbnail();
						dbconn.Close();
					}
					
					for (int i = 0; i < jsonArray.length(); i++) {
						final JSONObject jsonObject2 = jsonArray
								.getJSONObject(i);
						hashmap = new HashMap<String, String>();
						hashmap.put("id", jsonObject2.getString("id"));
						hashmap.put("adminID", jsonObject2.getString("adminID"));
						hashmap.put("thumbName",
								jsonObject2.getString("thumbName"));
						
						/**
						 *  convert to Japanese
						 */
						
						/*
						 * hashmap.put("thumbNameJap",
						 * translate(jsonObject2.getString("thumbName")));
						 */

						if (jsonObject2.getString("id").equalsIgnoreCase("122")) {
							hashmap.put("thumbNameJap", "ハワイの波");
						} else if (jsonObject2.getString("id")
								.equalsIgnoreCase("123")) {
							hashmap.put("thumbNameJap", "マノア滝");
						} else if (jsonObject2.getString("id")
								.equalsIgnoreCase("124")) {
							hashmap.put("thumbNameJap", "水面下の");
						} else if (jsonObject2.getString("id")
								.equalsIgnoreCase("125")) {
							hashmap.put("thumbNameJap", "ハワイ鳥");
						} else if (jsonObject2.getString("id")
								.equalsIgnoreCase("126")) {
							hashmap.put("thumbNameJap", "ハワイ雨");
						} else if (jsonObject2.getString("id")
								.equalsIgnoreCase("127")) {
							hashmap.put("thumbNameJap", "溶岩流");
						} else if (jsonObject2.getString("id")
								.equalsIgnoreCase("128")) {
							hashmap.put("thumbNameJap", "コンクシェル");
						} else if (jsonObject2.getString("id")
								.equalsIgnoreCase("129")) {
							hashmap.put("thumbNameJap", "イルカ");
						} else if (jsonObject2.getString("id")
								.equalsIgnoreCase("130")) {
							hashmap.put("thumbNameJap", "ビーチの上を歩きます");
						} else if (jsonObject2.getString("id")
								.equalsIgnoreCase("131")) {
							hashmap.put("thumbNameJap", "ウクレレ");
						}

						hashmap.put("thumbImage",
								jsonObject2.getString("thumbImage"));
						arraylist.add(hashmap);

						Log.e("arraylist====", "" + arraylist);
						dbconn.dbOpen();
						dbconn.insert_thumbnail(jsonObject2.getString("id"),
								jsonObject2.getString("adminID"),
								jsonObject2.getString("thumbName"),
								jsonObject2.getString("thumbImage"));
						dbconn.Close();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {

		}
		return arraylist;
	}

	public ArrayList<HashMap<String, String>> GetThumbNailFromDB() {
		dbconn.dbOpen();
		Cursor cur = dbconn.GetThumbnail();
		System.out.println("cursor lenght===" + cur.getCount());
		if (cur.getCount() != 0) {
			if (cur.moveToFirst()) {
				do {
					System.out.println("ii=="
							+ cur.getString(cur.getColumnIndex("id")));
					System.out.println("ii=="
							+ cur.getString(cur.getColumnIndex("adminID")));
					System.out.println("ii=="
							+ cur.getString(cur.getColumnIndex("thumbName")));
					System.out.println("ii=="
							+ cur.getString(cur.getColumnIndex("thumbImage")));
					hashmap = new HashMap<String, String>();
					hashmap.put("id", cur.getString(cur.getColumnIndex("id")));
					hashmap.put("adminID",
							cur.getString(cur.getColumnIndex("adminID")));
					hashmap.put("thumbName",
							cur.getString(cur.getColumnIndex("thumbName")));
					hashmap.put("thumbImage",
							cur.getString(cur.getColumnIndex("thumbImage")));
					arraylist.add(hashmap);
				} while (cur.moveToNext());
			}
		}
		cur.close();
		dbconn.Close();
		return arraylist;
	}

	@Override
	public void onRefresh() {
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				mSwipeRefreshLayout.setRefreshing(false);
				LoadThumbnail();
			}
		}, 5000);
	}

	public String getColorForHeader(int position) {
		String color = "#ffffff";
		if (position == 0)
			color = "#3163f8";
		else if (position == 1)
			color = "#03bf35";
		else if (position == 2)
			color = "#039FC2";
		else if (position == 3)
			color = "#938989";
		else if (position == 4)
			color = "#ffffff";
		else if (position == 5)
			color = "#FB3701";
		else if (position == 6)
			color = "#FD8F20";
		else if (position == 7)
			color = "#DCDBDA";
		else if (position == 8)
			color = "#FFF8A3";
		else if (position == 9)
			color = "#B0A227";
		else
			color = "#ffffff";

		return color;
	}
}