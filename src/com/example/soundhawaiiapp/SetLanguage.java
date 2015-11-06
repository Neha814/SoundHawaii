package com.example.soundhawaiiapp;

import java.util.Locale;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.soundhawaiiapp.R;

public class SetLanguage extends ActionBarActivity {

	Toolbar toolbar;
	Button english, french;
	Locale myLocale;
	SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_language);

		toolbar = (Toolbar) findViewById(R.id.toolbar);
		english = (Button) findViewById(R.id.english);
		french = (Button) findViewById(R.id.french);
		
		sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

		if (toolbar != null) {
			try {
				setSupportActionBar(toolbar);
				getSupportActionBar().setTitle(getResources().getString(R.string.set_language));
				getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		english.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				setLocale("en");
				Editor e = sp.edit();
				e.putString("language", "en");
				e.commit();
			}
		});

		french.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				setLocale("ja");
				Editor e = sp.edit();
				e.putString("language", "ja");
				e.commit();
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			onBackPressed();
		}
		return super.onOptionsItemSelected(item);
	}

	public void setLocale(String lang) {
		myLocale = new Locale(lang);
		Resources res = getResources();
		DisplayMetrics dm = res.getDisplayMetrics();
		Configuration conf = res.getConfiguration();
		conf.locale = myLocale;
		res.updateConfiguration(conf, dm);
		
		Intent refresh = new Intent(SetLanguage.this, MainActivity.class);
		startActivity(refresh);
	}
}
