package com.taxiuser.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.taxiuser.R;
import com.taxiuser.adapters.AdapterFAQ;
import com.taxiuser.databinding.ActivityFaqactBinding;
import com.taxiuser.models.FAQModel;
import com.taxiuser.utils.ProjectUtil;
import com.taxiuser.utils.retrofitutils.Api;
import com.taxiuser.utils.retrofitutils.ApiFactory;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FAQAct extends AppCompatActivity {

    Context mContext = FAQAct.this;
    ActivityFaqactBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_faqact);
        itit();
    }

    private void itit() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        getAllFAQInfo();

    }

    private void getAllFAQInfo() {

        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getAllFAQInformation();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {

                    String stringResponse = response.body().string();

                    try {

                        JSONObject jsonObject = new JSONObject(stringResponse);

                        if (jsonObject.getString("status").equals("1")) {
                            FAQModel accountModel = new Gson().fromJson(stringResponse, FAQModel.class);
                            AdapterFAQ adapterAccounts = new AdapterFAQ(mContext, accountModel.getResult());
                            binding.listOptions.setAdapter(adapterAccounts);
                        } else {
                            AdapterFAQ adapterAccounts = new AdapterFAQ(mContext, null);
                            binding.listOptions.setAdapter(adapterAccounts);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("JSONException", "JSONException = " + e.getMessage());
                    }

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

}