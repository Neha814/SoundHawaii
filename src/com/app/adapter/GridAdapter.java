package com.app.adapter;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import org.apache.commons.codec.binary.Base64;

import lazyadapter.ImageLoader;
import android.content.Context;
import android.graphics.Color;

import android.util.Log;
import android.util.Xml.Encoding;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.example.soundhawaiiapp.R;

public class GridAdapter extends BaseAdapter {
	private Context context;
	LayoutInflater inflater;
	View view;
	String language;
	ImageLoader imgloader;
	private ArrayList<HashMap<String, String>> arraylist;

	public GridAdapter(Context context,
			ArrayList<HashMap<String, String>> arraylist,String lang) {
		this.context = context;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.arraylist = arraylist;
		imgloader = new ImageLoader(context);
		language = lang;
	}

	@Override
	public int getCount() {
		System.out.println("arraylist size==" + arraylist.size());
		return arraylist.size();
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
		view = inflater.inflate(R.layout.grid_item, parent, false);
		ImageView image = (ImageView) view.findViewById(R.id.imageView1);
		TextView text = (TextView) view.findViewById(R.id.textView1);
		imgloader.DisplayImage(arraylist.get(position).get("thumbImage"),
				image, ScaleType.FIT_XY);
		System.out.println("lanuga===" + Locale.getDefault().getLanguage());
		if(language.equalsIgnoreCase("en")){
		 text.setText(arraylist.get(position).get("thumbName"));
		} else if(language.equalsIgnoreCase("ja")){
			 text.setText(arraylist.get(position).get("thumbNameJap"));	

		}
		
		if (position == 0) {
			text.setTextColor(Color.parseColor("#4e82e1"));
//			text.setText(context.getResources()
//					.getString(R.string.hawaii_waves));
		} else if (position == 1) {
			text.setTextColor(Color.parseColor("#00cb00"));
//			text.setText(context.getResources()
//					.getString(R.string.manao_falls));
		}

		else if (position == 2) {
			text.setTextColor(Color.parseColor("#039FC2"));
//			text.setText(context.getResources()
//					.getString(R.string.underwater));
		} else if (position == 3) {
			text.setTextColor(Color.parseColor("#938989"));
//			text.setText(context.getResources()
//					.getString(R.string.hawaii_birds));
		} else if (position == 4) {
			text.setTextColor(Color.WHITE);
//			text.setText(context.getResources()
//					.getString(R.string.hawaii_rain));
		} else if (position == 5) {
			text.setTextColor(Color.parseColor("#FB3701"));
//			text.setText(context.getResources()
//					.getString(R.string.lava_flow));
		} else if (position == 6) {
			text.setTextColor(Color.parseColor("#FD8F20"));
//			text.setText(context.getResources()
//					.getString(R.string.conch_shell));
		} else if (position == 7) {
			text.setTextColor(Color.parseColor("#DCDBDA"));
//			text.setText(context.getResources()
//					.getString(R.string.dolphins));
		} else if (position == 8) {
			text.setTextColor(Color.parseColor("#FFF8A3"));
//			text.setText(context.getResources()
//					.getString(R.string.walking_beach));
		} else if (position == 9) {
			text.setTextColor(Color.parseColor("#B0A227"));
//			text.setText(context.getResources()
//					.getString(R.string.ukulele));
		} else {
			text.setTextColor(Color.WHITE);
//			text.setText(context.getResources()
//					.getString(R.string.hawaii_waves));
//			text.setText("");
		}
		return view;
	}

}