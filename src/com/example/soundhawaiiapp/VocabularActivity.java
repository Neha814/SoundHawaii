package com.example.soundhawaiiapp;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.soundhawaiiapp.R;

public class VocabularActivity extends ActionBarActivity{
	Toolbar toolbar;
	TextView text1,text3,text5,text7,text9,text11,text13,text15,text17;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vocabulary_screen);
		toolbar=(Toolbar)findViewById(R.id.toolbar);
		if(toolbar!=null){
			try {
				setSupportActionBar(toolbar);
				getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		getSupportActionBar().setTitle(getResources().getString(R.string.Hawaiian_Vocabulary));
		
		text1=(TextView)findViewById(R.id.textView1);
		
		text3=(TextView)findViewById(R.id.textView3);
		text5=(TextView)findViewById(R.id.textView5);
		text7=(TextView)findViewById(R.id.textView7);
		text9=(TextView)findViewById(R.id.textView9);
		text11=(TextView)findViewById(R.id.textView11);
		text13=(TextView)findViewById(R.id.textView13);
		text15=(TextView)findViewById(R.id.textView15);
		text17=(TextView)findViewById(R.id.textView17);
//		text1.setText(Html.fromHtml("<u>Mahalo(Ma-ha-lo):</u>"));
//		text3.setText(Html.fromHtml("<u>Aloha(Ah-lo-ha):</u>"));
//		text5.setText(Html.fromHtml("<u>Poke(Poh-keh):</u>"));
//		text7.setText(Html.fromHtml("<u>Ono(Oh-no):</u>"));
//		text9.setText(Html.fromHtml("<u>Pupu(Poo-poo):</u>"));
//		text11.setText(Html.fromHtml("<u>Pau(Pow):</u>"));
//		text13.setText(Html.fromHtml("<u>Ohana(Oh-ha-na):</u>"));
//		text15.setText(Html.fromHtml("<u>Keiki(Kay-Key):</u>"));
//		text17.setText(Html.fromHtml("<u>Lanai(La-na-ee):</u>"));
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId()==android.R.id.home){
			onBackPressed();
		}
		return super.onOptionsItemSelected(item);
	}
	
	
}
