package com.taxiuser.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.gson.Gson;
import com.taxiuser.R;
import com.taxiuser.databinding.ActivityBusinessProfileBinding;
import com.taxiuser.models.ModelLogin;
import com.taxiuser.utils.AppConstant;
import com.taxiuser.utils.MyApplication;
import com.taxiuser.utils.ProjectUtil;
import com.taxiuser.utils.SharedPref;
import com.taxiuser.utils.retrofitutils.Api;
import com.taxiuser.utils.retrofitutils.ApiFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BusinessProfileAct extends AppCompatActivity {

    Context mContext = BusinessProfileAct.this;
    ActivityBusinessProfileBinding binding;
    SharedPref sharedPref;
    ModelLogin modelLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_business_profile);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        itit();
    }

    private void itit() {

        if (modelLogin.getResult().getBusiness_email() == null ||
                modelLogin.getResult().getBusiness_email().equals("")) {
            binding.etBusinessEmail.setText("");
        } else {
            binding.etBusinessEmail.setText(modelLogin.getResult().getBusiness_email());
        }

        binding.tvSave.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(binding.etBusinessEmail.getText().toString().trim())) {
                if (ProjectUtil.isValidEmail(binding.etBusinessEmail.getText().toString().trim())) {
                    businessProfileApi();
                } else {
                    MyApplication.showAlert(mContext, getString(R.string.invalid_email));
                }
            } else {
                businessProfileApi();
            }
        });

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.tvDeleteAccount.setOnClickListener(v -> {
            deleteAlert();
        });

        binding.etEmail.setText(modelLogin.getResult().getEmail());

    }

    private void businessProfileApi() {
        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));

        HashMap<String, String> paramHash = new HashMap<>();
        paramHash.put("business_email", binding.etBusinessEmail.getText().toString().trim());
        paramHash.put("user_id", modelLogin.getResult().getId());

        Log.e("asdfasdfasf", "paramHash = " + paramHash);

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.businessEmailUpdateApi(paramHash);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    Log.e("responseString", "responseString = " + responseString);

                    if (jsonObject.getString("status").equals("1")) {

                        modelLogin = new Gson().fromJson(responseString, ModelLogin.class);

                        sharedPref.setBooleanValue(AppConstant.IS_REGISTER, true);
                        sharedPref.setUserDetails(AppConstant.USER_DETAILS, modelLogin);

                        finish();

                    } else {
                        Toast.makeText(BusinessProfileAct.this, getString(R.string.invalid_credentials), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception", "Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
            }

        });
    }

    private void deleteAccount() {
        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));

        HashMap<String, String> param = new HashMap<>();
        param.put("user_id", modelLogin.getResult().getId());

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.deleteAccountApiCall(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String stringResponse = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(stringResponse);
                        if (jsonObject.getString("status").equals("1")) {
                            sharedPref.clearAllPreferences();
                            finishAffinity();
                            startActivity(new Intent(mContext, LoginAct.class));
                            MyApplication.showToast(mContext, getString(R.string.profile_delete_success));
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
            }
        });
    }

    private void deleteAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(getString(R.string.delete_profile_text))
                .setCancelable(false)
                .setPositiveButton(mContext.getString(R.string.yes)
                        , new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                deleteAccount();
                            }
                        }).setNegativeButton(mContext.getString(R.string.no)
                , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
    }

}