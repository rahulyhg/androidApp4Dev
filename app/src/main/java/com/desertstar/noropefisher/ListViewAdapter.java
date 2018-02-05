package com.desertstar.noropefisher;

/**
 * Created by Iker Redondo on 1/19/2018.
 */

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;


import static com.desertstar.noropefisher.Constants.FIRST_COLUMN;
import static com.desertstar.noropefisher.Constants.SECOND_COLUMN;
import static com.desertstar.noropefisher.Constants.THIRD_COLUMN;

public class ListViewAdapter extends BaseAdapter{
       //public static final int ITEM_VIEW_TYPE_IGNORE = -1;


    public ArrayList<HashMap<String, String>> list;
    Activity activity;
    TextView txtFirst;
    TextView txtSecond;
    TextView txtThird;

    //add all the attributes here


    public ListViewAdapter(Activity activity, ArrayList<HashMap<String, String>> list){
        super();
        this.activity=activity;
        this.list=list;
    }

    @Override
    public int getViewTypeCount(){

        //Log.d("getViewTypeCount", String.valueOf((getCount()>0) ? getCount() : 1));
        return (getCount()>0) ? getCount() : 1;

    }

    @Override
    public int getItemViewType(int position){
        //Log.d("getItemViewType", String.valueOf(position+1));

        return position;
    }


    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        //Log.d("getCount", String.valueOf(list.size()));

        return list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        //Log.d("getItem", String.valueOf(position));

        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub



        LayoutInflater inflater=activity.getLayoutInflater();

        if(convertView == null){

            convertView=inflater.inflate(R.layout.colmn_row, null);

            txtFirst=(TextView) convertView.findViewById(R.id.id);
            txtSecond=(TextView) convertView.findViewById(R.id.serial);
            txtThird=(TextView) convertView.findViewById(R.id.distance);

        }

        HashMap<String, String> map=list.get(position);
        txtFirst.setText(map.get(FIRST_COLUMN));
        txtSecond.setText(map.get(SECOND_COLUMN));
        txtThird.setText(map.get(THIRD_COLUMN));

        return convertView;
    }

}