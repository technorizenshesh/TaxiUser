package com.taxiuser.activities;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.taxiuser.R;
import com.taxiuser.adapters.AdapterRecentsLocations;
import com.taxiuser.bottomsheet.ServListener;
import com.taxiuser.bottomsheet.ServiceTypeBottomSheet;
import com.taxiuser.bottomsheet.ServiceTypeListener;
import com.taxiuser.bottomsheet.ServicesBottomSheet;
import com.taxiuser.databinding.ActivityHomeBinding;
import com.taxiuser.databinding.AddressPickDialogFullscreenBinding;
import com.taxiuser.databinding.ContactUsDialogBinding;
import com.taxiuser.databinding.SupportDialogBinding;
import com.taxiuser.models.ModelAvailableDriver;
import com.taxiuser.models.ModelCurrentBooking;
import com.taxiuser.models.ModelCurrentBookingResult;
import com.taxiuser.models.ModelLogin;
import com.taxiuser.models.ModelRecentLocations;
import com.taxiuser.utils.AppConstant;
import com.taxiuser.utils.InternetConnection;
import com.taxiuser.utils.LatLngInterpolator;
import com.taxiuser.utils.MarkerAnimation;
import com.taxiuser.utils.MyApplication;
import com.taxiuser.utils.ProjectUtil;
import com.taxiuser.utils.SharedPref;
import com.taxiuser.utils.retrofitutils.Api;
import com.taxiuser.utils.retrofitutils.ApiFactory;
import com.taxiuser.utils.routes.DrawPollyLine;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeAct extends AppCompatActivity implements OnMapReadyCallback, ServListener, ServiceTypeListener {

    private static final int AUTOCOMPLETE_FROM_PLACE_CODE = 101;
    private static final int AUTOCOMPLETE_EDIT_WORK_PLACE_CODE = 103;
    private static final int AUTOCOMPLETE_EDIT_HOME_PLACE_CODE = 104;
    private static final int AUTOCOMPLETE_TO_PLACE_CODE = 102;
    Context mContext = HomeAct.this;
    ActivityHomeBinding binding;
    SupportMapFragment mapFragment;
    GoogleMap mMap;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    ModelRecentLocations modelRecentLocations;
    private Marker currentLocationMarker, dropLocationMarker;
    private LocationRequest mLocationRequest;
    private LatLng PickUpLatLng, DropOffLatLng, CurrentLatLon;
    private MarkerOptions PicUpMarker, DropOffMarker;
    private long UPDATE_INTERVAL = 4000;  /* 5 secs */
    private long FASTEST_INTERVAL = 4000; /* 2 sec */
    private Location currentLocation;
    String currentAddress;
    Dialog dialogFullscreen;
    String pickupAddress = "", dropOffAddress = "";
    AddressPickDialogFullscreenBinding dialogBinding;
    private FusedLocationProviderClient fusedLocationClient;
    private PolylineOptions lineOptions;
    private ScheduledExecutorService scheduleTaskExecutor;
    private String registerId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);

        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        lineOptions = new PolylineOptions();

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

        if (!InternetConnection.checkConnection(mContext)) {
            MyApplication.showConnectionDialog(mContext);
        }

        BindExecutor();

        itit();

        startLocationUpdates();

        getProfile();

    }

    private void itit() {

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(HomeAct.this);

        binding.chlidDashboard.tvActiveRides.setOnClickListener(v -> {
            startActivity(new Intent(mContext, ActiveBookingAct.class));
        });

        binding.chlidDashboard.tvWhereTo.setOnClickListener(v -> {
            if (modelRecentLocations != null) {
                openFullScreenAddressDialog(modelRecentLocations);
            } else {
                getRecentLocations();
            }
        });

        binding.chlidDashboard.ivGPS.setOnClickListener(v -> {
            if (currentLocation != null) {
                showMarkerCurrentLocation(CurrentLatLon);
            }
        });

        binding.chlidDashboard.tvAvailableDriversPool.setOnClickListener(v -> {
            startActivity(new Intent(mContext, AvailablePoolDriversAct.class)
                    .putExtra("lat", String.valueOf(currentLocation.getLatitude()))
                    .putExtra("lon", String.valueOf(currentLocation.getLongitude()))
            );
        });

        PicUpMarker = new MarkerOptions().title("Pick Up Location")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_marker));
        DropOffMarker = new MarkerOptions().title("Drop Off Location")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.red_marker));

        binding.childNavDrawer.tvEmail.setText(modelLogin.getResult().getEmail());
        binding.childNavDrawer.tvUsername.setText(modelLogin.getResult().getUser_name());

        Glide.with(mContext)
                .load(modelLogin.getResult().getImage())
                .into(binding.childNavDrawer.userImg);

        binding.childNavDrawer.signout.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            ProjectUtil.logoutAppDialog(mContext);
        });

        binding.childNavDrawer.tvChangeCurrency.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            changeCurrenyDialog();
        });

        binding.childNavDrawer.tvFAQ.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(mContext, FAQAct.class));
        });

        binding.childNavDrawer.tvChangeLang.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            changeLangDialog();
        });

        binding.childNavDrawer.tvDonate.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(mContext, DonateAct.class));
        });

        binding.childNavDrawer.tvLiveChat.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(mContext, ChatingAct.class));
        });

        binding.childNavDrawer.tvActiveRides.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(mContext, ActiveBookingAct.class));
        });

        binding.childNavDrawer.tvBusinessProfile.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(mContext, BusinessProfileAct.class));
        });

        binding.childNavDrawer.tvNotification.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(mContext, NotificationAct.class));
        });

        binding.childNavDrawer.tvPayment.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(mContext, CardDetailsAct.class));
        });

        binding.childNavDrawer.tvfavouriteDrivers.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(mContext, FavouriteDriverAct.class));
        });

        binding.childNavDrawer.tvEmergencyContact.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(mContext, EmergencyContactAct.class));
        });

        binding.chlidDashboard.navbar.setOnClickListener(v -> {
            binding.drawerLayout.openDrawer(GravityCompat.START);
        });

        binding.childNavDrawer.tvHome.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        });

        binding.childNavDrawer.tvProfile.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(mContext, MyProfileAct.class));
        });

        binding.childNavDrawer.tvPersonalDetails.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(mContext, UpdateProfileAct.class));
        });

        binding.childNavDrawer.ivSettings.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(mContext, MyProfileAct.class));
        });

        binding.childNavDrawer.tvEmergencyContact.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(mContext, EmergencyContactAct.class));
        });

//        binding.chlidDashboard.carPool.setOnClickListener(v -> {
//            startActivity(new Intent(mContext, Home2Activity.class)
//                    .putExtra("type", AppConstant.POOL)
//            );
//        });

//        binding.chlidDashboard.llBookNow.setOnClickListener(v -> {
//            startActivity(new Intent(mContext, Home2Activity.class)
//                    .putExtra("type", AppConstant.BOOK)
//            );
//        });

        binding.childNavDrawer.tvMessage.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(mContext, ChatListAct.class));
        });

        binding.childNavDrawer.tvContactUs.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            openContactUsDialog();
        });

        binding.childNavDrawer.tvWallet.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(mContext, WalletAct.class));
        });

        binding.childNavDrawer.tvRideHistory.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(mContext, RideHistoryAct.class));
        });

        binding.childNavDrawer.tvSupport.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            supportDialog();
        });

//        binding.childNavDrawer.tvPromo.setOnClickListener(v -> {
//            binding.drawerLayout.closeDrawer(GravityCompat.START);
//            startActivity(new Intent(mContext, PromocodesAct.class));
//        });

//        binding.childNavDrawer.tvActiveBooking.setOnClickListener(v -> {
//            binding.drawerLayout.closeDrawer(GravityCompat.START);
//            startActivity(new Intent(mContext, ActiveBookingAct.class));
//        });

        binding.childNavDrawer.tvChnageLang.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            //changeLangDialog();
        });

        binding.chlidDashboard.tvHome.setOnClickListener(v -> {
            try {
                DropOffLatLng = new LatLng(Double.parseDouble(modelLogin.getResult().getLat()),
                        Double.parseDouble(modelLogin.getResult().getLon()));
                if (PickUpLatLng != null & DropOffLatLng != null) {
                    Log.e("asdfasdasdas", "Pick Address = " + pickupAddress);

                    Log.e("asdfasdasdas", "modelLogin.getResult().getAddress() = " + modelLogin.getResult().getAddress());
                    dropOffAddress = modelLogin.getResult().getAddress();
                    DrawPolyLineNew();
                }
            } catch (Exception e) {
                Toast.makeText(mContext, getString(R.string.please_add_home_address), Toast.LENGTH_SHORT).show();
                openFullScreenAddressDialog(modelRecentLocations);
            }

        });

        binding.chlidDashboard.tvOffice.setOnClickListener(v -> {
            if (modelLogin.getResult().getWorkplace() == null ||
                    modelLogin.getResult().getWorkplace().equals("")) {
                Toast.makeText(mContext, getString(R.string.please_add_work_address), Toast.LENGTH_SHORT).show();
                openFullScreenAddressDialog(modelRecentLocations);
            } else {
                DropOffLatLng = new LatLng(Double.parseDouble(modelLogin.getResult().getWork_lat()),
                        Double.parseDouble(modelLogin.getResult().getWork_lon()));
                if (PickUpLatLng != null & DropOffLatLng != null) {
                    dropOffAddress = modelLogin.getResult().getWorkplace();
                    Log.e("asdfasdasdas", "Pick Address tvOffice = " + pickupAddress);
                    Log.e("asdfasdasdas", "drop Address tvOffice = " + dropOffAddress);
                    DrawPolyLineNew();
                }
            }
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
                finish();
                startActivity(new Intent(mContext, SplashAct.class));
                dialog.dismiss();
            } else if (radioUrdu.isChecked()) {
                ProjectUtil.updateResources(mContext, "ur");
                sharedPref.setlanguage("lan", "ur");
                finish();
                startActivity(new Intent(mContext, SplashAct.class));
                dialog.dismiss();
            } else if (radioArabic.isChecked()) {
                ProjectUtil.updateResources(mContext, "ar");
                sharedPref.setlanguage("lan", "ar");
                finish();
                startActivity(new Intent(mContext, SplashAct.class));
                dialog.dismiss();
            } else if (radioFrench.isChecked()) {
                ProjectUtil.updateResources(mContext, "fr");
                sharedPref.setlanguage("lan", "fr");
                finish();
                startActivity(new Intent(mContext, SplashAct.class));
                dialog.dismiss();
            } else if (radioChinese.isChecked()) {
                ProjectUtil.updateResources(mContext, "zh");
                sharedPref.setlanguage("lan", "zh");
                finish();
                startActivity(new Intent(mContext, SplashAct.class));
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private void supportDialog() {
        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        SupportDialogBinding dialogBinding = DataBindingUtil
                .inflate(LayoutInflater.from(mContext), R.layout.support_dialog, null, false);
        dialog.setContentView(dialogBinding.getRoot());

        dialogBinding.ivBack.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialogBinding.llEmergencyContact.setOnClickListener(v -> {
            startActivity(new Intent(mContext, EmergencyContactAct.class));
        });

        dialogBinding.llContactUs.setOnClickListener(v -> {
            openContactUsDialog();
        });

        dialogBinding.llAboutUs.setOnClickListener(v -> {
            startActivity(new Intent(mContext, WebviewAct.class)
                    .putExtra("url", "https://technorizen.com/australia_taxi/about_us.html")
            );
        });

        dialogBinding.llTermsAndCondition.setOnClickListener(v -> {
            startActivity(new Intent(mContext, WebviewAct.class)
                    .putExtra("url", "https://technorizen.com/australia_taxi/terms&condition.html")
            );
        });

        dialogBinding.llPrivacyPolicy.setOnClickListener(v -> {
            startActivity(new Intent(mContext, WebviewAct.class)
                    .putExtra("url", "https://technorizen.com/australia_taxi/privacy-policy.html")
            );
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

    private void getActiveRidesCount() {

        HashMap<String, String> paramHash = new HashMap<>();
        paramHash.put("user_id", modelLogin.getResult().getId());

        Log.e("sadasddasd", "paramHash = " + paramHash);

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getScheduleBookingCount(paramHash);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String stringResponse = response.body().string();

                    try {

                        JSONObject jsonObject = new JSONObject(stringResponse);

                        if (jsonObject.getString("status").equals("1")) {

                            Log.e("asfddasfasdf", "response = " + response);
                            Log.e("asfddasfasdf", "stringResponse = " + stringResponse);

                            binding.chlidDashboard.tvActiveRides.setText(getString(R.string.you_have) + " " + jsonObject.getString("result")
                                    + " " + getString(R.string.active_rides));

                        } else {
                            binding.chlidDashboard.tvActiveRides.setText(getString(R.string.you_have_no_ride));
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

    private void BindExecutor() {
        scheduleTaskExecutor = Executors.newScheduledThreadPool(5);
        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                getNearDriver();
            }
        }, 0, 8, TimeUnit.MINUTES);
    }

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

        SettingsClient settingsClient = LocationServices.getSettingsClient(HomeAct.this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(HomeAct.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(HomeAct.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        getFusedLocationProviderClient(HomeAct.this)
                .requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        if (locationResult != null) {
                            Log.e("hdasfkjhksdf", "StartLocationUpdate = " + locationResult.getLastLocation());
                            currentLocation = locationResult.getLastLocation();
                            Location location = new Location("");
                            location.setLatitude(currentLocation.getLatitude());
                            location.setLongitude(currentLocation.getLongitude());
                            CurrentLatLon = new LatLng(location.getLatitude(), location.getLongitude());
                            showMarkerCurrentLocation(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
                        }
                    }
                }, Looper.myLooper());

    }

    private void showDropOffMarker(@NonNull LatLng currentLocation) {
        if (currentLocation != null) {
            if (dropLocationMarker == null) {
                if (mMap != null) {
                    Log.e("gdfgdfsdfdf", "Map Andar = " + currentLocation);
                    dropLocationMarker = mMap.addMarker(new MarkerOptions().position(currentLocation).title("My Location"));
                    animateCamera(currentLocation);
                }
            } else {
                Log.e("gdfgdfsdfdf", "Map Andar else = " + currentLocation);
                Log.e("gdfgdfsdfdf", "Hello Marker Anuimation");
                animateCamera(currentLocation);
                MarkerAnimation.animateMarkerToGB(currentLocationMarker, currentLocation, new LatLngInterpolator.Spherical());
            }
        }
    }

    private void showMarkerCurrentLocation(@NonNull LatLng currentLocation) {

        Log.e("asdfasfasf", "Location Akash = " + currentLocation);
        Log.e("asdfasfasf", "currentLocation = " + currentLocation);
        Log.e("asdfasfasf", "currentLocationMarker = " + currentLocationMarker);

        if (currentLocation != null) {
            if (currentLocationMarker == null) {
                if (mMap != null) {
                    Log.e("gdfgdfsdfdf", "Map Andar = " + currentLocation);
                    currentAddress = ProjectUtil.getCompleteAddressString(mContext, currentLocation.latitude, currentLocation.longitude);
                    currentLocationMarker = mMap.addMarker(new MarkerOptions().position(currentLocation).title(currentAddress));
                    PickUpLatLng = currentLocation;
                    pickupAddress = currentAddress;
                    animateCamera(currentLocation);
                }
            } else {
                Log.e("gdfgdfsdfdf", "Map Andar else = " + currentLocation);
                Log.e("gdfgdfsdfdf", "Hello Marker Anuimation");
                animateCamera(currentLocation);
                MarkerAnimation.animateMarkerToGB(currentLocationMarker, currentLocation, new LatLngInterpolator.Spherical());
            }
        }

    }

    private void DrawPolyLine() {
        DrawPollyLine.get(this).setOrigin(PickUpLatLng)
                .setDestination(DropOffLatLng)
                .execute(new DrawPollyLine.onPolyLineResponse() {
                    @Override
                    public void Success(ArrayList<LatLng> latLngs) {
                        mMap.clear();
                        lineOptions = new PolylineOptions();
                        lineOptions.addAll(latLngs);
                        lineOptions.width(10);
                        lineOptions.color(Color.BLUE);
                        AddDefaultMarker();
                        gotoRideOption();
                    }
                });
    }

    private void DrawPolyLineNew() {
        DrawPollyLine.get(this).setOrigin(PickUpLatLng)
                .setDestination(DropOffLatLng)
                .execute(new DrawPollyLine.onPolyLineResponse() {
                    @Override
                    public void Success(ArrayList<LatLng> latLngs) {
                        mMap.clear();
                        lineOptions = new PolylineOptions();
                        lineOptions.addAll(latLngs);
                        lineOptions.width(10);
                        lineOptions.color(Color.BLUE);
                        AddDefaultMarker();
                        gotoRideOptionOnClick();
                    }
                });
    }

    private void gotoRideOption() {
        Log.e("asdfasdasdas", "gotoRideOption Pick Address tvOffice = " + pickupAddress);
        Log.e("asdfasdasdas", "gotoRideOption drop Address tvOffice = " + dropOffAddress);
        dialogFullscreen.dismiss();
        currentLocationMarker = null;
        mMap.clear();
        //   Intent intent = new Intent(this, RideOptionAct.class);
        //   intent.putExtra("pollyLine", lineOptions);
        //   intent.putExtra("PickUp", PickUpLatLng);
        //   intent.putExtra("DropOff", DropOffLatLng);
        //   intent.putExtra("picadd", pickupAddress);
        //   intent.putExtra("dropadd", dropOffAddress);
        //   intent.putExtra("addressName",ProjectUtil.getCompleteAddressString(HomeAct.this,DropOffLatLng.latitude,DropOffLatLng.longitude));
        //   startActivity(intent);
        new ServicesBottomSheet(ProjectUtil.getAddress(mContext, DropOffLatLng.latitude, DropOffLatLng.longitude), PickUpLatLng.latitude + "", PickUpLatLng.longitude + "", DropOffLatLng.latitude + ""
                , DropOffLatLng.longitude + "").callBack(this::onService).show(getSupportFragmentManager(), "");

    }

    private void gotoRideOptionOnClick() {
        Log.e("asdfasdasdas", "gotoRideOptionOnClick Pick Address tvOffice = " + pickupAddress);
        Log.e("asdfasdasdas", "gotoRideOptionOnClick drop Address tvOffice = " + dropOffAddress);
        currentLocationMarker = null;
        mMap.clear();
        //   Intent intent = new Intent(this, RideOptionAct.class);
        //   intent.putExtra("pollyLine", lineOptions);
        //     intent.putExtra("PickUp", PickUpLatLng);
        //    intent.putExtra("DropOff", DropOffLatLng);
        //    intent.putExtra("picadd", pickupAddress);
        //     intent.putExtra("dropadd", dropOffAddress);
        //     startActivity(intent);

        new ServicesBottomSheet(ProjectUtil.getAddress(mContext, DropOffLatLng.latitude, DropOffLatLng.longitude), PickUpLatLng.latitude + "", PickUpLatLng.longitude + "", DropOffLatLng.latitude + ""
                , DropOffLatLng.longitude + "").callBack(this::onService).show(getSupportFragmentManager(), "");


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
        zoomMapInitial(PickUpLatLng, DropOffLatLng);
    }

    protected void zoomMapInitial(LatLng finalPlace, LatLng currenLoc) {
        try {
            int padding = 200;
            LatLngBounds.Builder bc = new LatLngBounds.Builder();
            bc.include(finalPlace);
            bc.include(currenLoc);
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bc.build(), padding));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @NonNull
    private CameraPosition getCameraPositionWithBearing(LatLng latLng) {
        return new CameraPosition.Builder().target(latLng).zoom(16).build();
    }

    private void animateCamera(@NonNull LatLng location) {
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(getCameraPositionWithBearing(location)));
    }

    private void openFullScreenAddressDialog(ModelRecentLocations modelRecentLocations) {

        dialogFullscreen = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);

        dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                R.layout.address_pick_dialog_fullscreen, null, false);
        dialogFullscreen.setContentView(dialogBinding.getRoot());

        if (currentAddress != null) {
            dialogBinding.tvFrom.setText(currentAddress);
        }

        if (modelRecentLocations != null) {
            AdapterRecentsLocations adapterRecentsLocations = new AdapterRecentsLocations(mContext, modelRecentLocations.getResult());
            dialogBinding.locationList.setAdapter(adapterRecentsLocations);
        }

        dialogBinding.tvHomeAddress.setText(modelLogin.getResult().getAddress());

        try {
            Double.parseDouble(modelLogin.getResult().getLat());
        } catch (Exception e) {
            dialogBinding.tvHomeAddress.setText("");
        }

        try {
            dialogBinding.tvHomeAddress.setOnClickListener(v -> {
                try {
                    DropOffLatLng = new LatLng(Double.parseDouble(modelLogin.getResult().getLat()),
                            Double.parseDouble(modelLogin.getResult().getLon()));
                } catch (Exception e) {
                    // MyApplication.showAlert(mContext, getString(R.string.please_edit_your_home_add));
                }
                if (PickUpLatLng != null & DropOffLatLng != null) {
                    pickupAddress = dialogBinding.tvFrom.getText().toString().trim();
                    dropOffAddress = dialogBinding.tvHomeAddress.getText().toString().trim();
                    DrawPolyLineNew();
                }
            });
        } catch (Exception e) {
            dialogBinding.tvHomeAddress.setText("");
        }

        dialogBinding.tvWorkAddress.setText(modelLogin.getResult().getWorkplace());

        dialogBinding.ivEditWork.setOnClickListener(v -> {
            Log.e("modelLogin", "Ayya click pe ");
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(this);
            startActivityForResult(intent, AUTOCOMPLETE_EDIT_WORK_PLACE_CODE);
        });


        dialogBinding.tvWorkAddress.setOnClickListener(v -> {
            if (modelLogin.getResult().getWorkplace() == null ||
                    modelLogin.getResult().getWorkplace().equals("")) {
                Toast.makeText(mContext, getString(R.string.please_add_work_address), Toast.LENGTH_SHORT).show();
            } else {
                DropOffLatLng = new LatLng(Double.parseDouble(modelLogin.getResult().getWork_lat()),
                        Double.parseDouble(modelLogin.getResult().getWork_lon()));
                if (PickUpLatLng != null & DropOffLatLng != null) {
                    pickupAddress = dialogBinding.tvFrom.getText().toString().trim();
                    dropOffAddress = dialogBinding.tvWorkAddress.getText().toString().trim();
                    DrawPolyLineNew();
                }
            }
        });

        dialogBinding.ivEditHome.setOnClickListener(v -> {
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(this);
            startActivityForResult(intent, AUTOCOMPLETE_EDIT_HOME_PLACE_CODE);
        });

        dialogBinding.tvFrom.setOnClickListener(v -> {
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(this);
            startActivityForResult(intent, AUTOCOMPLETE_FROM_PLACE_CODE);
        });

        dialogBinding.ivCancel.setOnClickListener(v -> {
            dialogFullscreen.dismiss();

        });

        dialogBinding.tvDone.setOnClickListener(v -> {
            if (TextUtils.isEmpty(dialogBinding.tvFrom.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_select_pickup_loc), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(dialogBinding.tvDestination.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_select_drop_loc), Toast.LENGTH_SHORT).show();
            } else {
                addRecentLocations(dialogBinding.tvDestination.getText().toString().trim());
            }
        });

        dialogBinding.tvSetCurrentLoc.setOnClickListener(v -> {
            if (CurrentLatLon != null) {
                showMarkerCurrentLocation(CurrentLatLon);
                dialogFullscreen.dismiss();
            }
        });

        dialogBinding.tvDestination.setOnClickListener(v -> {
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(this);
            startActivityForResult(intent, AUTOCOMPLETE_TO_PLACE_CODE);
        });

        dialogFullscreen.show();

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
                        Log.e("getProfilegetProfile", "getProfile = " + stringResponse);
                        JSONObject jsonObject = new JSONObject(stringResponse);
                        if (jsonObject.getString("status").equals("1")) {

                            modelLogin = new Gson().fromJson(stringResponse, ModelLogin.class);
                            sharedPref.setUserDetails(AppConstant.USER_DETAILS, modelLogin);

                            AppConstant.CURRENCY = modelLogin.getResult().getConverter_currency().trim();

                            if (modelLogin.getResult().getConverter_currency().equals("AUD")) {
                                AppConstant.CURRENT_CURRENCY_VALUE = 1.0;
                            } else {
                                AppConstant.CURRENT_CURRENCY_VALUE = Double.parseDouble(modelLogin.getResult().getCurrency_amount());
                            }

                            Log.e("adadadasd", "modelLogin.getResult().getRegister_id() = " + modelLogin.getResult().getRegister_id());
                            Log.e("adadadasd", "registerId = " + registerId);

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

    private void updateCurrency(String currentCurrency, String changeCurrency) {
        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));

        HashMap<String, String> paramHash = new HashMap<>();
        paramHash.put("user_id", modelLogin.getResult().getId());
        paramHash.put("base_currency", "AUD");
        paramHash.put("converter_currency", changeCurrency);

        Log.e("updateCurrency", "updateCurrency = " + paramHash);

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.updateCurrencyApiCall(paramHash);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {

                    String stringResponse = response.body().string();

                    try {

                        JSONObject jsonObject = new JSONObject(stringResponse);

                        if (jsonObject.getString("status").equals("1")) {

                            Log.e("updateCurrency", "updateCurrency = " + stringResponse);

                            finishAffinity();
                            startActivity(new Intent(mContext, SplashAct.class));
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

    private void changeCurrenyDialog() {
        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setContentView(R.layout.change_currency_dialog);
        dialog.setCancelable(true);

        Button btContinue = dialog.findViewById(R.id.btContinue);
        RadioButton radioUSD = dialog.findViewById(R.id.radioUSD);
        RadioButton radioAUD = dialog.findViewById(R.id.radioAUD);
        RadioButton radioSAR = dialog.findViewById(R.id.radioSAR);
        RadioButton radioAED = dialog.findViewById(R.id.radioAED);
        RadioButton radioGBP = dialog.findViewById(R.id.radioGBP);
        RadioButton radioPKR = dialog.findViewById(R.id.radioPKR);

        if (AppConstant.CURRENCY.equals("USD")) {
            radioUSD.setChecked(true);
        } else if (AppConstant.CURRENCY.equals("AUD")) {
            radioAUD.setChecked(true);
        } else if (AppConstant.CURRENCY.equals("SAR")) {
            radioSAR.setChecked(true);
        } else if (AppConstant.CURRENCY.equals("AED")) {
            radioAED.setChecked(true);
        } else if (AppConstant.CURRENCY.equals("GBP")) {
            radioGBP.setChecked(true);
        } else if (AppConstant.CURRENCY.equals("PKR")) {
            radioPKR.setChecked(true);
        } else {
            radioAUD.setChecked(true);
        }

        dialog.getWindow().setBackgroundDrawableResource(R.color.translucent_black);

        btContinue.setOnClickListener(v -> {
            if (radioUSD.isChecked()) {
                updateCurrency(AppConstant.CURRENCY, "USD");
            } else if (radioAUD.isChecked()) {
                updateCurrency(AppConstant.CURRENCY, "AUD");
            } else if (radioSAR.isChecked()) {
                updateCurrency(AppConstant.CURRENCY, "SAR");
            } else if (radioAED.isChecked()) {
                updateCurrency(AppConstant.CURRENCY, "AED");
            } else if (radioGBP.isChecked()) {
                updateCurrency(AppConstant.CURRENCY, "GBP");
            } else if (radioPKR.isChecked()) {
                updateCurrency(AppConstant.CURRENCY, "PKR");
            }
        });

        dialog.show();

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

    private void addRecentLocations(String add) {

        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));

        HashMap<String, String> paramHash = new HashMap<>();
        paramHash.put("user_id", modelLogin.getResult().getId());
        paramHash.put("address", add);
        paramHash.put("lat", String.valueOf(DropOffLatLng.latitude));
        paramHash.put("lon", String.valueOf(DropOffLatLng.longitude));

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.addRecentLocationApi(paramHash);
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
                            Toast.makeText(mContext, getString(R.string.deti_is_added_in_recent), Toast.LENGTH_LONG).show();
                            getRecentLocationNew();
                            dialogFullscreen.dismiss();
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

    private void updateUserAddresses() {

        HashMap<String, String> paramHash = new HashMap<>();
        paramHash.put("user_id", modelLogin.getResult().getId());
        paramHash.put("lat", modelLogin.getResult().getLat());
        paramHash.put("lon", modelLogin.getResult().getLon());
        paramHash.put("address", modelLogin.getResult().getAddress());
        paramHash.put("work_lat", modelLogin.getResult().getWork_lat());
        paramHash.put("work_lon", modelLogin.getResult().getWork_lon());
        paramHash.put("workplace", modelLogin.getResult().getWorkplace());

        Log.e("sdfdsfdsfs", "paramHash = " + paramHash);

        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.updateUserAddressApi(paramHash);
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
                            Toast.makeText(mContext, getString(R.string.success), Toast.LENGTH_SHORT).show();
                        } else {
                        }
                    } catch (Exception e) {
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

    private void getRecentLocationNew() {
        HashMap<String, String> paramHash = new HashMap<>();
        paramHash.put("user_id", modelLogin.getResult().getId());

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getRecentLocationApi(paramHash);
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
                            modelRecentLocations = new Gson().fromJson(stringResponse, ModelRecentLocations.class);
                            // openFullScreenAddressDialog(modelRecentLocations);
                        } else {
                            // openFullScreenAddressDialog(modelRecentLocations);
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

    private void getRecentLocations() {
        ProjectUtil.showProgressDialog(mContext, true, getString(R.string.please_wait));

        HashMap<String, String> paramHash = new HashMap<>();
        paramHash.put("user_id", modelLogin.getResult().getId());

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getRecentLocationApi(paramHash);
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
                            modelRecentLocations = new Gson().fromJson(stringResponse, ModelRecentLocations.class);
                            openFullScreenAddressDialog(modelRecentLocations);
                        } else {
                            openFullScreenAddressDialog(modelRecentLocations);
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AUTOCOMPLETE_FROM_PLACE_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                PickUpLatLng = place.getLatLng();
                try {
                    String addresses = ProjectUtil.getCompleteAddressString(mContext, place.getLatLng().latitude, place.getLatLng().longitude);
                    dialogBinding.tvFrom.setText(addresses);
                    pickupAddress = addresses;
                } catch (Exception e) {
                }
                if (PickUpLatLng != null & DropOffLatLng != null) {
                    DrawPolyLine();
                }
            }
        } else if (requestCode == AUTOCOMPLETE_TO_PLACE_CODE) {
            if (resultCode == RESULT_OK) {

                Place place = Autocomplete.getPlaceFromIntent(data);
                DropOffLatLng = place.getLatLng();

                try {
                    String addresses = ProjectUtil.getCompleteAddressString(mContext, place.getLatLng().latitude, place.getLatLng().longitude);
                    dialogBinding.tvDestination.setText(addresses);
                    dropOffAddress = addresses;
                } catch (Exception e) {
                }

                if (PickUpLatLng != null & DropOffLatLng != null) {
                    DrawPolyLine();
                }
            }
        } else if (requestCode == AUTOCOMPLETE_EDIT_WORK_PLACE_CODE) {
            if (resultCode == RESULT_OK) {

                Place place = Autocomplete.getPlaceFromIntent(data);
                // DropOffLatLng = place.getLatLng();
                Log.e("modelLogin", "ayya ander = AUTOCOMPLETE_EDIT_WORK_PLACE_CODE");
                try {
                    String addresses = ProjectUtil.getCompleteAddressString(mContext, place.getLatLng().latitude, place.getLatLng().longitude);
                    dialogBinding.tvWorkAddress.setText(addresses);
                    modelLogin.getResult().setWorkplace(addresses);
                    modelLogin.getResult().setWork_lat(String.valueOf(place.getLatLng().latitude));
                    modelLogin.getResult().setWork_lon(String.valueOf(place.getLatLng().longitude));
                    sharedPref.setUserDetails(AppConstant.USER_DETAILS, modelLogin);
                    Log.e("modelLogin", "modelLogin = " + addresses);
                    updateUserAddresses();
                } catch (Exception e) {
                }
            }
        } else if (requestCode == AUTOCOMPLETE_EDIT_HOME_PLACE_CODE) {
            if (resultCode == RESULT_OK) {

                Place place = Autocomplete.getPlaceFromIntent(data);
                // DropOffLatLng = place.getLatLng();

                try {
                    String addresses = ProjectUtil.getCompleteAddressString(mContext, place.getLatLng().latitude, place.getLatLng().longitude);
                    dialogBinding.tvHomeAddress.setText(addresses);
                    modelLogin.getResult().setAddress(addresses);
                    modelLogin.getResult().setLat(String.valueOf(place.getLatLng().latitude));
                    modelLogin.getResult().setLon(String.valueOf(place.getLatLng().longitude));
                    sharedPref.setUserDetails(AppConstant.USER_DETAILS, modelLogin);
                    updateUserAddresses();
                } catch (Exception e) {
                }
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        getProfile();
        getActiveRidesCount();
        getNearDriver();
        getRecentLocationNew();
        startLocationUpdates();
        BindExecutor();
        getCurrentBooking();
    }

    private void getCurrentBooking() {

        HashMap<String, String> param = new HashMap<>();
        param.put("user_id", modelLogin.getResult().getId());
        param.put("type", "USER");
        param.put("timezone", TimeZone.getDefault().getID());

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
                                startActivity(k);
                            } else if (result.getStatus().equalsIgnoreCase("Arrived")) {
                                Intent j = new Intent(mContext, TrackAct.class);
                                j.putExtra("data", data);
                                startActivity(j);
                            } else if (result.getStatus().equalsIgnoreCase("Start")) {
                                Intent j = new Intent(mContext, TrackAct.class);
                                j.putExtra("data", data);
                                startActivity(j);
                            } else if (result.getStatus().equalsIgnoreCase("End")) {
                                if ("Success".equals(result.getPayment_status())) {
                                    if (null == result.getUserRatingStatus() ||
                                            "".equals(result.getUserRatingStatus())) {
                                        Intent j = new Intent(mContext, DriverFeedbackAct.class);
                                        j.putExtra("data", data);
                                        startActivity(j);
                                    }
                                } else {
                                    Intent j = new Intent(mContext, EndUserAct.class);
                                    j.putExtra("data", data);
                                    startActivity(j);
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

    private void getNearDriver() {

        HashMap<String, String> param = new HashMap<>();
        try {
            param.put("latitude", "" + PickUpLatLng.latitude);
        } catch (Exception e) {
        }

        try {
            param.put("longitude", "" + PickUpLatLng.longitude);
        } catch (Exception e) {
        }

        param.put("user_id", modelLogin.getResult().getId());
        param.put("timezone", TimeZone.getDefault().getID());

        Log.e("user_iduser_id", "param = " + param);

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getAvailableCarDriversHome(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    Log.e("responseString", "responseString = " + responseString);

                    if (jsonObject.getString("status").equals("1")) {
                        Type listType = new TypeToken<ArrayList<ModelAvailableDriver>>() {
                        }.getType();
                        ArrayList<ModelAvailableDriver> drivers = new GsonBuilder().create().fromJson(jsonObject.getString("result"), listType);
                        if (mMap != null) {
                            AddDefaultMarker();
                            for (ModelAvailableDriver driver : drivers) {
                                int height = 95;
                                int width = 65;
                                Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.car_top);
                                Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                                BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);
                                MarkerOptions car = new MarkerOptions()
                                        .position(new LatLng(Double.valueOf(driver.getLat()), Double.valueOf(driver.getLon()))).title(driver.getUser_name())
                                        .icon(smallMarkerIcon);
                                mMap.addMarker(car);
                            }
                        }
                    } else {
                        mMap.clear();
                        PicUpMarker.position(PickUpLatLng);
                        mMap.addMarker(PicUpMarker);
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(getCameraPositionWithBearing(PickUpLatLng)));
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

    @Override
    protected void onPause() {
        super.onPause();
        scheduleTaskExecutor.shutdownNow();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        scheduleTaskExecutor.shutdownNow();
    }

    @Override
    public void onBackPressed() {
        ProjectUtil.exitAppDialog(mContext);
    }

    @Override
    public void onService(int position, String id) {
        new ServiceTypeBottomSheet(id).callBack(this::onType).show(getSupportFragmentManager(), "");
    }

    @Override
    public void onType(String type, String service) {
        Log.e("Selected valuesss==", type + "=====" + service);
        Intent intent = new Intent(this, RideOptionAct.class);
        intent.putExtra("pollyLine", lineOptions);
        intent.putExtra("PickUp", PickUpLatLng);
        intent.putExtra("DropOff", DropOffLatLng);
        intent.putExtra("picadd", pickupAddress);
        intent.putExtra("dropadd", dropOffAddress);
        intent.putExtra("addressName", ProjectUtil.getCompleteAddressString(HomeAct.this, DropOffLatLng.latitude, DropOffLatLng.longitude));
        intent.putExtra("service_id",service);
        intent.putExtra("service_type",type);
        startActivity(intent);
    }
}


