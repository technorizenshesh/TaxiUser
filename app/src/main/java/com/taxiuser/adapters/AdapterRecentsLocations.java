package com.taxiuser.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.taxiuser.R;
import com.taxiuser.models.ModelRecentLocations;

import java.util.ArrayList;

public class AdapterRecentsLocations extends BaseAdapter {

    Context mContext;
    ArrayList<ModelRecentLocations.Result> locList;

    public AdapterRecentsLocations(Context mContext, ArrayList<ModelRecentLocations.Result> locList) {
        this.mContext = mContext;
        this.locList = locList;
    }

    @Override
    public int getCount() {
        if(locList == null) {
            return 0;
        } else {
            return locList.size();
        }
    }

    @Override
    public Object getItem(int position) {
        return locList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.recent_loc_adapter,parent,false);
        ModelRecentLocations.Result data = locList.get(position);

        TextView tvAddress = view.findViewById(R.id.tvAddress);
        tvAddress.setText(data.getAddress());

        return view;

    }

}
