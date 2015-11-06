package com.example.soundhawaiiapp;

import com.app.adapter.AlbumAdapter;
import com.app.appdata.Constant;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.TextView;
import com.example.soundhawaiiapp.R;
public class AlbumActivity extends ActionBarActivity{
	ViewPager viewpager;
	AlbumAdapter mAdapter;
	TextView txtEmpty;
	Bundle extra;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.albumscreen);
		viewpager=(ViewPager)findViewById(R.id.viewpager1);
		txtEmpty=(TextView)findViewById(R.id.textView1);
		extra=getIntent().getExtras();
		if(extra.getStringArrayList(Constant.GALLERY_LIST).size()==0){
			viewpager.setVisibility(View.GONE);
			txtEmpty.setVisibility(View.VISIBLE);
		}
		
		
		mAdapter=new AlbumAdapter(AlbumActivity.this,extra.getStringArrayList(Constant.GALLERY_LIST));
		viewpager.setAdapter(mAdapter);
		//viewpager.setCurrentItem(0);
		//viewpager.setPadding(1, 0, 100, 0);
		//viewpager.setClipToPadding(false);
		//viewpager.setPageMargin(10);
		//System.out.println("ittt="+getCurrentPageIndex(viewpager));
	}
	private int getCurrentPageIndex(ViewPager vp){
	    int first,second,id1,id2 = 0,left;
	    id1 = first = second = 99999999;
	    View v;
	    for ( int i = 0, k = vp.getChildCount() ; i < k ; ++i ) {
	        left = vp.getChildAt(i).getRight();
	        if ( left < second ) {
	            if ( left < first ) {
	                second = first;
	                id2 = id1;
	                first = left;
	                id1 = i;
	            } else {
	                second = left;
	                id2 = i;
	            }
	        }
	    }
	    return id2;
	}
}