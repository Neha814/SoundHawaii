package com.example.soundhawaiiapp;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import lazyadapter.ImageLoader;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.appdata.Appdata;
import com.app.appdata.Constant;
import com.example.soundhawaiiapp.R;
import com.sound.database.Sqlite;
import com.sound.downloadmanger.DownloadService;

public class VideoTrackActivity extends ActionBarActivity implements
		OnClickListener, OnTouchListener, OnBufferingUpdateListener,
		OnCompletionListener, OnPreparedListener {

	Toolbar toolbar;
	ImageView mTrackImage;
	ImageView mNextTrack;
	private boolean tick;
	SharedPreferences sp;
	Bundle extra;
	ArrayList<HashMap<String, String>> trackArraylist = new ArrayList<HashMap<String, String>>();
	HashMap<String, String> trackHashmap = new HashMap<String, String>();
	ArrayList<HashMap<String, String>> galleryArraylist = new ArrayList<HashMap<String, String>>();
	HashMap<String, String> galleryHashmap = new HashMap<String, String>();
	ArrayList<String> imageList = new ArrayList<String>();
	ImageLoader imgLoader;
	Button mPlay, mReplay;
	Button mWithAbiance, mSoundAlone;
	TextView mTxtTrackDesc;
	SeekBar mSeekbar;
	MediaPlayer mediaPlayer;
	private int mediaFileLengthInMilliseconds;
	private final Handler handler = new Handler();
	private String TRACK_URL, TRACK_ID;
	Sqlite dbconn;
	String language;
	Locale myLocale;
	private Locale srcLanguage = Locale.ENGLISH;
	private Locale dstLanguage = Locale.JAPANESE;

	String[] _TRACK_URL = new String[2];
	String[] _TRACK_NAME = new String[2];
	String[] _TRACK_ID = new String[2];
	String[] _TRACK_THUMB = new String[2];

	// LinearLayout txtLayout;
	// ScrollView mScrollView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.trackscreen);
		dbconn = new Sqlite(VideoTrackActivity.this);
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		mTrackImage = (ImageView) findViewById(R.id.imageView1);
		mNextTrack = (ImageView) findViewById(R.id.imageView2);
		mPlay = (Button) findViewById(R.id.btnPlay);
		mReplay = (Button) findViewById(R.id.btnReplay);
		mWithAbiance = (Button) findViewById(R.id.btnAmbiance);
		mSoundAlone = (Button) findViewById(R.id.btnSoundAlone);
		mSeekbar = (SeekBar) findViewById(R.id.seekBar1);
		mTxtTrackDesc = (TextView) findViewById(R.id.txtTrackDesc);

		sp = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		language = sp.getString("language", "en");
		setlocale(language);
		// mScrollView=(ScrollView)findViewById(R.id.scrollview);
		if (toolbar != null) {
			try {
				setSupportActionBar(toolbar);
				getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		mediaPlayer = new MediaPlayer();
		imgLoader = new ImageLoader(VideoTrackActivity.this);
		mNextTrack.setOnClickListener(this);
		mTrackImage.setOnClickListener(this);
		mPlay.setOnClickListener(this);
		mReplay.setOnClickListener(this);
		mWithAbiance.setOnClickListener(this);
		mSoundAlone.setOnClickListener(this);
		mSeekbar.setOnTouchListener(this);
		mediaPlayer.setOnBufferingUpdateListener(this);
		mediaPlayer.setOnPreparedListener(this);
		mediaPlayer.setOnCompletionListener(this);
		// mScrollView.setOnTouchListener(this);
		// mTxtTrackDesc.setOnTouchListener(this);
		mSeekbar.setMax(99);

		extra = getIntent().getExtras();
		System.out.println("track name==" + extra.getString("name"));
		Constant.TRACK_ID = extra.getString("id");
		Constant.TRACK_NAME = extra.getString("name");
		Constant.TRACK_NAME_JAP = extra.getString("nameJap");

		if (language.equalsIgnoreCase("en")) {
			getSupportActionBar().setTitle(
					Html.fromHtml("<font color='"
							+ extra.getString(Constant.TRACK_HEADER_COLOR)
							+ "'>" + Constant.TRACK_NAME + "</font"));
		} else if (language.equalsIgnoreCase("ja")) {
			getSupportActionBar().setTitle(
					Html.fromHtml("<font color='"
							+ extra.getString(Constant.TRACK_HEADER_COLOR)
							+ "'>" + Constant.TRACK_NAME_JAP + "</font"));
		}

		GetTrack();

		System.out.println("media player state===looping"
				+ mediaPlayer.isLooping());
		System.out.println("media player state===playing"
				+ mediaPlayer.isPlaying());
		if (mediaPlayer.isLooping()) {
			mPlay.setBackgroundResource(R.drawable.btnpause);
		} else {

		}
	}

	public void setlocale(String lang) {
		myLocale = new Locale(lang);
		Resources res = getResources();
		DisplayMetrics dm = res.getDisplayMetrics();
		Configuration conf = res.getConfiguration();
		conf.locale = myLocale;
		res.updateConfiguration(conf, dm);

	}

	// Method to get the track url and get the gallery url
	public void GetTrack() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Appdata.showProgressBar(VideoTrackActivity.this, "",
						"Loading...");
				try {
					if (Appdata.isNetworkAvailable(VideoTrackActivity.this)) {
						trackArraylist = ParseTrack(Appdata
								.GetJsonFromUrl(Appdata.SERVER_URL
										+ Appdata.S_TRACK + "&id="
										+ Constant.TRACK_ID));
						System.out.println("track size=="
								+ trackArraylist.size());
						galleryArraylist = ParseGalleryImage(Appdata
								.GetJsonFromUrl(Appdata.SERVER_URL
										+ Appdata.S_GALLERY + "&id="
										+ Constant.TRACK_ID));
						System.out.println("gallery size=="
								+ galleryArraylist.size());
					} else {
						trackArraylist = ParseTrackFromDB();
						System.out.println("track size=="
								+ trackArraylist.size());
						galleryArraylist = parseGalleryFromDB();
						System.out.println("gallery size=="
								+ galleryArraylist.size());
					}

					for (int i = 0; i < galleryArraylist.size(); i++) {
						imageList.add(galleryArraylist.get(i).get(
								"thumbImageFile"));
					}
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							if (galleryArraylist.size() != 0)
								imgLoader.DisplayImage(galleryArraylist.get(0)
										.get("thumbImageFile"), mTrackImage,
										ScaleType.FIT_XY);
							if (trackArraylist.size() != 0) {
								if (language.equalsIgnoreCase("en")) {
									mWithAbiance.setText(trackArraylist.get(0)
											.get("trackName"));
								} else if (language.equalsIgnoreCase("ja")) {

									Thread t = new Thread() {
										String japLang;

										public void run() {
											// japLang =
											// translate(trackArraylist.get(0).get("trackName"));

											japLang = "音だけ";

											runOnUiThread(new Runnable() {

												@Override
												public void run() {
													mWithAbiance
															.setText(japLang);
												}
											});
										};
									};
									t.start();

								}
							}
							if (trackArraylist.size() > 1) {
								if (language.equalsIgnoreCase("en")) {
									mSoundAlone.setText(trackArraylist.get(1)
											.get("trackName"));
								} else if (language.equalsIgnoreCase("ja")) {

									Thread t = new Thread() {
										String japLang;

										public void run() {
											// japLang =
											// translate(trackArraylist.get(1).get("trackName"));
											japLang = "アンビアンス";
											runOnUiThread(new Runnable() {

												@Override
												public void run() {
													mSoundAlone
															.setText(japLang);
												}
											});
										};
									};
									t.start();
								}
							}
							WithAmbianceClick();
							START_SERVICE();
						}

					});

				} catch (Exception e) {
					e.printStackTrace();
				}
				Appdata.closeProgressBar();
			}
		}).start();
	}

	/***************************************** all override method ****************************************/
	@Override
	protected void onResume() {
		super.onResume();
		IntentFilter filter = new IntentFilter(DownloadService.NOTIFICATION);
		registerReceiver(receiver, filter);
		System.out.println("mediaplayer resu,e===" + mediaPlayer.isPlaying());
		if (tick) {
			if (!mediaPlayer.isPlaying()) {
				mediaPlayer.start();
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		System.out.println("inside the on destroy");
		unregisterReceiver(receiver);
		if (mediaPlayer.isPlaying()) {
			mediaPlayer.stop();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			onBackPressed();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (v.getId() == R.id.seekBar1) {
			if (mediaPlayer.isPlaying()) {
				SeekBar sb = (SeekBar) v;
				int playPositionInMillisecconds = (mediaFileLengthInMilliseconds / 100)
						* sb.getProgress();
				mediaPlayer.seekTo(playPositionInMillisecconds);
			}
		}
		/*
		 * if(v.getId()==R.id.scrollview){
		 * mTxtTrackDesc.getParent().requestDisallowInterceptTouchEvent(false);
		 * } if(v.getId()==R.id.txtTrackDesc){
		 * mTxtTrackDesc.getParent().requestDisallowInterceptTouchEvent(true); }
		 */
		return false;
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		mPlay.setBackgroundResource(R.drawable.btnplay);
		// mp.stop();
		// mp.reset();
		// mediaPlayer.release();
		// mSeekbar.setProgress(0);
	}

	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		mSeekbar.setSecondaryProgress(percent);
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		// Appdata.closeProgressBar();
		// mediaPlayer.start();

	}

	@Override
	protected void onPause() {
		super.onPause();
		// if(mediaPlayer.isPlaying()){
		// mediaPlayer.pause();
		// }
		System.out.println("inside the on pause");
	}

	@Override
	protected void onStop() {
		super.onStop();
		System.out.println("inside the on stop");
	}

	@Override
	public void onClick(View v) {
		if (v == mNextTrack || v == mTrackImage) {
			Intent intent = new Intent(VideoTrackActivity.this,
					AlbumActivity.class);
			intent.putStringArrayListExtra(Constant.GALLERY_LIST, imageList);
			startActivity(intent);
		}
		if (v == mPlay)
			TrackPlayClick();
		if (v == mReplay)
			ReplayTrack();
		if (v == mWithAbiance)
			WithAmbianceClick();
		if (v == mSoundAlone)
			SoundAloneClick();
	}

	// Play click
	public void TrackPlayClick() {
		System.out.println("track url==" + TRACK_URL);
		try {
			if (Appdata.isNetworkAvailable(VideoTrackActivity.this)) {
				if (TRACK_URL != null) {
					mediaPlayer.setDataSource(TRACK_URL.replace(" ", "%20"));
				} else {
					Toast.makeText(VideoTrackActivity.this,
							"Track Not Availbale", Toast.LENGTH_SHORT).show();
				}
			} else {
				if (TRACK_URL != null) {
					Uri uri = Uri.parse(TRACK_URL);
					mediaPlayer.setDataSource(VideoTrackActivity.this, uri);
				} else {
					Toast.makeText(VideoTrackActivity.this,
							"Track Not Availbale", Toast.LENGTH_SHORT).show();
				}
			}
			mediaPlayer.prepare();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			Toast.makeText(VideoTrackActivity.this, "Cannot Play",
					Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
		System.out.println("duration");
		System.out.println("duration media==" + mediaPlayer.getDuration());
		mediaFileLengthInMilliseconds = mediaPlayer.getDuration();
		if (!mediaPlayer.isPlaying()) {
			mediaPlayer.start();
			mPlay.setBackgroundResource(R.drawable.btnpause);
			tick = true;
		} else {
			mediaPlayer.pause();
			mPlay.setBackgroundResource(R.drawable.btnplay);
			tick = false;
		}
		SeekBarUpdate();
	}

	public void WithAmbianceClick() {

		if (trackArraylist.size() != 0) {
			TRACK_URL = trackArraylist.get(0).get("trackFile");
			TRACK_ID = trackArraylist.get(0).get("trackId");
			if (language.equalsIgnoreCase("en")) {
				mTxtTrackDesc.setText(trackArraylist.get(0).get("description"));
			} else if (language.equalsIgnoreCase("ja")) {
				Thread t = new Thread() {
					String japLang;

					public void run() {
						// japLang =
						// translate(trackArraylist.get(0).get("description"));
						if (Constant.TRACK_ID.equalsIgnoreCase("122")) {
							japLang = "知ってますか？ハワイに来＃ 1ほとんどの波が離れて日本、ニュージーランド、アラスカのような場所で起こった暴風雨により多くのマイルを生成しています。 ＃ 2波が海を通る水を移動されるのではなく、エネルギー、それが海岸に到達するまで波が前方に移動しないの下に水を移動しています。";
						} else if (Constant.TRACK_ID.equalsIgnoreCase("123")) {
							japLang = "マノア滝は、 150フィートの滝につながるマノア渓谷のハイキングコースです。ハイ。水が下に来るカスケード山の中まで雨高いから来ています。ハイキングは非常に緑豊かな緑だけでなく、泥だらけで倍、約1.5マイルです。光の微妙な滝。";

						} else if (Constant.TRACK_ID.equalsIgnoreCase("124")) {
							japLang = "ハワイの海は、サンゴ礁のオフフィード生態系に住んでいる多くの異なった面白い海の生き物が暮らしています。海域では、海岸から行くどこまで深い応じ-2度Cに85度Fから温度の範囲であり得ます。ハワイ諸島は、水中火山活動によって形成されました。";
						} else if (Constant.TRACK_ID.equalsIgnoreCase("125")) {
							japLang = "ハワイ＃で10の最も一般的な鳥の名前は1コモンキュウカンチョウ＃ 2ゼブラ鳩＃3レッドクレステッドカーディナル＃4イエスズメ＃5は鳩＃ 6ブンチョウの位に斑点7牛白鷺＃8レッドベントヒヨドリ＃9島カナリア＃ 10ネネグース（公式州鳥）自然調和のとれたメロディックチャープ、ツイートは同様にそれを高揚として気分をまろやか。";
						} else if (Constant.TRACK_ID.equalsIgnoreCase("126")) {
							japLang = "ハワイの天気は、 12気候帯の10を持つ、 1が考えることよりも広大です。雪に濡れた土地に乾燥した土地は、ハワイはそれをすべて持っています。ハワイの東側には、体に当たっ各year.Rainは非常に治療的である降雨の300で地球上で最も雨の多い場所」の一つである。それは非常になだめるようであるヒアリング。";
						} else if (Constant.TRACK_ID.equalsIgnoreCase("127")) {
							japLang = "ハワイの火山は溶岩になります玄武岩溶岩がにじみ出ます。ハワイの玄武岩は、約50 ％のシリカ、 10％の鉄、マグネシウムのそれぞれ、およびその他の重要なミネラルが含まれています。どの農家が土の中に溶岩を入れて好きな理由です。溶岩は2,200Fに1,300Fの温度に達します。連続して1983年は非常にユニークなサウンド以来噴火してきたハワイマウナロア、ロイヒとキラウェア3活火山があります。あなたは熱と新しい地殻が形成されて聞くことができます。";
						} else if (Constant.TRACK_ID.equalsIgnoreCase("128")) {
							japLang = "古代ハワイアンの時代にコンクシェルはチャント、儀式のために使用されたとしても、長距離間で通信します。コンクシェルは限り2マイルなどの音を発することができます。それでも今日はあなたが自然なサウンドがあなたの体全体に共振聞くことができ吹き。";
						} else if (Constant.TRACK_ID.equalsIgnoreCase("129")) {
							japLang = "あなたは知っていました？： ＃人間のように1イルカはお互いに名前を与えます。彼らはそれぞれによってお互いを呼び出すためのユニークな笛を持っています。一度脳の片側休止することにより＃ 2イルカの睡眠。これは、彼らが空気のために表面に上昇し、捕食者に目を光らせておくことができます。 ＃ 3イルカは、相互に通信するために多くの音を持っています。";
						} else if (Constant.TRACK_ID.equalsIgnoreCase("130")) {
							japLang = "砂は、岩の小さな遺跡によって形成されています。時間の経過とともに、これらの岩が侵食さと内訳小さな粒子に。ハワイの砂は、サンゴ礁、溶岩の岩、貝殻、海の宝石と異なるミネラルなど、さまざまな要素を構成することができます。いくつかのハワイのビーチで見られる、黒、黄色、赤と緑の砂を説明することができる侵食溶岩から鉱物。";
						} else if (Constant.TRACK_ID.equalsIgnoreCase("131")) {
							japLang = "ウクレレは1880年代のポルトガル移民によってハワイに持って来られました。ウクレレは4弦があり、 4つの異なるsizes-ソプラノ、テノール、コンサート、バリトンで来ます。ほとんどのハワイアンミュージックでは、ウクレレは歌を通して演奏が聞こえます。あなたの体、音のパワーを移動することができます。";
						}
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								mTxtTrackDesc.setText(japLang);
							}
						});
					};
				};
				t.start();

			}
			// int height=txtLayout.getHeight();
			// System.out.println("layout height=="+height);
			// if(mediaPlayer.isPlaying()){
			System.out.println("inside the if");
			try {
				mediaPlayer.stop();
				mediaPlayer.reset();
				if (Appdata.isNetworkAvailable(VideoTrackActivity.this)) {
					if (TRACK_URL != null) {
						mediaPlayer
								.setDataSource(TRACK_URL.replace(" ", "%20"));
					} else {
						Toast.makeText(VideoTrackActivity.this,
								"Track Not Availbale", Toast.LENGTH_SHORT)
								.show();
					}
				} else {
					if (TRACK_URL != null) {
						Uri uri = Uri.parse(TRACK_URL);
						mediaPlayer.setDataSource(VideoTrackActivity.this, uri);
					} else {
						Toast.makeText(VideoTrackActivity.this,
								"Track Not Availbale", Toast.LENGTH_SHORT)
								.show();
					}
				}
				mediaPlayer.prepare();
				mediaFileLengthInMilliseconds = mediaPlayer.getDuration();
				mediaPlayer.start();
				mPlay.setBackgroundResource(R.drawable.btnpause);
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			SeekBarUpdate();
			// }
			/*
			 * else{ System.out.println("inside the else"); try{
			 * if(Appdata.isNetworkAvailable(VideoTrackActivity.this)){
			 * if(TRACK_URL!=null){
			 * mediaPlayer.setDataSource(TRACK_URL.replace(" ", "%20")); } else{
			 * Toast.makeText(VideoTrackActivity.this, "Track Not Availbale",
			 * Toast.LENGTH_SHORT).show(); } } else{ if(TRACK_URL!=null){ Uri
			 * uri=Uri.parse(TRACK_URL);
			 * mediaPlayer.setDataSource(VideoTrackActivity.this,uri); } else{
			 * Toast.makeText(VideoTrackActivity.this, "Track Not Availbale",
			 * Toast.LENGTH_SHORT).show(); } } mediaPlayer.prepare();
			 * mediaFileLengthInMilliseconds = mediaPlayer.getDuration();
			 * mediaPlayer.start();
			 * mPlay.setBackgroundResource(R.drawable.btnpause); } catch
			 * (IllegalStateException e) { e.printStackTrace(); } catch
			 * (IOException e) { e.printStackTrace(); } SeekBarUpdate(); }
			 */
		} else {
			Toast.makeText(VideoTrackActivity.this, "No wav file Available",
					Toast.LENGTH_SHORT).show();
			mTxtTrackDesc.setText("No Description");
			mTxtTrackDesc.setGravity(Gravity.CENTER);
		}
	}

	public void SoundAloneClick() {
		if (trackArraylist.size() != 0 && trackArraylist.size() > 1) {
			TRACK_URL = trackArraylist.get(1).get("trackFile");
			TRACK_ID = trackArraylist.get(1).get("trackId");
			mTxtTrackDesc.setText(trackArraylist.get(1).get("description"));
			// if(mediaPlayer.isPlaying()){
			try {
				mediaPlayer.stop();
				mediaPlayer.reset();
				// mediaPlayer.seekTo(0);
				if (Appdata.isNetworkAvailable(VideoTrackActivity.this)) {
					if (TRACK_URL != null) {
						mediaPlayer
								.setDataSource(TRACK_URL.replace(" ", "%20"));
					} else {
						Toast.makeText(VideoTrackActivity.this,
								"Track Not Availbale", Toast.LENGTH_SHORT)
								.show();
					}
				} else {
					if (TRACK_URL != null) {
						Uri uri = Uri.parse(TRACK_URL);
						mediaPlayer.setDataSource(VideoTrackActivity.this, uri);
					} else {
						Toast.makeText(VideoTrackActivity.this,
								"Track Not Availbale", Toast.LENGTH_SHORT)
								.show();
					}
				}
				mediaPlayer.prepare();
				mediaFileLengthInMilliseconds = mediaPlayer.getDuration();
				mediaPlayer.start();
				mPlay.setBackgroundResource(R.drawable.btnpause);
				SeekBarUpdate();
				/*
				 * if(!mediaPlayer.isPlaying()){ mediaPlayer.start();
				 * mPlay.setBackgroundResource(R.drawable.btnpause); tick=true;
				 * } else { mediaPlayer.pause();
				 * mPlay.setBackgroundResource(R.drawable.btnplay); tick=false;
				 * }
				 */
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// }
		} else {
			Toast.makeText(VideoTrackActivity.this, "No wav file Available",
					Toast.LENGTH_SHORT).show();
		}
	}

	public void ReplayTrack() {
		if (mediaPlayer.isPlaying()) {
			try {
				mediaPlayer.stop();
				mediaPlayer.prepare();
				mediaPlayer.seekTo(0);
				mediaPlayer.start();

			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			TrackPlayClick();
		}

	}

	public void SeekBarUpdate() {
		mSeekbar.setProgress((int) (((float) mediaPlayer.getCurrentPosition() / mediaFileLengthInMilliseconds) * 100));
		if (mediaPlayer.isPlaying()) {
			Runnable notification = new Runnable() {

				@Override
				public void run() {
					SeekBarUpdate();
				}
			};
			handler.postDelayed(notification, 1000);
		}
	}

	public ArrayList<HashMap<String, String>> ParseTrack(JSONObject jsonObject) {
		try {
			if (jsonObject.getString("Response").equalsIgnoreCase("true")) {
				JSONArray jsonArray = jsonObject.getJSONArray("GetData");
				if (jsonArray.length() != 0) {
					dbconn.dbOpen();
					dbconn.delete_track(Constant.TRACK_ID);
					dbconn.Close();
				}
				dbconn.dbOpen();
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObject2 = jsonArray.getJSONObject(i);
					trackHashmap = new HashMap<String, String>();
					trackHashmap.put("thumbName",
							jsonObject2.getString("thumbName"));
					trackHashmap.put("thumbImage",
							jsonObject2.getString("thumbImage"));
					trackHashmap.put("trackId",
							jsonObject2.getString("trackId"));
					trackHashmap.put("trackName",
							jsonObject2.getString("trackName"));
					trackHashmap.put("trackFile",
							jsonObject2.getString("trackFile"));
					trackHashmap.put("PlayType",
							jsonObject2.getString("PlayType"));
					trackHashmap.put("description",
							jsonObject2.getString("description"));
					trackArraylist.add(trackHashmap);

					_TRACK_URL[i] = jsonObject2.getString("trackFile");
					_TRACK_NAME[i] = jsonObject2.getString("trackName");
					_TRACK_ID[i] = jsonObject2.getString("trackId");
					_TRACK_THUMB[i] = jsonObject2.getString("thumbName");

					dbconn.insert_track(
							Constant.TRACK_ID,
							jsonObject2.getString("thumbName"),
							jsonObject2.getString("thumbImage"),
							jsonObject2.getString("trackId"),
							jsonObject2.getString("trackName"),
							Appdata.SD_CARD_PATH
									+ jsonObject2.getString("thumbName") + " "
									+ jsonObject2.getString("trackName")
									+ jsonObject2.getString("trackId") + ".wav",
							jsonObject2.getString("PlayType"),
							jsonObject2.getString("description"));
				}
				dbconn.Close();
			}
		} catch (Exception e) {
		}
		return trackArraylist;
	}

	public ArrayList<HashMap<String, String>> ParseTrackFromDB() {
		dbconn.dbOpen();
		Cursor cur = dbconn.GetTrack(Constant.TRACK_ID);
		if (cur.getCount() != 0) {
			if (cur.moveToFirst()) {
				do {
					trackHashmap = new HashMap<String, String>();
					trackHashmap.put("thumbName",
							cur.getString(cur.getColumnIndex("thumbName")));
					trackHashmap.put("thumbImage",
							cur.getString(cur.getColumnIndex("thumbImage")));
					trackHashmap.put("trackId",
							cur.getString(cur.getColumnIndex("trackId")));
					trackHashmap.put("trackName",
							cur.getString(cur.getColumnIndex("trackName")));
					trackHashmap.put("trackFile",
							cur.getString(cur.getColumnIndex("trackFile")));
					trackHashmap.put("PlayType",
							cur.getString(cur.getColumnIndex("PlayType")));
					trackHashmap.put("description",
							cur.getString(cur.getColumnIndex("description")));
					trackArraylist.add(trackHashmap);
				} while (cur.moveToNext());
			}
		} else {

		}
		cur.close();
		dbconn.Close();
		return trackArraylist;
	}

	public ArrayList<HashMap<String, String>> ParseGalleryImage(
			JSONObject jsonObject) {
		try {
			if (jsonObject.getString("Response").equalsIgnoreCase("true")) {
				JSONArray jsonArray = jsonObject.getJSONArray("GetData");
				if (jsonArray.length() != 0) {
					dbconn.dbOpen();
					dbconn.delete_gallery(Constant.TRACK_ID);
					dbconn.Close();
				}
				dbconn.dbOpen();
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObject2 = jsonArray.getJSONObject(i);
					galleryHashmap = new HashMap<String, String>();
					galleryHashmap.put("id", jsonObject2.getString("id"));
					// galleryHashmap.put("thumbID",
					// jsonObject2.getString("thumbID"));
					galleryHashmap.put("thumbFileName",
							jsonObject2.getString("thumbFileName"));
					galleryHashmap.put("thumbImageFile",
							jsonObject2.getString("thumbImageFile"));
					galleryArraylist.add(galleryHashmap);

					dbconn.insert_gallery(Constant.TRACK_ID,
							jsonObject2.getString("thumbFileName"),
							jsonObject2.getString("thumbImageFile"));

				}
				dbconn.Close();
			}
		} catch (Exception e) {
		}
		return galleryArraylist;
	}

	public ArrayList<HashMap<String, String>> parseGalleryFromDB() {
		dbconn.dbOpen();
		Cursor cur = dbconn.GetGallery(Constant.TRACK_ID);
		if (cur.getCount() != 0) {
			if (cur.moveToFirst()) {
				do {
					galleryHashmap = new HashMap<String, String>();
					galleryHashmap.put("id",
							cur.getString(cur.getColumnIndex("id")));
					galleryHashmap.put("thumbFileName",
							cur.getString(cur.getColumnIndex("thumbFileName")));
					galleryHashmap
							.put("thumbImageFile", cur.getString(cur
									.getColumnIndex("thumbImageFile")));
					galleryArraylist.add(galleryHashmap);
				} while (cur.moveToNext());
			}
		} else {

		}
		cur.close();
		dbconn.Close();
		return galleryArraylist;
	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				String string = bundle.getString(DownloadService.FILEPATH);
				int resultcode = bundle.getInt(DownloadService.RESULT);
				if (resultcode == RESULT_OK) {
					Toast.makeText(VideoTrackActivity.this,
							"download complete", Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(VideoTrackActivity.this, "download failed",
							Toast.LENGTH_LONG).show();
				}
			}
		}
	};

	public void START_SERVICE() {
		Intent intent = new Intent(VideoTrackActivity.this,
				DownloadService.class);
		intent.putExtra(DownloadService.FILENAME, _TRACK_NAME);
		intent.putExtra(DownloadService.URL, _TRACK_URL);
		intent.putExtra(DownloadService.FILEID, _TRACK_ID);
		intent.putExtra(DownloadService.FILETHUMB, _TRACK_THUMB);
		startService(intent);

	}

	/*
	 * public String translate(String text) { String translated = null; try {
	 * String query = URLEncoder.encode(text, "UTF-8"); String langpair =
	 * URLEncoder.encode( srcLanguage.getLanguage() + "|" +
	 * dstLanguage.getLanguage(), "UTF-8"); String url =
	 * "http://mymemory.translated.net/api/get?q=" + query + "&langpair=" +
	 * langpair; HttpClient hc = new DefaultHttpClient(); HttpGet hg = new
	 * HttpGet(url); HttpResponse hr = hc.execute(hg); if
	 * (hr.getStatusLine().getStatusCode() == HttpStatus.SC_OK) { JSONObject
	 * response = new JSONObject( EntityUtils.toString(hr.getEntity()));
	 * translated = response.getJSONObject( "responseData").getString(
	 * "translatedText"); } } catch (Exception e) { e.printStackTrace(); }
	 * return translated; }
	 */
}