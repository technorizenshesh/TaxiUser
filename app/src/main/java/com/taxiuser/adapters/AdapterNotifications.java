package com.taxiuser.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.taxiuser.R;
import com.taxiuser.databinding.AdapterNotificationBinding;
import com.taxiuser.models.ModelNotification;
import java.util.ArrayList;

public class AdapterNotifications extends RecyclerView.Adapter<AdapterNotifications.MyRideHolder> {

    Context mContext;
    ArrayList<ModelNotification.Result> arrayList;

    public AdapterNotifications(Context context, ArrayList<ModelNotification.Result> arrayList) {
        this.mContext = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public AdapterNotifications.MyRideHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterNotificationBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext)
                , R.layout.adapter_notification, parent, false);
        return new AdapterNotifications.MyRideHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterNotifications.MyRideHolder holder, int position) {
        holder.binding.setData(arrayList.get(position));
    }


    @Override
    public int getItemCount() {
        return arrayList == null ? 0 : arrayList.size();
    }

    public class MyRideHolder extends RecyclerView.ViewHolder {

        AdapterNotificationBinding binding;

        public MyRideHolder(@NonNull AdapterNotificationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }

}
