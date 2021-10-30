package com.taxiuser.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.taxiuser.R;
import com.taxiuser.databinding.ItemRideHistoryBinding;

public class AdapterRideHistory extends RecyclerView.Adapter<AdapterRideHistory.MyRideHolder> {

    Context mContext;

    public AdapterRideHistory(Context mContext) {
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public MyRideHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRideHistoryBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext)
                , R.layout.item_ride_history, parent, false);
        return new MyRideHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyRideHolder holder, int position) {

        holder.binding.GoDetail.setOnClickListener(v -> {
           // mContext.startActivity(new Intent(mContext, RideDetailsAct.class));
        });

    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public class MyRideHolder extends RecyclerView.ViewHolder {

        ItemRideHistoryBinding binding;

        public MyRideHolder(@NonNull ItemRideHistoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }

}
