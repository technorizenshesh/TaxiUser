package com.taxiuser.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.taxiuser.R;
import com.taxiuser.databinding.ItemServicessBinding;
import com.taxiuser.models.ModelCar;
import com.taxiuser.models.ServiceTypeModel;

import java.util.ArrayList;

public class AdapterType extends RecyclerView.Adapter<AdapterType.MyViewHolder> {
    Context context;
    ArrayList<ServiceTypeModel>arrayList;
    onServiceTypeListener listener;


    public interface onServiceTypeListener {
        void onServiceType(String name);
    }

    public AdapterType(Context context, ArrayList<ServiceTypeModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    public AdapterType Callback(onServiceTypeListener listener) {
        this.listener = listener;
        return this;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemServicessBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_servicess, parent, false);
        return new MyViewHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.binding.tvColorName.setText(arrayList.get(position).getName());
        if(arrayList.get(position).isChk()==true){
            holder.binding.LlMain.setBackground(context.getDrawable(R.drawable.rounded_select_yellow_bg));
        } else  holder.binding.LlMain.setBackground(context.getDrawable(R.drawable.rounded_unselect_white_bg));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ItemServicessBinding binding;
        public MyViewHolder(@NonNull ItemServicessBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;

            binding.layoutClick.setOnClickListener(v -> {
                for (int i =0;i<arrayList.size();i++){
                    arrayList.get(i).setChk(false);

                }
                arrayList.get(getAdapterPosition()).setChk(true);
                listener.onServiceType(arrayList.get(getAdapterPosition()).getName());
                notifyDataSetChanged();

            });

        }
    }
}
