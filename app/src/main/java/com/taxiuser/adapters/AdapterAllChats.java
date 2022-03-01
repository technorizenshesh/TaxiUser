package com.taxiuser.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.taxiuser.R;
import com.taxiuser.databinding.AdapterChatingBinding;
import com.taxiuser.models.ModelChating;
import com.taxiuser.models.ModelLogin;
import com.taxiuser.utils.AppConstant;
import com.taxiuser.utils.BottomReachedInterface;
import com.taxiuser.utils.SharedPref;

import java.util.ArrayList;

public class AdapterAllChats extends RecyclerView.Adapter<AdapterAllChats.AdapterAllChatsViewHolder> {

    Context mContext;
    ArrayList<ModelChating.Result> modelChatList;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    BottomReachedInterface bottomReachedInterface;

    public AdapterAllChats(Context mContext, ArrayList<ModelChating.Result> modelChatList) {
        this.mContext = mContext;
        this.modelChatList = modelChatList;
        this.bottomReachedInterface = (BottomReachedInterface) mContext;
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
    }

    @NonNull
    @Override
    public AdapterAllChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterChatingBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(mContext), R.layout.adapter_chating,parent,false);
        return new AdapterAllChatsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterAllChatsViewHolder holder, int position) {

        ModelChating.Result data = modelChatList.get(position);

        if (modelChatList.size() - 1 == position) {
            bottomReachedInterface.isBottomReached(true);
        } else {
            bottomReachedInterface.isBottomReached(false);
        }

        if (modelLogin.getResult().getId().equals(data.getSender_id())) {
            holder.binding.llRightChat.setVisibility(View.VISIBLE);
            holder.binding.llLeftChat.setVisibility(View.GONE);
            Log.e("sdgdgsdgdf", "getSender_image = " + data.getSender_detail().getSender_image());
            holder.binding.tvRightChat.setText(data.getChat_message());
            holder.binding.tvRightDate.setText(data.getDate_time());
        } else {
            holder.binding.llLeftChat.setVisibility(View.VISIBLE);
            holder.binding.llRightChat.setVisibility(View.GONE);
            Log.e("sdgdgsdgdf", "getReceiver_detail = " + data.getReceiver_detail().getReceiver_image());
            holder.binding.tvLeftChat.setText(data.getChat_message());
            holder.binding.tvLeftDate.setText(data.getDate_time());
        }

    }

    @Override
    public int getItemCount() {
        return modelChatList == null ? 0 : modelChatList.size();
    }

    public class AdapterAllChatsViewHolder extends RecyclerView.ViewHolder {

        AdapterChatingBinding binding;

        public AdapterAllChatsViewHolder(AdapterChatingBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }


}

