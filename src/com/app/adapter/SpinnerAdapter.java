package com.app.adapter;

import com.app.appdata.Constant;

import com.example.soundhawaiiapp.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SpinnerAdapter extends ArrayAdapter<String>{
	
	LayoutInflater inflater;
//	public   String[] spinnerValues  = {
//			getResources().getString(R.string.set_sound_alarm),
//			getResources().getString(R.string.videos),
//			getResources().getString(R.string.hawaii_vocab),
//			getResources().getString(R.string.specialthankstitle),
//			getResources().getString(R.string.www),
//			getResources().getString(R.string.language) };
//	
	public SpinnerAdapter(Context context, int resource) {
		
		
		super(context, resource);
		inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public View getDropDownView(int position, View cnvtView, ViewGroup prnt) {
		return getCustomView(position, cnvtView, prnt);
	}

	@Override
	public View getView(int pos, View cnvtView, ViewGroup prnt) {
		return getCustomView(pos, cnvtView, prnt);
	}

	public View getCustomView(int position, View convertView,ViewGroup parent) {
		View mySpinner = inflater.inflate(R.layout.row, parent, false);
		TextView main_text = (TextView) mySpinner.findViewById(R.id.textView1);
		//main_text.setText(spinnerValues[position]);
		ImageView left_icon = (ImageView) mySpinner.findViewById(R.id.imageView1);
		left_icon.setImageResource(Constant.images[position]);
		return mySpinner;
	}
}