package com.taxiuser.activities;

import android.app.AlertDialog;
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
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.stripe.android.ApiResultCallback;
import com.stripe.android.Stripe;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.taxiuser.R;
import com.taxiuser.databinding.ActivityEndUserBinding;
import com.taxiuser.databinding.AddMoneyDialogBinding;
import com.taxiuser.models.ModelCurrentBooking;
import com.taxiuser.models.ModelCurrentBookingResult;
import com.taxiuser.models.ModelLogin;
import com.taxiuser.utils.AppConstant;
import com.taxiuser.utils.MyApplication;
import com.taxiuser.utils.ProjectUtil;
import com.taxiuser.utils.SharedPref;
import com.taxiuser.utils.retrofitutils.Api;
import com.taxiuser.utils.retrofitutils.ApiFactory;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.TimeZone;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EndUserAct extends AppCompatActivity {

    Context mContext = EndUserAct.this;
    ActivityEndUserBinding binding;
    private ModelCurrentBooking data;
    private ModelCurrentBookingResult result;
    String pickUp, dropOff;
    private ModelLogin.Result driverDetails;
    private String driverId, payType;
    ModelLogin modelLogin;
    SharedPref sharedPref;
    double serviceTax, distanceCost, tollCost, otherCost, waitingCost, serviceCost, totalCostFinal;
    private double walletTmpAmt = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_end_user);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        itit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
    }

    private void itit() {

        if (getIntent() != null) {

            data = (ModelCurrentBooking) getIntent().getSerializableExtra("data");
            result = data.getResult().get(0);
            pickUp = result.getPicuplocation();
            dropOff = result.getDropofflocation();
            payType = result.getPaymentType();

            binding.tvFrom.setText(pickUp);
            binding.tvDestination.setText(dropOff);
            binding.tvPayType.setText(result.getPaymentType().toUpperCase());

            Log.e("zddsasdfdasf", "pickUp = " + pickUp);
            Log.e("zddsasdfdasf", "dropOff = " + dropOff);
            Log.e("zddsasdfdasf", "payType = " + payType);
            Log.e("zddsasdfdasf", "payType = " + payType);

            Log.e("zddsasdfdasf", "payType = " + AppConstant.CURRENT_CURRENCY_VALUE);
            Log.e("zddsasdfdasf", "Double.parseDouble(result.getAmount()) * AppConstant.CURRENT_CURRENCY_VALUE = " + (Double.parseDouble(result.getAmount()) * AppConstant.CURRENT_CURRENCY_VALUE));
            Log.e("zddsasdfdasf", "Double.parseDouble(result.getAmount()) bracket mai = " + ((Double.parseDouble(result.getAmount())) * AppConstant.CURRENT_CURRENCY_VALUE));
            Log.e("zddsasdfdasf", "payType = " + AppConstant.CURRENCY);

            driverId = result.getDriverId();
            driverDetails = result.getDriver_details().get(0);

            if ("POOL".equals(result.getBooktype())) {
                binding.rlPassenger.setVisibility(View.VISIBLE);
                binding.rlType.setVisibility(View.GONE);
                binding.tvNoOfPassenger.setText(result.getBookedSeats());
            } else {
                binding.rlPassenger.setVisibility(View.GONE);
                binding.rlType.setVisibility(View.VISIBLE);
            }

            try {
                serviceTax = Double.parseDouble(result.getService_tax());
                serviceTax = Double.parseDouble(String.format("%.2f",serviceTax));
            } catch (Exception e) {
                serviceTax = 0.0;
            }

            try {
                distanceCost = Double.parseDouble(result.getAmount()) * AppConstant.CURRENT_CURRENCY_VALUE;
                distanceCost = Double.parseDouble(String.format("%.2f",distanceCost));
            } catch (Exception e) {
                distanceCost = 0.0;
            }

            try {
                tollCost = Double.parseDouble(result.getToll_charge()) * AppConstant.CURRENT_CURRENCY_VALUE;
                tollCost = Double.parseDouble(String.format("%.2f",tollCost));
            } catch (Exception e) {
                tollCost = 0.0;
            }

            try {
                otherCost = Double.parseDouble(result.getOther_charge()) * AppConstant.CURRENT_CURRENCY_VALUE;
                otherCost = Double.parseDouble(String.format("%.2f",otherCost));
            } catch (Exception e) {
                otherCost = 0.0;
            }

            try {
                waitingCost = Double.parseDouble(result.getWaiting_time_amount()) * AppConstant.CURRENT_CURRENCY_VALUE;
                waitingCost = Double.parseDouble(String.format("%.2f",waitingCost));
            } catch (Exception e) {
                waitingCost = 0.0;
            }

            try {
                serviceCost = ((distanceCost * Integer.parseInt(result.getService_tax())) / 100.0) * AppConstant.CURRENT_CURRENCY_VALUE;
                serviceCost = Double.parseDouble(String.format("%.2f",serviceCost));
            } catch (Exception e) {
                serviceCost = 0.0;
            }

            try {
                totalCostFinal = serviceCost + distanceCost + tollCost + otherCost + waitingCost;
            } catch (Exception e) {
                totalCostFinal = 0.0;
            }

            Log.e("dsfsdfdsf", "serviceCost = " + serviceCost);
            Log.e("dsfsdfdsf", "distanceCost = " + distanceCost);
            Log.e("dsfsdfdsf", "tollCost = " + tollCost);
            Log.e("dsfsdfdsf", "otherCost = " + otherCost);
            Log.e("dsfsdfdsf", "waitingCost = " + waitingCost);
            Log.e("dsfsdfdsf", "totalCostFinal = " + totalCostFinal);

            binding.tvFrom.setText(pickUp);
            binding.tvDestination.setText(dropOff);
            binding.tvServiceTax.setText(getString(R.string.tax) + serviceTax + "%");
            binding.tvTaxAmount.setText(AppConstant.CURRENCY + " " + serviceCost);
            binding.tvDistanceCost.setText(AppConstant.CURRENCY + " " + distanceCost);
            binding.tvDistance.setText(result.getDistance() + " Km");
            binding.tvTollCharge.setText(AppConstant.CURRENCY + " " + tollCost);
            binding.tvOtherCharge.setText(AppConstant.CURRENCY + " " + otherCost);
            binding.tvWaitingCharge.setText(AppConstant.CURRENCY + " " + waitingCost);
            binding.tvTotalPay.setText(String.format("%.2f", totalCostFinal));
            binding.tvPayType.setText(result.getPaymentType().toUpperCase());

        }

        binding.btnPay.setOnClickListener(v -> {
            if (result.getPaymentType().equals("Card")) {
                if (modelLogin.getResult().getCard_number() == null || modelLogin.getResult().getCard_number().equals("")) {
                    startActivity(new Intent(mContext, CardDetailsAct.class));
                    Toast.makeText(mContext, getString(R.string.please_add_your_card), Toast.LENGTH_LONG).show();
                } else {

                    try {
                        Log.e("dfasdasdas", "Exception = " + modelLogin.getResult().getExpiry_year());
                        Log.e("dfasdasdas", "Exception = " + Integer.valueOf(modelLogin.getResult().getExpiry_year().substring(2)));
                    } catch (Exception e) {
                        Log.e("dfasdasdas", "Exception = " + e.getMessage());
                    }

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
                                    onlinePayment("Card", token.getId());
                                    Log.e("asfadasdasd", "tokentokentoken = " + token.getId());
                                }

                                @Override
                                public void onError(@NotNull Exception e) {
                                    ProjectUtil.pauseProgressDialog();
                                }
                            });
                }
            } else if (result.getPaymentType().equals("Wallet")) {
                cashWalletApi("Wallet");
            } else {
                cashWalletApi("Cash");
            }
        });

    }

    private void onlinePayment(String payType, String token) {

        HashMap<String, String> map = new HashMap<>();

        map.put("payment_type", payType);
        map.put("amount", result.getAmount());
        map.put("user_id", modelLogin.getResult().getId());
        map.put("request_id", result.getId());
        map.put("token", token);
        map.put("currency", "AUD");

        Log.e("AcceptCancel", "AcceptCancel = " + map);

        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.stripePaymentApiCAll(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String stringResponse = response.body().string();

                    Log.e("asdfasdfasdfas", "stringResponse = " + stringResponse);

                    JSONObject jsonObject = new JSONObject(stringResponse);

                    if (jsonObject.getString("status").equals("1")) {
                        finish();
                        startActivity(new Intent(mContext, DriverFeedbackAct.class)
                                .putExtra("data", data)
                        );
                        MyApplication.showToast(mContext, getString(R.string.trip_finished));
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

    public void cashWalletApi(String payType) {

        HashMap<String, String> map = new HashMap<>();

        map.put("payment_type", payType);
        map.put("amount", result.getAmount());
        map.put("user_id", modelLogin.getResult().getId());
        map.put("request_id", result.getId());
        map.put("tip", "0");
        map.put("car_charge", "0");
        map.put("timezone", TimeZone.getDefault().getID());
        map.put("currency", "AUD");

        Log.e("AcceptCancel", "AcceptCancel = " + map);

        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.paymentApiCall(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String stringResponse = response.body().string();

                    Log.e("asdfasdfasdfas", "stringResponse = " + stringResponse);

                    JSONObject jsonObject = new JSONObject(stringResponse);
                    if (jsonObject.getString("status").equals("1")) {
                        finish();
                        startActivity(new Intent(mContext, DriverFeedbackAct.class)
                                .putExtra("data", data)
                        );
                        MyApplication.showToast(mContext, getString(R.string.trip_finished));
                    } else if (jsonObject.getString("status").equals("2")) {
                        showAmountDialog();
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

    private void showAmountDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(getString(R.string.insufficient_amount_text))
                .setCancelable(false)
                .setPositiveButton(mContext.getString(R.string.ok)
                        , new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                addMoneyDialog();
                            }
                        }).create().show();
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
            Log.e("asdasggggdasd", "Ayyaaa");
            walletTmpAmt = Double.parseDouble(dialogBinding.etMoney.getText().toString().trim());

            if (modelLogin.getResult().getCard_number() == null ||
                    modelLogin.getResult().getCard_number().equals("")) {
                startActivity(new Intent(mContext, CardDetailsAct.class));
            }

            if (TextUtils.isEmpty(dialogBinding.etMoney.getText().toString().trim())) {
                Toast.makeText(mContext, "Please enter amount", Toast.LENGTH_SHORT).show();
            } else if (walletTmpAmt == 0.0) {
                Toast.makeText(mContext, "Please enter valid amount", Toast.LENGTH_SHORT).show();
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
                                addWalletAmountApi(String.valueOf(walletTmpAmt), dialog, token.getId(), "AUD");
                                Log.e("asfadasdasd", "tokentokentoken = " + token.getId());
                            }

                            @Override
                            public void onError(@NotNull Exception e) {
                                ProjectUtil.pauseProgressDialog();
                            }
                        });

                //

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

    private void addWalletAmountApi(String amount, Dialog dialog, String token, String currency) {

        HashMap<String, String> map = new HashMap<>();
        map.put("user_id", modelLogin.getResult().getId());
        map.put("amount", amount);
        map.put("token", token);
        map.put("currency", currency);

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
                        MyApplication.showAlert(mContext, getString(R.string.amount_wallet_added_success));
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

}