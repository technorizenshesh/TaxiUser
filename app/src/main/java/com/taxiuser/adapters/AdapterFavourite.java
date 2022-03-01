package com.taxiuser.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.taxiuser.R;
import com.taxiuser.databinding.AdapterFavoriteDriverBinding;
import com.taxiuser.models.ModelFavourite;
import com.taxiuser.models.ModelLogin;
import com.taxiuser.utils.AppConstant;
import com.taxiuser.utils.ProjectUtil;
import com.taxiuser.utils.SharedPref;
import com.taxiuser.utils.retrofitutils.Api;
import com.taxiuser.utils.retrofitutils.ApiFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterFavourite extends RecyclerView.Adapter<AdapterFavourite.MyRideHolder> {

    Context mContext;
    ArrayList<ModelFavourite.Result> arrayList;
    SharedPref sharedPref;
    ModelLogin modelLogin;

    public AdapterFavourite(Context context, ArrayList<ModelFavourite.Result> arrayList) {
        this.mContext = context;
        this.arrayList = arrayList;
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
    }

    @NonNull
    @Override
    public AdapterFavourite.MyRideHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterFavoriteDriverBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext)
                , R.layout.adapter_favorite_driver, parent, false);
        return new AdapterFavourite.MyRideHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterFavourite.MyRideHolder holder, int position) {
        ModelFavourite.Result data = arrayList.get(position);

        Glide.with(mContext).load(data.getImage())
                .error(R.drawable.user_ic)
                .placeholder(R.drawable.user_ic)
                .into(holder.binding.ivUserPic);

        if (data.getFavorite_driver_status().equals("False")) {
            holder.binding.ivLikeUnlike.setImageResource(R.drawable.ic_unlike);
        } else {
            holder.binding.ivLikeUnlike.setImageResource(R.drawable.ic_like);
        }

        holder.binding.ivLikeUnlike.setOnClickListener(v -> {
            if (data.getFavorite_driver_status().equals("False")) {
                setLikeUnlike("True", data.getId(), position);
            } else {
                setLikeUnlike("False", data.getId(), position);
            }
        });

        holder.binding.tvName.setText(data.getFirst_name() + " " + data.getLast_name());
        holder.binding.rbRating.setRating(Float.parseFloat(data.getUser_review_rating()));

    }

    private void setLikeUnlike(String status, String driverId, int position) {
        ProjectUtil.showProgressDialog(mContext, false, mContext.getString(R.string.please_wait));

        HashMap<String, String> paramHash = new HashMap<>();
        paramHash.put("user_id", modelLogin.getResult().getId());
        paramHash.put("driver_id", driverId);

        Log.e("setLikeUnlike", "paramHash = " + paramHash);
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.favouriteDriverApiCall(paramHash);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String stringResponse = response.body().string();

                    try {
                        JSONObject jsonObject = new JSONObject(stringResponse);
                        Log.e("setLikeUnlike", "response = " + stringResponse);
                        if (jsonObject.getString("status").equals("1")) {
                            arrayList.get(position).setFavorite_driver_status(status);
                            notifyDataSetChanged();
                        } else {
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
            }

        });
    }

    @Override
    public int getItemCount() {
        return arrayList == null ? 0 : arrayList.size();
    }

    public class MyRideHolder extends RecyclerView.ViewHolder {

        AdapterFavoriteDriverBinding binding;

        public MyRideHolder(@NonNull AdapterFavoriteDriverBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }

}
