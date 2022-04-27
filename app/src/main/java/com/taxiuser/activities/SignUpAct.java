package com.taxiuser.activities;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.messaging.FirebaseMessaging;
import com.taxiuser.R;
import com.taxiuser.databinding.ActivitySignUpBinding;
import com.taxiuser.models.ModelLogin;
import com.taxiuser.utils.Compress;
import com.taxiuser.utils.MyApplication;
import com.taxiuser.utils.ProjectUtil;
import com.taxiuser.utils.SharedPref;
import com.taxiuser.utils.retrofitutils.Api;
import com.taxiuser.utils.retrofitutils.ApiFactory;

import org.json.JSONObject;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpAct extends AppCompatActivity {

    private static final int PERMISSION_ID = 1001;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 101;
    private static final int AUTOCOMPLETE_WORK_PLACE_CODE = 103;
    private static final int AUTOCOMPLETE_REQUEST_CODE_CITY = 102;
    private Location currentLocation;
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 4000;  /* 5 secs */
    private long FASTEST_INTERVAL = 4000; /* 2 sec */
    Context mContext = SignUpAct.this;
    ActivitySignUpBinding binding;
    private final int GALLERY = 0, CAMERA = 1;
    String type = "", registerId = "";
    File profileImage;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    private String str_image_path;
    private LatLng latLng, workLatLng = new LatLng(0.0,0.0);
    String driverEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);
        sharedPref = SharedPref.getInstance(mContext);

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

        if (!Places.isInitialized()) {
            Places.initialize(mContext, getString(R.string.api_key));
        }

        itit();

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

        SettingsClient settingsClient = LocationServices.getSettingsClient(SignUpAct.this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(SignUpAct.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SignUpAct.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        getFusedLocationProviderClient(SignUpAct.this)
                .requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        if (locationResult != null) {
                            Log.e("hdasfkjhksdf", "StartLocationUpdate = " + locationResult.getLastLocation());
                            currentLocation = locationResult.getLastLocation();
                        }
                    }
                }, Looper.myLooper());

    }

    private void itit() {

        binding.ivChangeLanguage.setOnClickListener(v -> {
            changeLangDialog();
        });

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.tvTerms.setOnClickListener(v -> {
            startActivity(new Intent(mContext, WebviewAct.class)
                    .putExtra("url", "https://technorizen.com/australia_taxi/terms&condition.html")
            );
        });

        binding.ivAlready.setOnClickListener(v -> {
            finish();
        });

        binding.etAdd1.setOnClickListener(v -> {
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(this);
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
        });

        binding.etWorkAdd.setOnClickListener(v -> {
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(this);
            startActivityForResult(intent, AUTOCOMPLETE_WORK_PLACE_CODE);
        });

        binding.etCityName.setOnClickListener(v -> {
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                    .setTypeFilter(TypeFilter.CITIES)
                    .build(this);
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE_CITY);
        });

        binding.addIcon.setOnClickListener(v -> {
            if (ProjectUtil.checkPermissions(mContext)) {
                showPictureDialog();
            } else {
                ProjectUtil.requestPermissions(mContext);
            }
        });

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.btnSignUp.setOnClickListener(v -> {
            if (TextUtils.isEmpty(binding.etFirstName.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.enter_name_firsttext), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(binding.etLastName.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.enter_name_lasttext), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(binding.etEmail.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.enter_email_text), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(binding.etPhone.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.enter_phone_text), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(binding.etCityName.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.enter_city_text), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(binding.etAdd1.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.enter_address1_text), Toast.LENGTH_SHORT).show();
            }/* else if (TextUtils.isEmpty(binding.etWorkAdd.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.enter_work_text), Toast.LENGTH_SHORT).show();
            }*/ else if (!ProjectUtil.isValidEmail(binding.etEmail.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.enter_valid_email), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(binding.etPassword.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_enter_pass), Toast.LENGTH_SHORT).show();
            } else if (!(binding.etPassword.getText().toString().trim().length() >= 4)) {
                Toast.makeText(mContext, getString(R.string.password_validation_text), Toast.LENGTH_SHORT).show();
            } else if (!binding.cbAcceptTerms.isChecked()) {
                Toast.makeText(mContext, getString(R.string.please_accept_terms), Toast.LENGTH_SHORT).show();
            } else if (profileImage == null) {
                Toast.makeText(mContext, getString(R.string.please_upload_profile), Toast.LENGTH_SHORT).show();
            } else {
                HashMap<String, String> params = new HashMap<>();
                HashMap<String, File> fileHashMap = new HashMap<>();

                params.put("first_name", binding.etFirstName.getText().toString().trim());
                params.put("last_name", binding.etLastName.getText().toString().trim());
                params.put("email", binding.etEmail.getText().toString().trim());
                params.put("mobile", binding.etPhone.getText().toString().trim());
                params.put("city", binding.etCityName.getText().toString().trim());
                params.put("address", binding.etAdd1.getText().toString().trim());
                params.put("register_id", registerId);
                params.put("workplace", binding.etWorkAdd.getText().toString().trim());
                params.put("work_lon", String.valueOf(workLatLng.longitude));
                params.put("work_lat", String.valueOf(workLatLng.latitude));
                params.put("lat", String.valueOf(latLng.latitude));
                params.put("lon", String.valueOf(latLng.longitude));
                params.put("password", binding.etPassword.getText().toString().trim());
                params.put("type", "USER");

                fileHashMap.put("image", profileImage);

                Log.e("signupDriver", "signupDriver = " + params);
                Log.e("signupDriver", "fileHashMap = " + fileHashMap);

                checkCredentials(params, fileHashMap);

            }

        });

    }

    private void checkCredentials(HashMap<String, String> params, HashMap<String, File> fileHashMap) {
        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));

        HashMap<String, String> paramHash = new HashMap<>();
        paramHash.put("mobile", binding.etPhone.getText().toString().trim());
        paramHash.put("email", binding.etEmail.getText().toString().trim());

        Log.e("asdfasdfasf", "paramHash = " + paramHash);

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.checkCredentialsApi(paramHash);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    Log.e("responseString", "responseString = " + responseString);

                    if (jsonObject.getString("status").equals("1")) {
                        String mobileNumber = "+61" + binding.etPhone.getText().toString().trim();
                       //  String mobileNumber = "+91" + binding.etPhone.getText().toString().trim();

                        startActivity(new Intent(mContext, VerifyAct.class)
                                .putExtra("resgisterHashmap", params)
                                .putExtra("mobile", mobileNumber)
                                .putExtra("fileHashMap", fileHashMap)
                        );
                    } else {
                        MyApplication.showAlert(mContext, getString(R.string.email_mobile_exit));
                    }

                } catch (Exception e) {
                    Log.e("Exception", "Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
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
                startActivity(new Intent(mContext, SignUpAct.class));
                dialog.dismiss();
            } else if (radioUrdu.isChecked()) {
                ProjectUtil.updateResources(mContext, "ur");
                sharedPref.setlanguage("lan", "ur");
                finish();
                startActivity(new Intent(mContext, SignUpAct.class));
                dialog.dismiss();
            } else if (radioArabic.isChecked()) {
                ProjectUtil.updateResources(mContext, "ar");
                sharedPref.setlanguage("lan", "ar");
                finish();
                startActivity(new Intent(mContext, SignUpAct.class));
                dialog.dismiss();
            } else if (radioFrench.isChecked()) {
                ProjectUtil.updateResources(mContext, "fr");
                sharedPref.setlanguage("lan", "fr");
                finish();
                startActivity(new Intent(mContext, SignUpAct.class));
                dialog.dismiss();
            } else if (radioChinese.isChecked()) {
                ProjectUtil.updateResources(mContext, "zh");
                sharedPref.setlanguage("lan", "zh");
                finish();
                startActivity(new Intent(mContext, SignUpAct.class));
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(mContext);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {"Select photo from gallery", "Capture photo from camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                ProjectUtil.openGallery(mContext, GALLERY);
                                break;
                            case 1:
                                str_image_path = ProjectUtil.openCamera(mContext, CAMERA);
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                latLng = place.getLatLng();
                try {
                    String addresses = ProjectUtil.getCompleteAddressString(mContext, place.getLatLng().latitude, place.getLatLng().longitude);
                    binding.etAdd1.setText(addresses);
                } catch (Exception e) {
                }
            }
        } else if (requestCode == AUTOCOMPLETE_WORK_PLACE_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                workLatLng = place.getLatLng();
                try {
                    String addresses = ProjectUtil.getCompleteAddressString(mContext, place.getLatLng().latitude, place.getLatLng().longitude);
                    binding.etWorkAdd.setText(addresses);
                } catch (Exception e) {
                }
            }
        } else if (requestCode == AUTOCOMPLETE_REQUEST_CODE_CITY) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                try {
                    String addresses = place.getName();

                    Log.e("addresses", "addresses = " + addresses);
                    Log.e("addresses", "addresses = " + place.getName());

                    binding.etCityName.setText(addresses);
                } catch (Exception e) {
                }
            }
        } else if (requestCode == GALLERY) {
            if (resultCode == RESULT_OK) {
                String path = ProjectUtil.getRealPathFromURI(mContext, data.getData());
                Compress.get(mContext).setQuality(80).execute(new Compress.onSuccessListener() {
                    @Override
                    public void response(boolean status, String message, File file) {
                        profileImage = file;
                        binding.profileImage.setImageURI(Uri.parse(file.getPath()));
                    }
                }).CompressedImage(path);
            }
        } else if (requestCode == CAMERA) {
            if (resultCode == RESULT_OK) {
                profileImage = new File(str_image_path);
                Compress.get(mContext).setQuality(80).execute(new Compress.onSuccessListener() {
                    @Override
                    public void response(boolean status, String message, File file) {
                        profileImage = file;
                        binding.profileImage.setImageURI(Uri.parse(file.getPath()));
                    }
                }).CompressedImage(str_image_path);
            }
        }

    }

}