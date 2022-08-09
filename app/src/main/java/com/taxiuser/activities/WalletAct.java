package com.taxiuser.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.gson.Gson;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.Stripe;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.taxiuser.R;
import com.taxiuser.adapters.AdapterTransactions;
import com.taxiuser.databinding.ActivityWalletBinding;
import com.taxiuser.databinding.AddMoneyDialogBinding;
import com.taxiuser.databinding.SendMoneyDialogBinding;
import com.taxiuser.models.ModelLogin;
import com.taxiuser.models.ModelTransactions;
import com.taxiuser.utils.AppConstant;
import com.taxiuser.utils.MyApplication;
import com.taxiuser.utils.ProjectUtil;
import com.taxiuser.utils.SharedPref;
import com.taxiuser.utils.retrofitutils.Api;
import com.taxiuser.utils.retrofitutils.ApiFactory;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WalletAct extends AppCompatActivity {

    Context mContext = WalletAct.this;
    ActivityWalletBinding binding;
    private double walletTmpAmt;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    double walletAmountWithCurrencyChange = 0.0;
    SendMoneyDialogBinding dialogBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_wallet);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        itit();
        getNewProfile2();
    }

    private void itit() {

        getAllTransactionsApi();

        binding.swipLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllTransactionsApi();
            }
        });

        binding.cvAddMoney.setOnClickListener(v -> {
            addMoneyDialog();
        });

        binding.cvTransfer.setOnClickListener(v -> {
            tranferMOneyDialog();
        });

    }

    private void getAllTransactionsApi() {

        HashMap<String, String> paramHash = new HashMap<>();
        paramHash.put("user_id", modelLogin.getResult().getId());

        Log.e("paramHashparamHash", "paramHash = " + paramHash);

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getTransactionApiCall(paramHash);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);
                try {

                    String stringResponse = response.body().string();

                    try {

                        JSONObject jsonObject = new JSONObject(stringResponse);

                        Log.e("sendMoneyAPiCall", "sendMoneyAPiCall = " + stringResponse);

                        if (jsonObject.getString("status").equals("1")) {
                            Log.e("sendMoneyAPiCall", "sendMoneyAPiCall = " + stringResponse);
                            ModelTransactions modelTransactions = new Gson().fromJson(stringResponse, ModelTransactions.class);
                            AdapterTransactions adapterTransactions = new AdapterTransactions(mContext, modelTransactions.getResult());
                            binding.rvTransaction.setAdapter(adapterTransactions);
                        } else {
                            AdapterTransactions adapterTransactions = new AdapterTransactions(mContext, null);
                            binding.rvTransaction.setAdapter(adapterTransactions);
                            Toast.makeText(mContext, jsonObject.getString("result"), Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
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
                binding.swipLayout.setRefreshing(false);
            }

        });

    }

    private void sendMoneyAPiCall(Dialog dialog, String email, String amount, String type) {

        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));

        HashMap<String, String> paramHash = new HashMap<>();
        paramHash.put("user_id", modelLogin.getResult().getId());
        paramHash.put("amount", amount);
        paramHash.put("email", email);
        paramHash.put("type", type);

        Log.e("paramHashparamHash", "paramHash = " + paramHash);

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.walletTransferApiCall(paramHash);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {

                    String stringResponse = response.body().string();

                    try {

                        JSONObject jsonObject = new JSONObject(stringResponse);

                        Log.e("sendMoneyAPiCall", "sendMoneyAPiCall = " + stringResponse);

                        if (jsonObject.getString("status").equals("1")) {
                            Log.e("sendMoneyAPiCall", "sendMoneyAPiCall = " + stringResponse);
                            getAllTransactionsApi();
                            getProfileApiCall();
                            dialog.dismiss();
                        } else {
                            MyApplication.showAlert(mContext, jsonObject.getString("result"));
                        }

                    } catch (JSONException e) {
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

    @Override
    protected void onResume() {
        super.onResume();
        getProfileApiCall();
    }

    private void addWalletAmountApi(String amount, Dialog dialog, String token) {

        HashMap<String, String> map = new HashMap<>();
        map.put("user_id", modelLogin.getResult().getId());
        map.put("amount", amount);
        map.put("token", token);
        map.put("currency", "AUD");

        Log.e("AcceptCancel", "AcceptCancel = " + map);

        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.addWalletAmount(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String stringResponse = response.body().string();

                    Log.e("asdfasdfasdfas", "stringResponse = " + stringResponse);

                    JSONObject jsonObject = new JSONObject(stringResponse);
                    if (jsonObject.getString("status").equals("1")) {
                        dialog.dismiss();
                        getAllTransactionsApi();
                        getProfileApiCall();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
                Log.e("sfasfsdfdsf", "Exception = " + t.getMessage());
            }
        });

    }

    private void getProfileApiCall() {
        // ProjectUtil.showProgressDialog(mContext,false,getString(R.string.please_wait));

        HashMap<String, String> paramHash = new HashMap<>();
        paramHash.put("user_id", modelLogin.getResult().getId());

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getProfileCall(paramHash);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {

                    String stringResponse = response.body().string();

                    try {

                        JSONObject jsonObject = new JSONObject(stringResponse);

                        if (jsonObject.getString("status").equals("1")) {

                            Log.e("getProfileApiCall", "getProfileApiCall = " + stringResponse);

                            modelLogin = new Gson().fromJson(stringResponse, ModelLogin.class);

                            double amount = Double.parseDouble(modelLogin.getResult().getWallet()) * AppConstant.CURRENT_CURRENCY_VALUE;
                            binding.tvWalletAmount.setText(AppConstant.CURRENCY + " " + String.format("%.2f", amount));

                        }

                    } catch (JSONException e) {
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

    private void getNewProfile2() {

        ProjectUtil.showProgressDialog(mContext,false,getString(R.string.please_wait));

        HashMap<String, String> paramHash = new HashMap<>();
        paramHash.put("user_id", modelLogin.getResult().getId());

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getProfileCall(paramHash);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {

                    String stringResponse = response.body().string();

                    try {

                        JSONObject jsonObject = new JSONObject(stringResponse);

                        if (jsonObject.getString("status").equals("1")) {

                            Log.e("getProfileApiCall", "getProfileApiCall = " + stringResponse);

                            Log.e("getProfileApiCall", "modelLogin.getResult().getWallet() = " + modelLogin.getResult().getWallet());
                            Log.e("getProfileApiCall", "AppConstant.CURRENT_CURRENCY_VALUE = " + AppConstant.CURRENT_CURRENCY_VALUE);

                            modelLogin = new Gson().fromJson(stringResponse, ModelLogin.class);
                            sharedPref.setUserDetails(AppConstant.USER_DETAILS, modelLogin);

                            try {
                                double walletAmt = Double.parseDouble(modelLogin.getResult().getWallet()) * AppConstant.CURRENT_CURRENCY_VALUE;
                                binding.tvWalletAmount.setText(AppConstant.CURRENCY + " " + String.format("%.2f",walletAmt));
                            } catch (Exception e) {
                            }

                        }

                    } catch (JSONException e) {
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

    private void addMoneyDialog() {

        Dialog dialog = new Dialog(mContext, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        AddMoneyDialogBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                R.layout.add_money_dialog, null, false);
        dialog.setContentView(dialogBinding.getRoot());

        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP)
                    dialog.dismiss();
                return false;
            }
        });

        dialogBinding.tvCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialogBinding.ivMinus.setOnClickListener(v -> {
            if (!(dialogBinding.etMoney.getText().toString().trim().equals("") || dialogBinding.etMoney.getText().toString().trim().equals("0"))) {
                walletTmpAmt = Double.parseDouble(dialogBinding.etMoney.getText().toString().trim()) - 1;
                dialogBinding.etMoney.setText(String.valueOf(walletTmpAmt));
            }
        });

        dialogBinding.ivPlus.setOnClickListener(v -> {
            if (TextUtils.isEmpty(dialogBinding.etMoney.getText().toString().trim())) {
                dialogBinding.etMoney.setText("0");
                walletTmpAmt = Double.parseDouble(dialogBinding.etMoney.getText().toString().trim()) + 1;
                dialogBinding.etMoney.setText(String.valueOf(walletTmpAmt));
            } else {
                walletTmpAmt = Double.parseDouble(dialogBinding.etMoney.getText().toString().trim()) + 1;
                dialogBinding.etMoney.setText(String.valueOf(walletTmpAmt));
            }
        });

        dialogBinding.tv699.setOnClickListener(v -> {
            dialogBinding.etMoney.setText("699");
            walletTmpAmt = Double.parseDouble(dialogBinding.etMoney.getText().toString().trim());
            dialogBinding.etMoney.setText(String.valueOf(walletTmpAmt));
        });

        dialogBinding.tv799.setOnClickListener(v -> {
            dialogBinding.etMoney.setText("799");
            walletTmpAmt = Double.parseDouble(dialogBinding.etMoney.getText().toString().trim());
            dialogBinding.etMoney.setText(String.valueOf(walletTmpAmt));
        });

        dialogBinding.tv899.setOnClickListener(v -> {
            dialogBinding.etMoney.setText("899");
            walletTmpAmt = Double.parseDouble(dialogBinding.etMoney.getText().toString().trim());
            dialogBinding.etMoney.setText(String.valueOf(walletTmpAmt));
        });

        dialogBinding.btDone.setOnClickListener(v -> {
            walletTmpAmt = Double.parseDouble(dialogBinding.etMoney.getText().toString().trim());
            if (TextUtils.isEmpty(dialogBinding.etMoney.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_enter_amount), Toast.LENGTH_SHORT).show();
            } else if (walletTmpAmt == 0.0) {
                Toast.makeText(mContext, getString(R.string.enter_valid_amount), Toast.LENGTH_SHORT).show();
            } else {
                if (modelLogin.getResult().getCard_number() == null ||
                        modelLogin.getResult().getCard_number().equals("")) {
                    startActivity(new Intent(mContext, CardDetailsAct.class));
                    Toast.makeText(mContext, getString(R.string.please_add_card), Toast.LENGTH_LONG).show();
                } else {
                    Card.Builder card = new Card.Builder(modelLogin.getResult().getCard_number(),
                            Integer.valueOf(modelLogin.getResult().getExpiry_month()),
                            Integer.valueOf(modelLogin.getResult().getExpiry_year().substring(2)),
                            modelLogin.getResult().getCvc_code());

                    Stripe stripe = new Stripe(mContext, AppConstant.STRIPE_TEST_KEY);

                    ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));
                    stripe.createCardToken(
                            card.build(), new ApiResultCallback<Token>() {
                                @Override
                                public void onSuccess(Token token) {
                                    ProjectUtil.pauseProgressDialog();
                                    walletAmountWithCurrencyChange = walletTmpAmt;
                                    changeToAUD(AppConstant.CURRENCY, dialog, token.getId());
                                    // addWalletAmountApi(String.valueOf(walletTmpAmt), dialog, token.getId());
                                    Log.e("asfadasdasd", "tokentokentoken = " + token.getId());
                                }

                                @Override
                                public void onError(@NotNull Exception e) {
                                    ProjectUtil.pauseProgressDialog();
                                }
                            });
                }
            }
        });

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        dialog.getWindow().setBackgroundDrawableResource(R.color.translucent_black);

        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        dialog.show();

    }

    private void changeToAUD(String base, Dialog dialog, String token) {
        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));

        Log.e("changeToAUD", "URL = " + "https://api.exchangeratesapi.io/v1/latest?access_key="
                + AppConstant.CURRENCY_KEY + "&base=" + base + "&symbols=AUD");

        AndroidNetworking.get("https://api.exchangeratesapi.io/v1/latest?access_key="
                + AppConstant.CURRENCY_KEY + "&base=" + base + "&symbols=AUD")
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        ProjectUtil.pauseProgressDialog();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject rates = jsonObject.getJSONObject("rates");
                            double currencyAmount = rates.getDouble("AUD");

                            walletAmountWithCurrencyChange = walletAmountWithCurrencyChange * currencyAmount;

                            if (AppConstant.CURRENCY.equals("PKR")) {
                                if (walletTmpAmt < 100.0) {
                                    MyApplication.showAlert(mContext, getString(R.string.need_to_add_100_pkr));
                                } else {
                                    addWalletAmountApi(String.format("%.2f", walletAmountWithCurrencyChange), dialog, token);
                                }
                            } else {
                                addWalletAmountApi(String.format("%.2f", walletAmountWithCurrencyChange), dialog, token);
                            }

                            Log.e("changeToAUD", "currencyAmount = " + currencyAmount);
                            Log.e("changeToAUD", "currencyAmountRoundOFF = " + String.format("%.2f", walletAmountWithCurrencyChange));
                            Log.e("changeToAUD", "walletAmountWithCurrencyChange = " + walletAmountWithCurrencyChange);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        ProjectUtil.pauseProgressDialog();
                    }
                });
    }

    private void tranferMOneyDialog() {

        Dialog dialog = new Dialog(mContext, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                R.layout.send_money_dialog, null, false);

        dialog.setContentView(dialogBinding.getRoot());

        dialogBinding.tvCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialogBinding.btDone.setOnClickListener(v -> {

            if (TextUtils.isEmpty(dialogBinding.etEmail.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.enter_email), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(dialogBinding.etEnterAmount.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_enter_amount), Toast.LENGTH_SHORT).show();
            } else if (!ProjectUtil.isValidEmail(dialogBinding.etEmail.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.invalid_email), Toast.LENGTH_SHORT).show();
            } else {
                walletTmpAmt = Double.parseDouble(dialogBinding.etEnterAmount.getText().toString().trim());
                walletAmountWithCurrencyChange = walletTmpAmt;
                changeToAUDTransfer(dialog, dialogBinding.etEmail.getText().toString().trim()
                        , dialogBinding.etEnterAmount.getText().toString().trim(), "USER", AppConstant.CURRENCY);
            }

        });

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        dialog.getWindow().setBackgroundDrawableResource(R.color.translucent_black);

        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        dialog.show();

    }

    private void changeToAUDTransfer(Dialog dialog, String email, String amount, String type, String base) {
        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));

        Log.e("changeToAUD", "URL = " + "https://api.exchangeratesapi.io/v1/latest?access_key="
                + AppConstant.CURRENCY_KEY + "&base=" + base + "&symbols=AUD");

        AndroidNetworking.get("https://api.exchangeratesapi.io/v1/latest?access_key="
                + AppConstant.CURRENCY_KEY + "&base=" + base + "&symbols=AUD")
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        ProjectUtil.pauseProgressDialog();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject rates = jsonObject.getJSONObject("rates");
                            double currencyAmount = rates.getDouble("AUD");

                            walletAmountWithCurrencyChange = walletAmountWithCurrencyChange * currencyAmount;

                            if (AppConstant.CURRENCY.equals("PKR")) {
                                if (walletTmpAmt < 100.0) {
                                    MyApplication.showAlert(mContext, getString(R.string.need_to_send_100_pkr));
                                } else {
                                    if (dialogBinding.rbUser.isChecked()) {
                                        sendMoneyAPiCall(dialog, email, String.valueOf(walletAmountWithCurrencyChange), "USER");
                                    } else if (dialogBinding.rbDriver.isChecked()) {
                                        sendMoneyAPiCall(dialog, email, String.valueOf(walletAmountWithCurrencyChange), "DRIVER");
                                    } else {
                                        MyApplication.showAlert(mContext, getString(R.string.select_user_driver_text));
                                    }
                                }
                            } else {
                                if (dialogBinding.rbUser.isChecked()) {
                                    sendMoneyAPiCall(dialog, email, dialogBinding.etEnterAmount.getText().toString().trim(), "USER");
                                } else if (dialogBinding.rbDriver.isChecked()) {
                                    sendMoneyAPiCall(dialog, dialogBinding.etEmail.getText().toString().trim()
                                            , dialogBinding.etEnterAmount.getText().toString().trim(), "DRIVER");
                                } else {
                                    MyApplication.showAlert(mContext, getString(R.string.select_user_driver_text));
                                }
                            }

                            Log.e("changeToAUD", "currencyAmount = " + currencyAmount);
                            Log.e("changeToAUD", "currencyAmountRoundOFF = " + String.format("%.2f", walletAmountWithCurrencyChange));
                            Log.e("changeToAUD", "walletAmountWithCurrencyChange = " + walletAmountWithCurrencyChange);
                        } catch (Exception e) {
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        ProjectUtil.pauseProgressDialog();
                    }
                });
    }

}