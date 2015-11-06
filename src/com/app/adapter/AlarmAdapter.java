package com.app.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.soundhawaiiapp.R;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AlarmAdapter extends BaseAdapter{

	private Context context;
	private ArrayList<HashMap<String, String>>arraylist;
	String time[];
	Integer pos[];
	
	public AlarmAdapter(Context context,ArrayList<HashMap<String, String>>arraylist,String[]time,Integer pos[]){
		this.context=context;
		this.arraylist=arraylist;
		this.time=time;
		this.pos=pos;
	}
	@Override
	public int getCount() {
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
		View view=convertView;
		if(view==null){
			LayoutInflater inflater=LayoutInflater.from(context);
			view=inflater.inflate(R.layout.alarm_item, parent,false);
		}
			TextView text=(TextView)view.findViewById(R.id.textView1);
			TextView txttime=(TextView)view.findViewById(R.id.textView2);
			ImageView image=(ImageView)view.findViewById(R.id.imageView1);
		
		text.setText(arraylist.get(position).get("title"));
		
		
		
	//System.out.println("pos ar: "+pos[position]);
	//txttime.setText(time[position]);
		/*if(pos[position]!=null){
		if(position==pos[position]){
			txttime.setText(""+time[position]);
		}
		else{
			//txttime.setText(""+time[pos[position]]);
			System.out.println("inside the else");
		}
		}
		else{
			if(pos[position]!=null){
			System.out.println(time[position]);
			}
		}*/
		
		return view;
	}
	
	class viewholder{
		
	}

}
