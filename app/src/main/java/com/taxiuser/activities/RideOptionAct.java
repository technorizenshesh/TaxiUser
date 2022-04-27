package com.taxiuser.activities;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.taxiuser.R;
import com.taxiuser.adapters.AdapterCarTypes;
import com.taxiuser.databinding.AcceptDriverDialogBinding;
import com.taxiuser.databinding.ActivityRideOptionBinding;
import com.taxiuser.databinding.DialogSearchDriverBinding;
import com.taxiuser.databinding.SanitixzeAgreeDialogBinding;
import com.taxiuser.databinding.ScheduleBookingDialogBinding;
import com.taxiuser.models.ModelAvailableDriver;
import com.taxiuser.models.ModelCar;
import com.taxiuser.models.ModelCurrentBooking;
import com.taxiuser.models.ModelCurrentBookingResult;
import com.taxiuser.models.ModelLogin;
import com.taxiuser.utils.AppConstant;
import com.taxiuser.utils.ProjectUtil;
import com.taxiuser.utils.SharedPref;
import com.taxiuser.utils.onSearchingDialogListener;
import com.taxiuser.utils.retrofitutils.Api;
import com.taxiuser.utils.retrofitutils.ApiFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RideOptionAct extends AppCompatActivity implements OnMapReadyCallback,
        onSearchingDialogListener {

    Context mContext = RideOptionAct.this;
    ActivityRideOptionBinding binding;
    ModelLogin modelLogin;
    SharedPref sharedPref;
    Dialog dialogSerach;
    String bookDate = "", bookTime = "", bookType = "";
    GoogleMap mMap;
    String paymentType = "", pickadd = "", dropadd = "";
    private PolylineOptions lineOptions;
    private LatLng PickUpLatLng, DropOffLatLng;
    private MarkerOptions PicUpMarker, DropOffMarker;
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 2000;  /* 5 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */
    Location currentLocation;
    private String CarTypeID = "";
    private String carAmount = "";
    Timer timer;
    private String serviceName = "";
    private String registerId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_ride_option);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);

        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {
            if (!TextUtils.isEmpty(token)) {
                registerId = token;
                Log.e("tokentoken", "retrieve token successful : " + token);
            } else {
                Log.e("tokentoken", "token should not be null...");
            }
        }).addOnFailureListener(e -> {
        }).addOnCanceledListener(() -> {
        }).addOnCompleteListener(task -> Log.e("tokentoken", "This is the token : " + task.getResult()));

        PicUpMarker = new MarkerOptions().title("Pick Up Location " + pickadd)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_marker));
        DropOffMarker = new MarkerOptions().title("Drop Off Location " + dropadd)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_marker));

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(RideOptionAct.this);

        startLocationUpdates();
        getProfile();
        getData();

        itit();

    }

    private void getProfileNew(String bootype) {
        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

        HashMap<String, String> param = new HashMap<>();
        param.put("user_id", modelLogin.getResult().getId());

        Call<ResponseBody> call = api.getProfileCall(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String stringResponse = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(stringResponse);
                        if (jsonObject.getString("status").equals("1")) {

                            modelLogin = new Gson().fromJson(stringResponse, ModelLogin.class);
                            sharedPref.setUserDetails(AppConstant.USER_DETAILS, modelLogin);

                            if (!registerId.equals(modelLogin.getResult().getRegister_id())) {
                                logoutAlertDialog();
                            }

                            if (modelLogin.getResult().getCovid_screen_status() != null) {
                                if (modelLogin.getResult().getCovid_screen_status().equalsIgnoreCase("Active")) {
                                    sanitizeDialog(bootype);
                                } else {
                                    if (bootype.equalsIgnoreCase("Now")) {
                                        if (Validation()) {
                                            bookingRequest("NOW");
                                        }
                                    } else {
                                        openScheduleBookingDialog();
                                    }
                                }
                            } else {
                                if (bootype.equalsIgnoreCase("Now")) {
                                    if (Validation()) {
                                        bookingRequest("NOW");
                                    }
                                } else {
                                    openScheduleBookingDialog();
                                }
                            }

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

    private void getProfile() {
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

        HashMap<String, String> param = new HashMap<>();
        param.put("user_id", modelLogin.getResult().getId());

        Call<ResponseBody> call = api.getProfileCall(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String stringResponse = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(stringResponse);
                        if (jsonObject.getString("status").equals("1")) {

                            modelLogin = new Gson().fromJson(stringResponse, ModelLogin.class);
                            sharedPref.setUserDetails(AppConstant.USER_DETAILS, modelLogin);

                            if (!registerId.equals(modelLogin.getResult().getRegister_id())) {
                                logoutAlertDialog();
                            }

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

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(JobStatusReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        registerReceiver(JobStatusReceiver, new IntentFilter("Job_Status_Action_Accept"));
    }

    BroadcastReceiver JobStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getStringExtra("status") != null) {
                try {
                    if ("Accept".equals(intent.getStringExtra("status"))) {
                        if ("LATER".equals(intent.getStringExtra("booktype"))) {
                            dialogSerach.dismiss();
                            scheduleBookingAcceptedDialog();
                        } else {
                            dialogSerach.dismiss();
                            bookNowDialog(intent.getStringExtra("request_id"));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private void scheduleBookingAcceptedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(R.string.see_active_bookings_text);
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(mContext, ActiveBookingAct.class));
                finish();
            }
        }).create().show();
    }

    private void getBookingDetails(String requestId) {

        ProjectUtil.showProgressDialog(mContext, false, mContext.getString(R.string.please_wait));

        HashMap<String, String> param = new HashMap<>();
        param.put("request_id", requestId);
        param.put("type", "USER");

        Log.e("paramparam", "param = " + param);

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getCurrentBookingDetails(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    Log.e("responseString", "responseString = " + responseString);

                    if (jsonObject.getString("status").equals("1")) {
                        Log.e("getCurrentBooking", "getCurrentBooking = " + responseString);
                        Log.e("getCurrentBooking", "getCurrentBooking = " + responseString);
                        Type listType = new TypeToken<ModelCurrentBooking>() {
                        }.getType();
                        ModelCurrentBooking data = new GsonBuilder().create().fromJson(responseString, listType);
                        if (data.getStatus().equals(1)) {
                            ModelCurrentBookingResult result = data.getResult().get(0);
                            Log.e("getUserRatingStatus", "getUserRatingStatus = " + result.getUserRatingStatus());
                            Log.e("getUserRatingStatus", "ModelCurrentBookingResult = " + result.getPayment_status());
                            if (result.getStatus().equalsIgnoreCase("Accept")) {
                                Intent k = new Intent(mContext, TrackAct.class);
                                k.putExtra("data", data);
                                mContext.startActivity(k);
                            } else if (result.getStatus().equalsIgnoreCase("Arrived")) {
                                Intent k = new Intent(mContext, TrackAct.class);
                                k.putExtra("data", data);
                                mContext.startActivity(k);
                            } else if (result.getStatus().equalsIgnoreCase("Start")) {
                                Intent k = new Intent(mContext, TrackAct.class);
                                k.putExtra("data", data);
                                mContext.startActivity(k);
                            } else if (result.getStatus().equalsIgnoreCase("End")) {
                                if ("Success".equals(result.getPayment_status())) {
                                    if (null == result.getUserRatingStatus() ||
                                            "".equals(result.getUserRatingStatus())) {
                                        Intent j = new Intent(mContext, DriverFeedbackAct.class);
                                        j.putExtra("data", data);
                                        mContext.startActivity(j);
                                    }
                                } else {
                                    Intent j = new Intent(mContext, EndUserAct.class);
                                    j.putExtra("data", data);
                                    mContext.startActivity(j);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    // Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception", "Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
            }

        });

    }

    private void bookNowDialog(String requestId) {
        dialogSerach.dismiss();
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(R.string.request_accepted_by_driver);
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getBookingDetails(requestId);
            }
        }).create().show();
    }

    private void getData() {

        if (getIntent().getExtras() != null) {
            lineOptions = (PolylineOptions) getIntent().getExtras().get("pollyLine");
            PickUpLatLng = (LatLng) getIntent().getExtras().get("PickUp");
            DropOffLatLng = (LatLng) getIntent().getExtras().get("DropOff");
            pickadd = getIntent().getStringExtra("picadd");
            dropadd = getIntent().getStringExtra("dropadd");
        }

    }

    private void itit() {

        getCar();

        binding.btnBack.setOnClickListener(v -> {
            finish();
        });

        binding.btnScheduleRide.setOnClickListener(v -> {
            if (TextUtils.isEmpty(modelLogin.getResult().getMobile())) {
                updateProfileDialog();
            } else {
                getProfileNew("");
            }
        });

        binding.btnBook.setOnClickListener(v -> {
            if (TextUtils.isEmpty(modelLogin.getResult().getMobile())) {
                updateProfileDialog();
            } else {
                getProfileNew("Now");
            }
        });

    }

    public void updateProfileDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(R.string.please_update_profile_text)
                .setCancelable(false)
                .setPositiveButton(mContext.getString(R.string.ok)
                        , new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                startActivity(new Intent(mContext, UpdateProfileAct.class));
                            }
                        }).create().show();
    }

    private void sanitizeDialog(String type) {
        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        SanitixzeAgreeDialogBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                R.layout.sanitixze_agree_dialog, null, false);
        dialog.setContentView(dialogBinding.getRoot());

        dialogBinding.btCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialogBinding.btnAgree.setOnClickListener(v -> {
            if (type.equalsIgnoreCase("Now")) {
                if (Validation()) {
                    bookingRequest("NOW");
                }
            } else {
                openScheduleBookingDialog();
            }
            dialog.dismiss();
        });

        dialog.show();

    }

    private void bookingRequest(String type) {
        bookType = type;
        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.bookingRequestApi(getBookingParam(type));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {

                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    Log.e("GetCar", "==>" + response);

                    Log.e("bookingRequest", "==>" + responseString);

                    try {
                        JSONObject object = new JSONObject(responseString);
                        if (object.getString("status").equals("1")) {
                            if (object.getString("message").equals("already in ride")) {
                                alertForAlreadyInRide();
                            } else {
                                String request_id = object.getString("request_id");
                                sharedPref.setlanguage(AppConstant.LAST, request_id);
                                driverSerachDialog();
                            }
                        } else {
                            onDriverNotFound();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    // Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception", "Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
            }

        });

    }

    private void alertForAlreadyInRide() {
        try {
            dialogSerach.dismiss();
        } catch (Exception e) {
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(R.string.already_in_ride);
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(mContext, ActiveBookingAct.class));
                finish();
            }
        }).create().show();
    }

    private void cancelRequestApi(Dialog dialog) {

        HashMap<String, String> parmas = new HashMap<>();
        parmas.put("request_id", sharedPref.getLanguage(AppConstant.LAST));

        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.cancelRequestForUser(parmas);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    Log.e("GetCar", "==>" + response);
                    Log.e("bookingRequest", "==>" + responseString);
                    try {
                        JSONObject object = new JSONObject(responseString);
                        if (object.getString("status").equals("1")) {
                            //  Toast.makeText(mContext, "" + object.getString("message"), Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            finish();
                        } else {
                            // Toast.makeText(mContext, "" + object.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    //  Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception", "Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
            }

        });
    }

    private void getCar() {

        HashMap<String, String> param = new HashMap<>();
        param.put("picuplat", "" + PickUpLatLng.latitude);
        param.put("pickuplon", "" + PickUpLatLng.longitude);
        param.put("droplat", "" + DropOffLatLng.latitude);
        param.put("droplon", "" + DropOffLatLng.longitude);
        param.put("user_id", modelLogin.getResult().getId());

        Log.e("fsdafsfadsf", "param = " + param);

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getCarTypeListApi(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();

                    Log.e("GetCar", "==>" + responseString);
                    try {
                        JSONObject object = new JSONObject(responseString);
                        if (object.getString("status").equals("1")) {
                            Type listType = new TypeToken<ArrayList<ModelCar>>() {
                            }.getType();
                            ArrayList<ModelCar> cars = new GsonBuilder().create().fromJson(object.getString("result"), listType);
                            cars.get(0).setSelected(true);
                            binding.recycleView.setAdapter(new AdapterCarTypes(mContext, cars).Callback(RideOptionAct.this::onSelectCar));
                            onSelectCar(cars.get(0), "basic_car");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    // Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception", "Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
            }

        });

    }

    private void openScheduleBookingDialog() {

        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        ScheduleBookingDialogBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                R.layout.schedule_booking_dialog, null, false);
        dialog.setContentView(dialogBinding.getRoot());

        dialog.getWindow().setBackgroundDrawableResource(R.color.translucent_black);

        dialogBinding.etDate.setOnClickListener(v -> {

            final Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH);
            int mDay = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dpd = new DatePickerDialog(mContext,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            c.set(year, monthOfYear, dayOfMonth);
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            String finalDate = simpleDateFormat.format(c.getTime());
                            // bookDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                            dialogBinding.etDate.setText(finalDate);
                        }
                    }, mYear, mMonth, mDay);
            dpd.getDatePicker().setMinDate(new Date().getTime());
            dpd.show();

        });

        dialogBinding.etTime.setOnClickListener(v -> {
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                    String am_pm = "";

                    Calendar datetime = Calendar.getInstance();
                    datetime.set(Calendar.HOUR_OF_DAY, selectedHour);
                    datetime.set(Calendar.MINUTE, minute);

                    if (datetime.get(Calendar.AM_PM) == Calendar.AM)
                        am_pm = "AM";
                    else if (datetime.get(Calendar.AM_PM) == Calendar.PM)
                        am_pm = "PM";

                    String strHrsToShow = (datetime.get(Calendar.HOUR) == 0) ? "12" : datetime.get(Calendar.HOUR) + "";
                    dialogBinding.etTime.setText(strHrsToShow + ":" + datetime.get(Calendar.MINUTE) + " " + am_pm);
                    bookTime = strHrsToShow + ":" + datetime.get(Calendar.MINUTE) + " " + am_pm;
//                    dialogBinding.etTime.setText(bookTime);
                }
            }, hour, minute, false); // Yes 24 hour time
            mTimePicker.show();
        });

        dialogBinding.btnCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialogBinding.btnSubmit.setOnClickListener(v -> {
            dialog.dismiss();
            if (TextUtils.isEmpty(dialogBinding.etDate.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_select_date), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(dialogBinding.etTime.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_select_time), Toast.LENGTH_SHORT).show();
            } else {
                if (Validation()) {
                    bookingRequest("LATER");
                }
            }
        });

        dialog.show();

    }

    private void driverSerachDialog() {
        dialogSerach = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        DialogSearchDriverBinding dialogBinding = DataBindingUtil
                .inflate(LayoutInflater.from(mContext), R.layout.dialog_search_driver, null, false);
        dialogSerach.setContentView(dialogBinding.getRoot());
        dialogBinding.ripple.startRippleAnimation();

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // getCurrentBookingApi();
            }
        }, 0, 4000);

        dialogBinding.btnCancel.setOnClickListener(v -> {
            cancelRequestDialog(dialogSerach);
        });

        dialogSerach.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialogSerach.show();
    }

    private void cancelRequestDialog(Dialog dialog1) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(getString(R.string.sure_request_cancel));
        builder.setPositiveButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                cancelRequestApi(dialog1);
            }
        }).create().show();
    }

    private void driverArrivedDialog(ModelCurrentBooking data) {

        dialogSerach.dismiss();

        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        AcceptDriverDialogBinding dialogBinding = DataBindingUtil
                .inflate(LayoutInflater.from(mContext), R.layout.accept_driver_dialog,
                        null, false);
        dialog.setContentView(dialogBinding.getRoot());

        timer.cancel();

        dialogBinding.btnOk.setOnClickListener(v -> {
            dialog.dismiss();
            Intent k = new Intent(mContext, TrackAct.class);
            k.putExtra("data", data);
            startActivity(k);
            finish();
        });

        dialogBinding.btnCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();

    }

    private void getNearDriver(String id, String name) {

        HashMap<String, String> param = new HashMap<>();

        param.put("lat", "" + PickUpLatLng.latitude);
        param.put("lon", "" + PickUpLatLng.longitude);
        param.put("user_id", modelLogin.getResult().getId());
        param.put("timezone", TimeZone.getDefault().getID());
        param.put("car_type_id", "1");
        param.put("service_name", name);

        Log.e("getNearDriverDriver", "paramparam = " + param);

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getAvailableDrivers(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    Log.e("getNearDriverDriver", "responseString = " + responseString);

                    if (jsonObject.getString("status").equals("1")) {
                        Type listType = new TypeToken<ArrayList<ModelAvailableDriver>>() {
                        }.getType();
                        ArrayList<ModelAvailableDriver> drivers = new GsonBuilder().create().fromJson(jsonObject.getString("result"), listType);
                        if (mMap != null) {
                            AddDefaultMarker();

                            CarTypeID = drivers.get(0).getCarTypeId();

                            for (ModelAvailableDriver driver : drivers) {
                                int height = 95;
                                int width = 65;
                                Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.car_top);
                                Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                                BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);
                                MarkerOptions car;
                                if (driver.getUser_name() == null || driver.getUser_name().equals("")) {
                                    car = new MarkerOptions()
                                            .position(new LatLng(Double.valueOf(driver.getLat()), Double.valueOf(driver.getLon()))).title(driver.getFirstName() + " " + driver.getLastName())
                                            .icon(smallMarkerIcon);
                                    mMap.addMarker(car);
                                } else {
                                    car = new MarkerOptions()
                                            .position(new LatLng(Double.valueOf(driver.getLat()), Double.valueOf(driver.getLon()))).title(driver.getUser_name())
                                            .icon(smallMarkerIcon);
                                    mMap.addMarker(car);
                                }
                            }
                        }
                    } else {

                    }
                } catch (Exception e) {
                    // Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception", "Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
            }

        });

    }

    public void AddDefaultMarker() {
        if (mMap != null) {
            mMap.clear();
            if (lineOptions != null)
                mMap.addPolyline(lineOptions);
            if (PickUpLatLng != null) {
                PicUpMarker.position(PickUpLatLng);
                mMap.addMarker(PicUpMarker);
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(getCameraPositionWithBearing(PickUpLatLng)));
            }
            if (DropOffLatLng != null) {
                DropOffMarker.position(DropOffLatLng);
                mMap.addMarker(DropOffMarker);
            }
        }
    }

    private CameraPosition getCameraPositionWithBearing(LatLng latLng) {
        return new CameraPosition.Builder().target(latLng).zoom(16).build();
    }

    private void onSelectCar(ModelCar car, String name) {
        carAmount = car.getTotal();
        if (car.getCarName().equals("Basic")) {
            serviceName = "basic_car";
            Log.e("fasdasfdsf", "car.getId() = " + car.getId());
            getNearDriver(car.getId(), "basic_car");
        } else if (car.getCarName().equals("Normal")) {
            serviceName = "normal_car";
            getNearDriver(car.getId(), "normal_car");
        } else if (car.getCarName().equals("Luxurious")) {
            serviceName = "luxurious_car";
            getNearDriver(car.getId(), "luxurious_car");
        } else if (car.getCarName().equals("Pool")) {
            serviceName = "pool_car";
            getNearDriver(car.getId(), "pool_car");
        }
        binding.tvRideDistance.setText(car.getDistance());
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);
        AddDefaultMarker();
    }

    @Override
    public void onRequestAccepted(ModelCurrentBooking data) {
        driverArrivedDialog(data);
    }

    @Override
    public void onRequestCancel() {

    }

    @Override
    public void onDriverNotFound() {

    }

    private HashMap<String, String> getBookingParam(String type) {
        HashMap<String, String> param = new HashMap<>();
        param.put("car_type_id", CarTypeID);
        param.put("user_id", modelLogin.getResult().getId());
        param.put("picuplocation", pickadd);
        param.put("dropofflocation", dropadd);
        param.put("picuplat", "" + PickUpLatLng.latitude);
        param.put("pickuplon", "" + PickUpLatLng.longitude);
        param.put("droplat", "" + DropOffLatLng.latitude);
        param.put("droplon", "" + DropOffLatLng.longitude);
        param.put("shareride_type", "");
        param.put("booktype", type);
        param.put("service_name", serviceName);
        param.put("status", "Now");
        param.put("passenger", "1");
        param.put("current_time", "" + ProjectUtil.getCurrentDateTime());
        param.put("timezone", "" + TimeZone.getDefault().getID());
        param.put("apply_code", "");
        param.put("payment_type", paymentType);
        param.put("vehical_type", "Reqular");
        param.put("picklatertime", bookTime);
        param.put("picklaterdate", bookDate);
        param.put("route_img", "");
        param.put("amount", carAmount);
        Log.e("param", param.toString().replace(", ", "&"));
        Log.e("param", "" + param);
        return param;
    }

    private boolean Validation() {
        if (CarTypeID.equals("")) {
            Toast.makeText(mContext, getString(R.string.select_car_type), Toast.LENGTH_SHORT).show();
            return false;
        } else if (binding.rbCash.isChecked()) {
            paymentType = "Cash";
            return true;
        } else if (binding.rbCard.isChecked()) {
            paymentType = "Card";
            return true;
        } else if (binding.rbWallet.isChecked()) {
            paymentType = "Wallet";
            return true;
        } else {
            Toast.makeText(mContext, getString(R.string.select_pay_text), Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void getCurrentBookingApi() {

        HashMap<String, String> param = new HashMap<>();
        param.put("user_id", modelLogin.getResult().getId());
        param.put("type", "USER");
        param.put("timezone", TimeZone.getDefault().getID());

        // Log.e("asdfasdfasf", "paramHash = " + getBookingParam());

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getCurrentBooking(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    Log.e("responseString", "responseString = " + responseString);

                    if (jsonObject.getString("status").equals("1")) {
                        Log.e("getCurrentBooking", "getCurrentBooking = " + responseString);
                        Type listType = new TypeToken<ModelCurrentBooking>() {
                        }.getType();
                        ModelCurrentBooking data = new GsonBuilder().create().fromJson(responseString, listType);
                        if (data.getStatus().equals(1)) {
                            ModelCurrentBookingResult result = data.getResult().get(0);
                            if (result.getStatus().equalsIgnoreCase("Pending")) {

                            } else if (result.getStatus().equalsIgnoreCase("Accept")) {
                                onRequestAccepted(data);
//                                Intent k = new Intent(mContext, TrackAct.class);
//                                k.putExtra("data", data);
//                                startActivity(k);
                            } else if (result.getStatus().equalsIgnoreCase("Arrived")) {
                                Intent j = new Intent(mContext, TrackAct.class);
                                j.putExtra("data", data);
                                startActivity(j);
                            } else if (result.getStatus().equalsIgnoreCase("Start")) {
                                Intent j = new Intent(mContext, TrackAct.class);
                                j.putExtra("data", data);
                                startActivity(j);
                            } else if (result.getStatus().equalsIgnoreCase("End")) {
                                Intent j = new Intent(mContext, PaymentAct.class);
                                j.putExtra("data", data);
                                startActivity(j);
                            }
                        }
                    } else {

                    }
                } catch (Exception e) {
                    // Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception", "Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
            }

        });
    }

    // Trigger new location updates at interval
    @SuppressLint("MissingPermission")
    protected void startLocationUpdates() {

        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        SettingsClient settingsClient = LocationServices.getSettingsClient(RideOptionAct.this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(RideOptionAct.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(RideOptionAct.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        getFusedLocationProviderClient(RideOptionAct.this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    Log.e("hdasfkjhksdf", "StartLocationUpdate = " + locationResult.getLastLocation());
                    currentLocation = locationResult.getLastLocation();
                    // currentlocation = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
                    // showMarkerCurrentLocation(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()));
                }
            }
        }, Looper.myLooper());

    }

}
