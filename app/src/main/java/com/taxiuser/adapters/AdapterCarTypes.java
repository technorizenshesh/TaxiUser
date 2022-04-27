package com.taxiuser.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.taxiuser.R;
import com.taxiuser.databinding.ItemRideBookBinding;
import com.taxiuser.models.ModelCar;
import com.taxiuser.utils.AppConstant;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class AdapterCarTypes extends RecyclerView.Adapter<AdapterCarTypes.MyRideHolder> {

    Context mContext;
    ArrayList<ModelCar> arrayList;
    private onCarSelectListener listener;

    public interface onCarSelectListener {
        void onCarSelected(ModelCar car, String name);
    }

    public AdapterCarTypes(Context context, ArrayList<ModelCar> arrayList) {
        this.mContext = context;
        this.arrayList = arrayList;
    }

    public AdapterCarTypes Callback(onCarSelectListener listener) {
        this.listener = listener;
        return this;
    }

    @NonNull
    @Override
    public AdapterCarTypes.MyRideHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRideBookBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext)
                , R.layout.item_ride_book, parent, false);
        return new AdapterCarTypes.MyRideHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterCarTypes.MyRideHolder holder, int position) {
        holder.binding.setCar(arrayList.get(position));
        holder.binding.executePendingBindings();

        Log.e("fasfasdas","AppConstant.CURRENCY = " + AppConstant.CURRENCY);
        Log.e("fasfasdas","AppConstant.CURRENT_CURRENCY_VALUE = " + AppConstant.CURRENT_CURRENCY_VALUE);
        Log.e("fasfasdas","Double.parseDouble(arrayList.get(position).getTotal() = " + Double.parseDouble(arrayList.get(position).getTotal()));
        Log.e("fasfasdas","Multiply = " + (Double.parseDouble(arrayList.get(position).getTotal()) * AppConstant.CURRENT_CURRENCY_VALUE));

        double finalValue = (Double.parseDouble(arrayList.get(position).getTotal()) * AppConstant.CURRENT_CURRENCY_VALUE);
        DecimalFormat f = new DecimalFormat("##.00");

        holder.binding.tvTotal.setText(AppConstant.CURRENCY + " " + String.format("%.2f",finalValue));

        holder.binding.getRoot().setOnClickListener(v -> {
            for (int i = 0; i < arrayList.size(); i++) {
                arrayList.get(i).setSelected(false);
            }
            arrayList.get(position).setSelected(true);
            listener.onCarSelected(arrayList.get(position), "basic_car");
            notifyDataSetChanged();
        });

    }

    @Override
    public int getItemCount() {
        return arrayList == null ? 0 : arrayList.size();
    }

    public class MyRideHolder extends RecyclerView.ViewHolder {

        ItemRideBookBinding binding;

        public MyRideHolder(@NonNull ItemRideBookBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }

}
