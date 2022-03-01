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
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.taxiuser.R;
import com.taxiuser.databinding.ActivityMyProfileBinding;
import com.taxiuser.databinding.AddMoneyDialogBinding;
import com.taxiuser.databinding.ChangePasswordDialogBinding;
import com.taxiuser.databinding.ContactUsDialogBinding;
import com.taxiuser.databinding.SendMoneyDialogBinding;
import com.taxiuser.models.ModelLogin;
import com.taxiuser.utils.AppConstant;
import com.taxiuser.utils.InternetConnection;
import com.taxiuser.utils.MyApplication;
import com.taxiuser.utils.ProjectUtil;
import com.taxiuser.utils.SharedPref;
import com.taxiuser.utils.retrofitutils.Api;
import com.taxiuser.utils.retrofitutils.ApiFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyProfileAct extends AppCompatActivity {

    Context mContext = MyProfileAct.this;
    ActivityMyProfileBinding binding;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    private double walletTmpAmt = 0.0;
    private String registerId = "";
    private LatLng homeLatLng, workLatlong;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 101;
    private static final int AUTOCOMPLETE_WORK_CODE = 103;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_profile);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);

        if (!Places.isInitialized()) {
            Places.initialize(mContext, getString(R.string.api_key));
        }

        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {
            if (!TextUtils.isEmpty(token)) {
                registerId = token;
                Log.e("tokentoken", "retrieve token successful : " + token);
            } else {
                Log.e("tokentoken", "token should not be null...");
            }
        }).addOnFailureListener(e -> {
        }).addOnCanceledListener(() -> {
        });

        itit();
    }

    @Override
    public void onResume() {
        super.onResume();

        getProfileApiCall();

        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);

        Glide.with(mContext)
                .load(modelLogin.getResult().getImage())
                .placeholder(R.drawable.user_ic)
                .error(R.drawable.user_ic)
                .into(binding.cvImg);

        if (modelLogin.getResult().getUser_name() == null ||
                modelLogin.getResult().getUser_name().equals("")) {
            binding.tvName.setText(modelLogin.getResult().getFirst_name() + " " +
                    modelLogin.getResult().getLast_name());
        } else {
            binding.tvName.setText(modelLogin.getResult().getUser_name());
        }

//      binding.tvHomeAddress.setText(modelLogin.getResult().getWorkplace());
//      binding.tvWorkAddress.setText(modelLogin.getResult().getAddress());

        binding.tvEmail.setText(modelLogin.getResult().getEmail());

    }

    private void itit() {

        binding.editProfile.setOnClickListener(v -> {
            startActivity(new Intent(mContext, UpdateProfileAct.class));
        });

        binding.llDonate.setOnClickListener(v -> {
            startActivity(new Intent(mContext, DonateAct.class));
        });

        binding.lllang.setOnClickListener(v -> {
            changeLangDialog();
        });

        binding.llLiveChat.setOnClickListener(v -> {
            startActivity(new Intent(mContext, ChatingAct.class));
        });

        binding.llFAQ.setOnClickListener(v -> {
            startActivity(new Intent(mContext, FAQAct.class));
        });

        binding.llBusinessProfile.setOnClickListener(v -> {
            startActivity(new Intent(mContext, BusinessProfileAct.class));
        });

        binding.llNotifications.setOnClickListener(v -> {
            startActivity(new Intent(mContext, NotificationAct.class));
        });

        binding.llFavDrivers.setOnClickListener(v -> {
            startActivity(new Intent(mContext, FavouriteDriverAct.class));
        });

        binding.llContactUs.setOnClickListener(v -> {
            openContactUsDialog();
        });

        binding.llPersonalDetails.setOnClickListener(v -> {
            startActivity(new Intent(mContext, UpdateProfileAct.class));
        });

        binding.rlLogout.setOnClickListener(v -> {
            ProjectUtil.logoutAppDialog(mContext);
        });

        binding.topUp.setOnClickListener(v -> {
            addMoneyDialog();
        });

        binding.llEmergencyContact.setOnClickListener(v -> {
            startActivity(new Intent(mContext, EmergencyContactAct.class));
        });

        binding.llAboutUs.setOnClickListener(v -> {
            startActivity(new Intent(mContext, WebviewAct.class)
                    .putExtra("url", "https://technorizen.com/australia_taxi/about_us.html")
            );
        });

        binding.llTermsAndCondition.setOnClickListener(v -> {
            startActivity(new Intent(mContext, WebviewAct.class)
                    .putExtra("url", "https://technorizen.com/australia_taxi/terms&condition.html")
            );
        });

        binding.llPrivacyPolicy.setOnClickListener(v -> {
            startActivity(new Intent(mContext, WebviewAct.class)
                    .putExtra("url", "https://technorizen.com/australia_taxi/privacy-policy.html")
            );
        });

        binding.llAddMoney.setOnClickListener(v -> {
            addMoneyDialog();
        });

        binding.llSendMoney.setOnClickListener(v -> {
            tranferMOneyDialog();
        });

        binding.llBooking.setOnClickListener(v -> {
            startActivity(new Intent(mContext, ActiveBookingAct.class));
        });

        binding.llPaymentMethod.setOnClickListener(v -> {
            startActivity(new Intent(mContext, CardDetailsAct.class));
        });

        binding.llYourTrip.setOnClickListener(v -> {
            startActivity(new Intent(mContext, RideHistoryAct.class));
        });

        binding.llMyWallet.setOnClickListener(v -> {
            startActivity(new Intent(mContext, WalletAct.class));
        });

        binding.llWallet.setOnClickListener(v -> {
            startActivity(new Intent(mContext, WalletAct.class));
        });

        binding.llActiveTrips.setOnClickListener(v -> {
            startActivity(new Intent(mContext, ActiveBookingAct.class));
        });

        binding.llChangePass.setOnClickListener(v -> {
            changePasswordDialog();
        });

        binding.llHomeAddress.setOnClickListener(v -> {
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(this);
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
        });

        binding.llWorkAddress.setOnClickListener(v -> {
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(this);
            startActivityForResult(intent, AUTOCOMPLETE_WORK_CODE);
        });

    }

    private void changeLangDialog() {
        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setContentView(R.layout.change_language_dialog);
        dialog.setCancelable(true);

        Button btContinue = dialog.findViewById(R.id.btContinue);
        RadioButton radioEng = dialog.findViewById(R.id.radioEng);
        RadioButton radioUrdu = dialog.findViewById(R.id.radioUrdu);
        RadioButton radioChinese = dialog.findViewById(R.id.radioChinese);
        RadioButton radioArabic = dialog.findViewById(R.id.radioArabic);
        RadioButton radioFrench = dialog.findViewById(R.id.radioFrench);

        if ("en".equals(sharedPref.getLanguage("lan"))) {
            radioEng.setChecked(true);
        } else if ("ar".equals(sharedPref.getLanguage("lan"))) {
            radioArabic.setChecked(true);
        } else if ("fr".equals(sharedPref.getLanguage("lan"))) {
            radioFrench.setChecked(true);
        } else if ("ur".equals(sharedPref.getLanguage("lan"))) {
            radioUrdu.setChecked(true);
        } else if ("zh".equals(sharedPref.getLanguage("lan"))) {
            radioChinese.setChecked(true);
        } else {
            radioEng.setChecked(true);
        }

        dialog.getWindow().setBackgroundDrawableResource(R.color.translucent_black);

        btContinue.setOnClickListener(v -> {
            if (radioEng.isChecked()) {
                ProjectUtil.updateResources(mContext, "en");
                sharedPref.setlanguage("lan", "en");
                finishAffinity();
                startActivity(new Intent(mContext, SplashAct.class));
                dialog.dismiss();
            } else if (radioUrdu.isChecked()) {
                ProjectUtil.updateResources(mContext, "ur");
                sharedPref.setlanguage("lan", "ur");
                finishAffinity();
                startActivity(new Intent(mContext, SplashAct.class));
                dialog.dismiss();
            } else if (radioArabic.isChecked()) {
                ProjectUtil.updateResources(mContext, "ar");
                sharedPref.setlanguage("lan", "ar");
                finishAffinity();
                startActivity(new Intent(mContext, SplashAct.class));
                dialog.dismiss();
            } else if (radioFrench.isChecked()) {
                ProjectUtil.updateResources(mContext, "fr");
                sharedPref.setlanguage("lan", "fr");
                finishAffinity();
                startActivity(new Intent(mContext, SplashAct.class));
                dialog.dismiss();
            } else if (radioChinese.isChecked()) {
                ProjectUtil.updateResources(mContext, "zh");
                sharedPref.setlanguage("lan", "zh");
                finishAffinity();
                startActivity(new Intent(mContext, SplashAct.class));
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private void openContactUsDialog() {

        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        ContactUsDialogBinding dialogBinding = DataBindingUtil
                .inflate(LayoutInflater.from(mContext), R.layout.contact_us_dialog, null, false);
        dialog.setContentView(dialogBinding.getRoot());

        dialogBinding.ivBack.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialogBinding.btnSubmit.setOnClickListener(v -> {
            if (TextUtils.isEmpty(dialogBinding.etTitle.getText().toString().trim())) {
                MyApplication.showAlert(mContext, getString(R.string.field_is_required));
            } else if (TextUtils.isEmpty(dialogBinding.etdetail.getText().toString().trim())) {
                MyApplication.showAlert(mContext, getString(R.string.field_is_required));
            } else {
                if (InternetConnection.checkConnection(mContext)) {
                    contactUsApi(dialogBinding.etTitle.getText().toString().trim(),
                            dialogBinding.etdetail.getText().toString().trim(), dialog);
                } else {
                    MyApplication.showConnectionDialog(mContext);
                }
            }
        });

        dialog.show();

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
                            getProfileApiCall();
                            dialog.dismiss();

                        } else {
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
            }

        });

    }

    private void contactUsApi(String title, String detail, Dialog parentDialog) {
        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));

        HashMap<String, String> paramHash = new HashMap<>();
        paramHash.put("user_id", modelLogin.getResult().getId());
        paramHash.put("feedback", title);
        paramHash.put("detail", detail);

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.contactUsApiCall(paramHash);
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
                            showAlertDialog(parentDialog);
                            Log.e("asfddasfasdf", "response = " + response);
                        } else {}
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

    private void showAlertDialog(Dialog parentDialog) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(R.string.contact_alert_text);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                parentDialog.dismiss();
            }
        }).create().show();
    }

    private void updateAddressApi() {
        // ProjectUtil.showProgressDialog(mContext, true, getString(R.string.please_wait));

        HashMap<String, String> param = new HashMap<>();
        param.put("user_id", modelLogin.getResult().getId());
        param.put("lat", modelLogin.getResult().getLat());
        param.put("lon", modelLogin.getResult().getLon());
        param.put("work_lat", modelLogin.getResult().getWork_lat());
        param.put("work_lon", modelLogin.getResult().getWork_lon());
        param.put("address", binding.tvHomeAddress.getText().toString().trim());
        param.put("workplace", binding.tvWorkAddress.getText().toString().trim());

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.updateAddressApiCall(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    Log.e("updateAddressApi", "responseString = " + responseString);

                    if (jsonObject.getString("status").equals("1")) {
                        Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    // Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception", "Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
                Log.e("Exception", "Throwable = " + t.getMessage());
            }

        });
    }

    private void getProfileApiCall() {
        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));

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
                            sharedPref.setUserDetails(AppConstant.USER_DETAILS, modelLogin);

                            binding.tvHomeAddress.setText(modelLogin.getResult().getAddress());
                            binding.tvWorkAddress.setText(modelLogin.getResult().getWorkplace());

                            try {
                                workLatlong = new LatLng(Double.parseDouble(modelLogin.getResult().getWork_lat())
                                        , Double.parseDouble(modelLogin.getResult().getWork_lon()));
                            } catch (Exception e) {
                            }


                            if (!registerId.equals(modelLogin.getResult().getRegister_id())) {
                                logoutAlertDialog();
                            }

                            binding.tvWalletAmount.setText(AppConstant.CURRENCY + " " + modelLogin.getResult().getWallet());

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

    private void logoutAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("Your session is expired Please login Again!")
                .setCancelable(false)
                .setPositiveButton(mContext.getString(R.string.ok)
                        , new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sharedPref.clearAllPreferences();
                                finishAffinity();
                                startActivity(new Intent(mContext, LoginAct.class));
                                dialog.dismiss();
                            }
                        }).create().show();
    }

    private void tranferMOneyDialog() {

        Dialog dialog = new Dialog(mContext, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        SendMoneyDialogBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
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
                if (dialogBinding.rbUser.isChecked()) {
                    sendMoneyAPiCall(dialog, dialogBinding.etEmail.getText().toString().trim()
                            , dialogBinding.etEnterAmount.getText().toString().trim(), "USER");
                } else if (dialogBinding.rbDriver.isChecked()) {
                    sendMoneyAPiCall(dialog, dialogBinding.etEmail.getText().toString().trim()
                            , dialogBinding.etEnterAmount.getText().toString().trim(), "DRIVER");
                } else {
                    MyApplication.showAlert(mContext, getString(R.string.select_user_driver_text));
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

    private void changePasswordDialog() {
        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);

        ChangePasswordDialogBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext)
                , R.layout.change_password_dialog, null, false);
        dialog.setContentView(dialogBinding.getRoot());

        dialogBinding.ivBack.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialogBinding.btSubmit.setOnClickListener(v -> {
            if (TextUtils.isEmpty(dialogBinding.etOldPass.getText().toString().trim())) {
                MyApplication.showAlert(mContext, getString(R.string.enter_old_pass));
            } else if (TextUtils.isEmpty(dialogBinding.etNewPass.getText().toString().trim())) {
                MyApplication.showAlert(mContext, getString(R.string.enter_new_pass));
            } else if (!(dialogBinding.etNewPass.getText().toString().trim().length() >= 5)) {
                MyApplication.showAlert(mContext, getString(R.string.password_validation_text));
            } else {
                if (modelLogin.getResult().getPassword().trim().equals(dialogBinding.etOldPass.getText().toString().trim())) {
                    changePasswordApi(dialogBinding.etNewPass.getText().toString().trim(), dialog);
                } else {
                    MyApplication.showAlert(mContext, getString(R.string.old_pass_is_incorrect));
                }
            }
        });

        dialog.show();

    }

    private void changePasswordApi(String password, Dialog dialog) {
        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));

        HashMap<String, String> paramHash = new HashMap<>();
        paramHash.put("user_id", modelLogin.getResult().getId());
        paramHash.put("password", password);

        Log.e("sadasddasd", "paramHash = " + paramHash);

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.changePass(paramHash);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                dialog.dismiss();
                try {
                    String stringResponse = response.body().string();

                    try {

                        JSONObject jsonObject = new JSONObject(stringResponse);

                        if (jsonObject.getString("status").equals("1")) {

                            Log.e("asfddasfasdf", "response = " + response);
                            Log.e("asfddasfasdf", "stringResponse = " + stringResponse);

                            modelLogin.getResult().setPassword(password);
                            sharedPref.setUserDetails(AppConstant.USER_DETAILS, modelLogin);

                            Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();
                        } else {

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

        dialogBinding.ivMinus.setOnClickListener(v -> {
            if (!(dialogBinding.etMoney.getText().toString().trim().equals("") || dialogBinding.etMoney.getText().toString().trim().equals("0"))) {
                walletTmpAmt = Double.parseDouble(dialogBinding.etMoney.getText().toString().trim()) - 1;
                dialogBinding.etMoney.setText(String.valueOf(walletTmpAmt));
            }
        });

        dialogBinding.btDone.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialogBinding.tvCancel.setOnClickListener(v -> {
            dialog.dismiss();
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
                Toast.makeText(mContext, "Please enter amount", Toast.LENGTH_SHORT).show();
            } else if (walletTmpAmt == 0.0) {
                Toast.makeText(mContext, "Please enter valid amount", Toast.LENGTH_SHORT).show();
            } else {
                // dialog.dismiss();
                addWalletAmountApi(String.valueOf(walletTmpAmt), dialog);
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

    private void addWalletAmountApi(String amount, Dialog dialog) {

        HashMap<String, String> map = new HashMap<>();
        map.put("user_id", modelLogin.getResult().getId());
        map.put("amount", amount);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                homeLatLng = place.getLatLng();
                try {
                    String addresses = ProjectUtil.getCompleteAddressString(mContext, place.getLatLng().latitude, place.getLatLng().longitude);
                    binding.tvHomeAddress.setText(addresses);
                    modelLogin.getResult().setAddress(addresses);
                    modelLogin.getResult().setLat(String.valueOf(place.getLatLng().latitude));
                    modelLogin.getResult().setLon(String.valueOf(place.getLatLng().longitude));
                    sharedPref.setUserDetails(AppConstant.USER_DETAILS, modelLogin);
                    updateAddressApi();
                } catch (Exception e) {
                }
            }
        } else if (requestCode == AUTOCOMPLETE_WORK_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                workLatlong = place.getLatLng();
                try {
                    String addresses = ProjectUtil.getCompleteAddressString(mContext, place.getLatLng().latitude, place.getLatLng().longitude);
                    binding.tvWorkAddress.setText(addresses);
                    modelLogin.getResult().setWorkplace(addresses);
                    modelLogin.getResult().setWork_lat(String.valueOf(place.getLatLng().latitude));
                    modelLogin.getResult().setWork_lon(String.valueOf(place.getLatLng().longitude));
                    sharedPref.setUserDetails(AppConstant.USER_DETAILS, modelLogin);
                    updateAddressApi();
                } catch (Exception e) {
                }
            }
        }
    }


}