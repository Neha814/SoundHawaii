package com.app.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import lazyadapter.ImageLoader;

import com.app.appdata.Appdata;
import com.app.appdata.Constant;
import com.app.appdata.TouchImageView;
import com.example.soundhawaiiapp.R;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

public class AlbumAdapter extends PagerAdapter{
	
	Context context;
	ArrayList<String>arryalist;
	ImageLoader imgloader;
	public AlbumAdapter(Context context,ArrayList<String>arraylist){
		this.context=context;
		this.arryalist=arraylist;
		imgloader=new ImageLoader(context);
	}
	@Override
	public int getCount() {
		return arryalist.size();
	}

	@Override
	public boolean isViewFromObject(View container, Object arg1) {                             
		return container==((LinearLayout)arg1);
	}
	
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		
		LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view=inflater.inflate(R.layout.album_item, container,false);
		ImageView image=(ImageView)view.findViewById(R.id.imgdisplay);
		imgloader.DisplayImage(arryalist.get(position), image,ScaleType.CENTER_CROP);
		((ViewPager)container).addView(view);
		return view;
	}
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		((ViewPager)container).removeView((LinearLayout)object);
	}
}