package com.taxiuser.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.taxiuser.R;
import com.taxiuser.databinding.ItemServicessBinding;
import com.taxiuser.models.ModelCar;
import com.taxiuser.models.ModelServicess;

import java.util.ArrayList;

public class AdapterService extends RecyclerView.Adapter<AdapterService.MyViewHolder> {
    Context context;
    ArrayList<ModelServicess.Result> arrayList;
    onCarSelectListener listener;
    ModelCar carrr;

    public interface onCarSelectListener {
        void onCarSelected(ModelCar car, String name);
    }


    public AdapterService(Context context, ArrayList<ModelServicess.Result> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    public AdapterService Callback(onCarSelectListener listener) {
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
        holder.binding.tvColorName.setText(arrayList.get(position).getCarName());
        if(arrayList.get(position).isSelected()==true){
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
/*
                if(arrayList.get(getAdapterPosition()).isChk()==false){
                    arrayList.get(getAdapterPosition()).setChk(true);
                    listener.onPosition(getAdapterPosition(),"true");
                    notifyItemChanged(getAdapterPosition());
                }
*/
/*
                else if(arrayList.get(getAdapterPosition()).isChk()==true){
                    arrayList.get(getAdapterPosition()).setChk(false);
                    listener.onPosition(getAdapterPosition(),"false");
                    notifyItemChanged(getAdapterPosition());
                }
*/

                for (int i =0;i<arrayList.size();i++){
                    arrayList.get(i).setSelected(false);

                }
                arrayList.get(getAdapterPosition()).setSelected(true);
                listener.onCarSelected(carrr, arrayList.get(getAdapterPosition()).getId());
                notifyDataSetChanged();

            });



        }
    }
}