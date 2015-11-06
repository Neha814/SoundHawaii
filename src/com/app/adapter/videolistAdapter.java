package com.app.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import lazyadapter.ImageLoader;

import com.app.appdata.Appdata;
import com.example.soundhawaiiapp.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

public class videolistAdapter extends BaseAdapter{
	ImageLoader imgloader;
	private Context context;
	private LayoutInflater inflater;
	private View view;
	private ArrayList<HashMap<String, String>>arryalist;
	public videolistAdapter(Context context,ArrayList<HashMap<String, String>>arraylist) {
		imgloader=new ImageLoader(context);
		this.context=context;
		this.arryalist=arraylist;
		inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return arryalist.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		view=inflater.inflate(R.layout.video_list_item, parent,false);
		ImageView image=(ImageView)view.findViewById(R.id.imageView1);
		TextView text=(TextView)view.findViewById(R.id.textView1);
		
		text.setText(arryalist.get(position).get("videoName"));
		imgloader.DisplayImage(arryalist.get(position).get("videoImage"), image,ScaleType.FIT_XY);
		return view;
	}
}