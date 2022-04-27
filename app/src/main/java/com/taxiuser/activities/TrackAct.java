package com.taxiuser.activities;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.taxiuser.R;
import com.taxiuser.databinding.ActivityTrackBinding;
import com.taxiuser.databinding.DialogDriverArrivedDialogBinding;
import com.taxiuser.databinding.TripStatusDialogNewBinding;
import com.taxiuser.models.ModelCurrentBooking;
import com.taxiuser.models.ModelCurrentBookingResult;
import com.taxiuser.models.ModelLogin;
import com.taxiuser.utils.AppConstant;
import com.taxiuser.utils.LatLngInterpolator;
import com.taxiuser.utils.MarkerAnimation;
import com.taxiuser.utils.ProjectUtil;
import com.taxiuser.utils.SharedPref;
import com.taxiuser.utils.retrofitutils.Api;
import com.taxiuser.utils.retrofitutils.ApiFactory;
import com.taxiuser.utils.routes.DrawPollyLine;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrackAct extends AppCompatActivity implements OnMapReadyCallback {

    Context mContext = TrackAct.this;
    ActivityTrackBinding binding;
    private String status;
    private ModelCurrentBooking data;
    private ModelCurrentBookingResult result;
    private ModelLogin.Result driverDetails;
    private LatLng PicLatLon, DropLatLon;
    private GoogleMap mMap;
    private MarkerOptions DriverMarker;
    private MarkerOptions DropOffMarker;
    private Marker driverMarkerCar;
    Timer timer = null;
    double bearing = 0.0;
    String driverId = "", usermobile = "";
    SharedPref sharedPref;
    ModelLogin modelLogin;
    boolean isMarkerZoom = false;
    private Marker pCurrentLocationMarker;
    private Marker dcurrentLocationMarker;

    BroadcastReceiver statusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("gdsfdsfdsf", "statusReceiver");
            if (intent.getAction().equals("Job_Status_Action")) {
                if (intent.getStringExtra("status").equals("Cancel")) {
                    finish();
                    Toast.makeText(mContext, "Your ride has been cancelled by driver", Toast.LENGTH_SHORT).show();
                } else if (intent.getStringExtra("status").equals("START")) {
                    waitingDialog();
                } else {
                    getCurrentBooking();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_track);

        status = getIntent().getStringExtra("status");

        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);

        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                getDriverLocation();
            }
        }, 0, 5000);

        if (getIntent() != null) {

            data = (ModelCurrentBooking) getIntent().getSerializableExtra("data");
            result = data.getResult().get(0);
            driverId = result.getDriverId();
            driverDetails = result.getDriver_details().get(0);
            usermobile = driverDetails.getMobile();
            PicLatLon = new LatLng(Double.parseDouble(result.getPicuplat()), Double.parseDouble(result.getPickuplon()));

            try {
                DropLatLon = new LatLng(Double.parseDouble(result.getDroplat()), Double.parseDouble(result.getDroplon()));
            } catch (Exception e) {
            }

            if (driverDetails.getProfile_image() != null) {
                Glide.with(mContext).load(driverDetails.getProfile_image())
                        .placeholder(R.drawable.user_ic).into(binding.driverImage);
            }

        }

        itit();

    }

    private void itit() {

        if (result.getStatus().equalsIgnoreCase("Arrived")) {
            tripStatusDialog("Driver is arrived at pickup location", result.getStatus(), data);
        } else if (result.getWaiting_status().equalsIgnoreCase("Start")) {
            tripStatusDialog("Driver ", result.getStatus(), data);
        } else if (result.getStatus().equalsIgnoreCase("Start")) {
            if (result.getWaiting_time().equalsIgnoreCase("start")) {
                waitingDialog();
            } else {
                tripStatusDialog("Trip is started", result.getStatus(), data);
            }
        } else if (result.getStatus().equalsIgnoreCase("End")) {
            tripStatusDialog("Trip is ended", result.getStatus(), data);
        } else if (result.getStatus().equalsIgnoreCase("Finish")) {
            tripStatusDialog("Trip is Finished", result.getStatus(), data);
        }

        binding.tvCarNumber.setText(driverDetails.getCar_number());
        binding.tvName.setText(driverDetails.getUser_name());

        if ("basic_car".equals(result.getService_name())) {
            binding.tvCarName.setText("Basic");
        } else if ("normal_car".equals(result.getService_name())) {
            binding.tvCarName.setText("Normal");
        } else if ("luxurious_car".equals(result.getService_name())) {
            binding.tvCarName.setText("Luxurious");
        } else if ("pool_car".equals(result.getService_name())) {
            binding.tvCarName.setText("Pool");
        } else {
            binding.tvCarName.setText("Basic");
        }

        binding.tvPrice.setText(AppConstant.CURRENCY + " " + (int) (Double.parseDouble(result.getAmount()) * AppConstant.CURRENT_CURRENCY_VALUE));
        binding.tvTime.setText(result.getEstimateTime() + " Min");

        binding.btnBack.setOnClickListener(v -> {
            finish();
        });

        binding.btnRate.setOnClickListener(v -> {
            finish();
        });

        binding.icCall.setOnClickListener(v -> {
            ProjectUtil.call(mContext, usermobile);
        });

        binding.ivCancelTrip.setOnClickListener(v -> {
            startActivity(new Intent(mContext, RideCancellationAct.class)
                    .putExtra("id", result.getId())
            );
        });

    }

    @Override
    protected void onResume() {
        getCurrentBooking();
        registerReceiver(statusReceiver, new IntentFilter("Job_Status_Action"));
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(statusReceiver);
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

                    Log.e("getCurrentBooking", "responseString = " + responseString);

                    if (jsonObject.getString("status").equals("1")) {
                        Log.e("getCurrentBooking", "getCurrentBooking = " + responseString);
                        Type listType = new TypeToken<ModelCurrentBooking>() {
                        }.getType();
                        ModelCurrentBooking data = new GsonBuilder().create().fromJson(responseString, listType);
                        if (data.getStatus().equals(1)) {
                            ModelCurrentBookingResult result = data.getResult().get(0);
                            if (result.getStatus().equalsIgnoreCase("Arrived")) {
//                              binding.driverOtp.setVisibility(View.VISIBLE);
//                              binding.driverOtp.setText("Give this Otp " + result.getOtp() + " to driver ");
                                binding.titler.setText("Driver is arrived");
                                tripStatusDialog("Driver is arrived at pickup location", result.getStatus(), data);
                            } else if (result.getStatus().equalsIgnoreCase("Start")) {
                                if (result.getWaiting_status().equalsIgnoreCase("Start")) {
                                    waitingDialog();
                                } else {
                                    tripStatusDialog("Trip is started", result.getStatus(), data);
                                }
                                binding.titler.setText("Trip is started");
                            } else if (result.getStatus().equalsIgnoreCase("End")) {
                                binding.titler.setText("Trip is ended");
                                tripStatusDialog("Trip is ended", result.getStatus(), data);
                            } else if (result.getStatus().equalsIgnoreCase("Finish")) {
                                binding.titler.setText("Trip is Finished");
                                tripStatusDialog("Trip is Finished", result.getStatus(), data);
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

    private void getCurrentBookingForCode() {

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

                        Type listType = new TypeToken<ModelCurrentBooking>() {
                        }.getType();

                        ModelCurrentBooking data = new GsonBuilder().create().fromJson(responseString, listType);

                        if (data.getStatus().equals(1)) {
                            ModelCurrentBookingResult result = data.getResult().get(0);
                            if (result.getStatus().equalsIgnoreCase("Arrived")) {
//                                binding.driverOtp.setVisibility(View.VISIBLE);
//                                binding.driverOtp.setText("Give this Otp " + result.getOtp() + " to driver ");
                                binding.titler.setText("Driver is arrived");
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

    private void tripStatusDialog(String text, String status, ModelCurrentBooking data) {

        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);

        dialog.setCancelable(false);
        TripStatusDialogNewBinding dialogNewBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext)
                , R.layout.trip_status_dialog_new, null, false);
        dialogNewBinding.tvMessage.setText(text);

        dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);

        dialogNewBinding.tvOk.setOnClickListener(v -> {
            if ("End".equals(status)) {
                Intent j = new Intent(mContext, EndUserAct.class);
                j.putExtra("data", data);
                startActivity(j);
                finish();
            } else if ("Finish".equals(status)) {
                finishAffinity();
                startActivity(new Intent(mContext, HomeAct.class));
            }
            dialog.dismiss();
        });

        dialog.setContentView(dialogNewBinding.getRoot());

        dialog.show();

    }

    private void waitingDialog() {

        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);

        dialog.setCancelable(false);
        TripStatusDialogNewBinding dialogNewBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext)
                , R.layout.trip_status_dialog_new, null, false);
        dialogNewBinding.tvMessage.setText(R.string.hold_by_driver);

        dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);

        dialogNewBinding.tvOk.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.setContentView(dialogNewBinding.getRoot());

        dialog.show();

    }

    private void getDriverLocation() {

        HashMap<String, String> param = new HashMap<>();
        param.put("user_id", driverId);

        Log.e("paramparam", "param = " + param);

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getLatLonDriver(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);
                    Log.e("responseString", "responseString = " + responseString);

                    if (jsonObject.getString("status").equals("1")) {
                        double lat = Double.parseDouble(jsonObject.getString("lat"));
                        double lon = Double.parseDouble(jsonObject.getString("lon"));
                        Location location = new Location("");
                        location.setLatitude(lat);
                        location.setLongitude(lon);
                        if (driverMarkerCar == null) {
                            int height = 95;
                            int width = 65;
                            Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.car_top);
                            Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                            BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);
                            DriverMarker = new MarkerOptions().title("Driver Here")
                                    .position(new LatLng(lat, lon))
                                    .icon(smallMarkerIcon);
                            driverMarkerCar = mMap.addMarker(DriverMarker);
                            if (!isMarkerZoom) {
                            }
                            // zoomCameraToLocation(new LatLng(location.getLatitude(), location.getLongitude()));
                        } else {
                            driverMarkerCar.setRotation(location.getBearing());

                            Log.e("LatlonDriver = ", " driver Location = " + new LatLng(lat, lon));
                            Log.e("LatlonDriver = ", " driver Marker = " + driverMarkerCar);
                            MarkerAnimation.animateMarkerToGB(driverMarkerCar, new LatLng(location.getLatitude(), location.getLongitude()), new LatLngInterpolator.Spherical());
                            // MarkerAnimation.animateMarkerToGB(driverMarkerCar, new LatLng(location.getLatitude(), location.getLongitude()), new LatLngInterpolator.Spherical());
                            // zoomCameraToLocation(new LatLng(location.getLatitude(),location.getLongitude()));

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

    private void tripFinishDialog() {
        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        DialogDriverArrivedDialogBinding dialogBinding = DataBindingUtil
                .inflate(LayoutInflater.from(mContext), R.layout.dialog_driver_arrived_dialog, null, false);
        dialog.setContentView(dialogBinding.getRoot());

        dialogBinding.tvText.setText(R.string.trip_finish_text);
        dialogBinding.btnIamComing.setText(getString(R.string.ok));
        dialogBinding.btnIamComing.setOnClickListener(v -> {
            dialog.dismiss();
            startActivity(new Intent(mContext, PaymentAct.class));
            finish();
        });

        dialogBinding.btnCall.setVisibility(View.GONE);

        dialogBinding.btnCall.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }

    private void showMarkerPickUp(@NonNull LatLng currentLocation) {
        if (currentLocation != null) {
            if (pCurrentLocationMarker == null) {
                pCurrentLocationMarker = mMap.addMarker(new MarkerOptions().position(PicLatLon).title("PickUp Location")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_marker)));
            }
        }
    }

    private void showDestinationMarker(@NonNull LatLng dcurrentLocation) {
        Log.e("TAG", "showDestinationMarker: " + dcurrentLocation);
        if (dcurrentLocation != null) {
            if (dcurrentLocationMarker == null) {
                dcurrentLocationMarker = mMap.addMarker(new MarkerOptions().position(DropLatLon).title("Destination Location")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.red_marker)));
            }
        }
        zoomMapAccordingToLatLng();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);

        showMarkerPickUp(PicLatLon);
        showDestinationMarker(DropLatLon);

        DrawPollyLine.get(this)
                .setOrigin(PicLatLon)
                .setDestination(DropLatLon)
                .execute(new DrawPollyLine.onPolyLineResponse() {
                    @Override
                    public void Success(ArrayList<LatLng> latLngs) {
                        PolylineOptions options = new PolylineOptions();
                        options.addAll(latLngs);
                        options.color(Color.BLUE);
                        options.width(10);
                        options.startCap(new SquareCap());
                        options.endCap(new SquareCap());
                        Polyline line = mMap.addPolyline(options);
                    }
                });

    }

    private void zoomMapAccordingToLatLng() {

        Log.e("pCurrentLocationMarker", "pCurrentLocationMarker = " + pCurrentLocationMarker);
        Log.e("pCurrentLocationMarker", "dcurrentLocationMarker = " + dcurrentLocationMarker);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        // the include method will calculate the min and max bound.
        builder.include(pCurrentLocationMarker.getPosition());
        builder.include(dcurrentLocationMarker.getPosition());

        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.10) + 30; // offset from edges of the map 10% of screen

        Log.e("pCurrentLocationMarker", "padding = " + padding);

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

        mMap.animateCamera(cu);

    }

}