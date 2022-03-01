package com.taxiuser.activities;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.load.model.Model;
import com.google.gson.Gson;
import com.taxiuser.R;
import com.taxiuser.adapters.AdapterActiveBooking;
import com.taxiuser.adapters.AdapterNotifications;
import com.taxiuser.databinding.ActivityNotificationBinding;
import com.taxiuser.models.ModelActiveBooking;
import com.taxiuser.models.ModelNotification;
import com.taxiuser.utils.ProjectUtil;
import com.taxiuser.utils.retrofitutils.Api;
import com.taxiuser.utils.retrofitutils.ApiFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationAct extends AppCompatActivity {

    Context mContext = NotificationAct.this;
    ActivityNotificationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notification);
        itit();
    }

    private void itit() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.swipLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllNotification();
            }
        });

        getAllNotification();

    }

    private void getAllNotification() {
        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getNotificationAdmin();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);
                try {
                    String stringResponse = response.body().string();

                    try {

                        JSONObject jsonObject = new JSONObject(stringResponse);

                        if (jsonObject.getString("status").equals("1")) {

                            Log.e("asfddasfasdf", "response = " + response);
                            Log.e("asfddasfasdf", "stringResponse = " + stringResponse);

                            ModelNotification modelHistory = new Gson().fromJson(stringResponse, ModelNotification.class);
                            AdapterNotifications adapterRideHistory = new AdapterNotifications(mContext, modelHistory.getResult());
                            binding.rvNotification.setAdapter(adapterRideHistory);

                        } else {
                            AdapterNotifications adapterRideHistory = new AdapterNotifications(mContext, null);
                            binding.rvNotification.setAdapter(adapterRideHistory);
                            Toast.makeText(mContext, getString(R.string.no_notfi_found), Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);
            }

        });

    }

}