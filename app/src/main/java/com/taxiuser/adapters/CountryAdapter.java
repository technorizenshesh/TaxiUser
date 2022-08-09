package com.taxiuser.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.taxiuser.R;
import com.taxiuser.models.CountryModel;

import java.util.ArrayList;

public class CountryAdapter extends BaseAdapter {
    Context context;
    ArrayList<CountryModel.Result> countryList;
    LayoutInflater inflater;


    public CountryAdapter(Context context, ArrayList<CountryModel.Result> countryList) {
        this.context = context;
        this.countryList = countryList;

        inflater = (LayoutInflater.from(context));

    }

    @Override
    public int getCount() {
        return countryList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.item_spinner, null);
        TextView names = convertView.findViewById(R.id.item);
        RelativeLayout mainView = convertView.findViewById(R.id.mainView);
        names.setText(countryList.get(position).getName());


      /*  mainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickPositionListener.clickPos(position,status);
            }
        });*/

        return convertView;
    }
}