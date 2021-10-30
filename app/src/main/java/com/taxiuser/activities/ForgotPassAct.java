package com.taxiuser.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.taxiuser.R;
import com.taxiuser.databinding.ActivityForgotPassBinding;
import com.taxiuser.utils.InternetConnection;
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

public class ForgotPassAct extends AppCompatActivity {

    Context mContext = ForgotPassAct.this;
    ActivityForgotPassBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_pass);
        itit();
    }

    private void itit() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.btSubmit.setOnClickListener(v -> {
            if (TextUtils.isEmpty(binding.etEmail.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.enter_email_text), Toast.LENGTH_SHORT).show();
            } else {
                if (InternetConnection.checkConnection(mContext)) forgotPassApiCall();
                else
                    Toast.makeText(mContext, getString(R.string.check_internet_text), Toast.LENGTH_LONG).show();
            }
        });

    }

    private void forgotPassApiCall() {
        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));

        HashMap<String, String> paramHash = new HashMap<>();
        paramHash.put("email", binding.etEmail.getText().toString().trim());

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.forgotPass(paramHash);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String stringResponse = response.body().string();

                    try {

                        JSONObject jsonObject = new JSONObject(stringResponse);
                        Log.e("asfddasfasdf", "response = " + stringResponse);
                        if (jsonObject.getString("status").equals("1")) {

                            Log.e("asfddasfasdf", "response = " + response);
                            finish();
                            Toast.makeText(mContext, getString(R.string.reset_pass_msg), Toast.LENGTH_LONG).show();

                        } else {
                            Toast.makeText(mContext, getString(R.string.email_not_found), Toast.LENGTH_LONG).show();
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

}