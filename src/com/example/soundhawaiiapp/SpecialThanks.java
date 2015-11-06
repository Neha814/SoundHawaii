package com.example.soundhawaiiapp;

import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.soundhawaiiapp.R;

public class SpecialThanks extends ActionBarActivity{
	
	Toolbar toolbar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.specialthnaks);
		
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		
		if (toolbar != null) {
			try {
				setSupportActionBar(toolbar);
				getSupportActionBar().setTitle(getResources().getString(R.string.specialthankstitle));
				getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			onBackPressed();
		}
		return super.onOptionsItemSelected(item);
	}
}
