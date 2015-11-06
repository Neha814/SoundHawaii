package com.example.soundhawaiiapp;
	
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.soundhawaiiapp.R;
	
public class WhyActivity extends ActionBarActivity{
	
	Toolbar toolbar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.whyscreen);
		toolbar=(Toolbar)findViewById(R.id.toolbar);
		if(toolbar!=null){
			try {
				setSupportActionBar(toolbar);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId()==android.R.id.home){
			onBackPressed();
		}
		return super.onOptionsItemSelected(item);
	}
}