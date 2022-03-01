package com.taxiuser.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.taxiuser.R;
import com.taxiuser.databinding.ActivityCarPoolingFormBinding;
import com.taxiuser.databinding.PassengerDialogBinding;
import com.taxiuser.models.ModelLogin;
import com.taxiuser.models.ModelPoolList;
import com.taxiuser.utils.AppConstant;
import com.taxiuser.utils.MyApplication;
import com.taxiuser.utils.ProjectUtil;
import com.taxiuser.utils.SharedPref;
import com.taxiuser.utils.retrofitutils.Api;
import com.taxiuser.utils.retrofitutils.ApiFactory;
import com.taxiuser.utils.routes.DrawPollyLine;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CarPoolingFormAct extends AppCompatActivity implements OnMapReadyCallback {

    Context mContext = CarPoolingFormAct.this;
    ActivityCarPoolingFormBinding binding;
    Dialog dialog;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    private boolean isAddedMarker2 = false, isAddedMarker1 = false;
    ModelPoolList.Result data;
    LatLng pickLatLon, destiLatLon;
    private PolylineOptions lineOptions;
    SupportMapFragment mapFragment;
    private MarkerOptions PicUpMarker, DropOffMarker;
    GoogleMap mMap;
    String lat, lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_car_pooling_form);
        data = (ModelPoolList.Result) getIntent().getSerializableExtra("data");
        sharedPref = SharedPref.getInstance(mContext);
        lat = getIntent().getStringExtra("lat");
        lon = getIntent().getStringExtra("lon");
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        itit();
    }

    private void itit() {

        PicUpMarker = new MarkerOptions().title("Pick Up Location")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_marker));
        DropOffMarker = new MarkerOptions().title("Drop Off Location")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_marker));

        pickLatLon = new LatLng(Double.parseDouble(data.getStart_lat()), Double.parseDouble(data.getStart_lon()));
        destiLatLon = new LatLng(Double.parseDouble(data.getEnd_lat()), Double.parseDouble(data.getEnd_lon()));

        DrawPolyLine();

        binding.tvFrom.setText(data.getStart_location());
        binding.tvDestination.setText(data.getEnd_location());

        binding.tvFrom.setOnClickListener(v -> {
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(this);
            startActivityForResult(intent, 1002);
        });

        binding.tvDestination.setOnClickListener(v -> {
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(this);
            startActivityForResult(intent, 1003);
        });

        binding.btnNext.setOnClickListener(v -> {
            openPassengerDialog();
        });

    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(mContext, AvailablePoolDriversAct.class)
                .putExtra("lat", String.valueOf(lat))
                .putExtra("lon", String.valueOf(lon))
        );
    }

    private void openPassengerDialog() {

        dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        PassengerDialogBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                R.layout.passenger_dialog, null, false);
        dialog.setContentView(dialogBinding.getRoot());
        dialog.getWindow().setBackgroundDrawableResource(R.color.white2);

        dialogBinding.btSubmit.setOnClickListener(v -> {
            if (TextUtils.isEmpty(dialogBinding.etPassenger.getText().toString().trim())) {
                MyApplication.showAlert(mContext, getString(R.string.enter_no_passenger));
            } else if ((Integer.parseInt(data.getSeats_offer()) < Integer.parseInt(dialogBinding.etPassenger.getText().toString().trim()))) {
                Log.e("sfdasfasdfasd", "data Seats = " + Integer.parseInt(data.getSeats_offer()));
                Log.e("sfdasfasdfasd", "Edittext Seats = " + Integer.parseInt(dialogBinding.etPassenger.getText().toString().trim()));
                MyApplication.showAlert(mContext, "Only " + data.getSeats_offer() + " Seats are available!");
            } else if (dialogBinding.rbCash.isChecked()) {
                dialog.dismiss();
                bookingPoolRequestApiCall(dialogBinding.etPassenger.getText().toString().trim(),"Cash");
            } else if (dialogBinding.rbCard.isChecked()) {
                dialog.dismiss();
                bookingPoolRequestApiCall(dialogBinding.etPassenger.getText().toString().trim(),"Card");
            } else if (dialogBinding.rbWallet.isChecked()) {
                dialog.dismiss();
                bookingPoolRequestApiCall(dialogBinding.etPassenger.getText().toString().trim(),"Wallet");
            } else {
                MyApplication.showAlert(mContext, getString(R.string.please_select_pay_method));
            }
        });

        dialog.show();

    }

    private void bookingPoolRequestApiCall(String passenger,String payMethod) {

        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));

        HashMap<String, String> paramHash = new HashMap<>();

        paramHash.put("user_id", modelLogin.getResult().getId());
        paramHash.put("pool_request_id", data.getId());
        paramHash.put("driver_id", data.getDriver_id());
        paramHash.put("seats_no", passenger);
        paramHash.put("payment_type", payMethod);
        paramHash.put("start_location", binding.tvFrom.getText().toString().trim());
        paramHash.put("end_location", binding.tvDestination.getText().toString().trim());
        paramHash.put("start_lat", String.valueOf(pickLatLon.latitude));
        paramHash.put("start_lon", String.valueOf(pickLatLon.longitude));
        paramHash.put("end_lat", String.valueOf(destiLatLon.latitude));
        paramHash.put("end_lon", String.valueOf(destiLatLon.longitude));

        Log.e("sadasddasd", "paramHash = " + paramHash);

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.sendPoolRequestToDriver(paramHash);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();

                try {

                    String stringResponse = response.body().string();

                    try {

                        JSONObject jsonObject = new JSONObject(stringResponse);

                        if (jsonObject.getString("status").equals("1")) {

                            Log.e("asfddasfasdf", "response = " + stringResponse);
                            Toast.makeText(mContext, "Request Sent Successfully !", Toast.LENGTH_LONG).show();
                            finish();
                            startActivity(new Intent(mContext, AvailablePoolDriversAct.class)
                                    .putExtra("lat", String.valueOf(lat))
                                    .putExtra("lon", String.valueOf(lon))
                            );

                        } else {
                            Toast.makeText(mContext, getString(R.string.no_history_found), Toast.LENGTH_LONG).show();
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

    private void DrawPolyLine() {
        DrawPollyLine.get(this).setOrigin(pickLatLon)
                .setDestination(destiLatLon)
                .execute(new DrawPollyLine.onPolyLineResponse() {
                    @Override
                    public void Success(ArrayList<LatLng> latLngs) {
                        mMap.clear();
                        lineOptions = new PolylineOptions();
                        lineOptions.addAll(latLngs);
                        lineOptions.width(10);
                        lineOptions.color(Color.BLUE);
                        AddDefaultMarker();
                    }
                });
    }

    @NonNull
    private CameraPosition getCameraPositionWithBearing(LatLng latLng) {
        return new CameraPosition.Builder().target(latLng).zoom(16).build();
    }

    public void AddDefaultMarker() {
        if (mMap != null) {
            mMap.clear();
            if (lineOptions != null)
                mMap.addPolyline(lineOptions);
            if (pickLatLon != null) {
                PicUpMarker.position(pickLatLon);
                mMap.addMarker(PicUpMarker);
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(getCameraPositionWithBearing(pickLatLon)));
            }
            if (destiLatLon != null) {
                DropOffMarker.position(destiLatLon);
                mMap.addMarker(DropOffMarker);
            }
        }
        zoomMapInitial(pickLatLon, destiLatLon);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1002) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                pickLatLon = place.getLatLng();
                binding.tvFrom.setText(ProjectUtil.getCompleteAddressString(mContext, pickLatLon.latitude, pickLatLon.longitude));
                if (pickLatLon != null) {
                    PicUpMarker.position(pickLatLon);
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(getCameraPositionWithBearing(pickLatLon)));
                    if (!isAddedMarker1) {
                        mMap.addMarker(PicUpMarker);
                        isAddedMarker1 = true;
                    }
                }
                if (pickLatLon != null & destiLatLon != null) {
                    DrawPolyLine();
                }
            }
        } else if (requestCode == 1003) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                destiLatLon = place.getLatLng();
                binding.tvDestination.setText(ProjectUtil.getCompleteAddressString(mContext, destiLatLon.latitude, destiLatLon.longitude));
                if (destiLatLon != null) {
                    PicUpMarker.position(destiLatLon);
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(getCameraPositionWithBearing(destiLatLon)));
                    if (!isAddedMarker1) {
                        mMap.addMarker(PicUpMarker);
                        isAddedMarker1 = true;
                    }
                }
                if (pickLatLon != null & destiLatLon != null) {
                    DrawPolyLine();
                }
            }
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

}