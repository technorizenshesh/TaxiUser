package com.taxiuser.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.facebook.login.Login;
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
import com.taxiuser.utils.AppConstant;
import com.taxiuser.utils.Compress;
import com.taxiuser.utils.ProjectUtil;
import com.taxiuser.utils.SharedPref;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SignUpAct extends AppCompatActivity {

    private static final int PERMISSION_ID = 1001;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 101;
    private static final int AUTOCOMPLETE_REQUEST_CODE_CITY = 102;
    Context mContext = SignUpAct.this;
    ActivitySignUpBinding binding;
    private final int GALLERY = 0, CAMERA = 1;
    String type = "", registerId = "";
    File profileImage;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    private String str_image_path;
    private LatLng latLng;
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

    private void itit() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.tvTerms.setOnClickListener(v -> {
            startActivity(new Intent(mContext, TermsConditionAct.class));
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
            } else if (!ProjectUtil.isValidEmail(binding.etEmail.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.enter_valid_email), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(binding.etPassword.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_enter_pass), Toast.LENGTH_SHORT).show();
            } else if (!(binding.etPassword.getText().toString().trim().length() >= 8)) {
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
                params.put("lat", String.valueOf(latLng.latitude));
                params.put("lon", String.valueOf(latLng.longitude));
                params.put("password", binding.etPassword.getText().toString().trim());
                params.put("type", "USER");

                fileHashMap.put("image", profileImage);

                Log.e("signupDriver", "signupDriver = " + params);
                Log.e("signupDriver", "fileHashMap = " + fileHashMap);

                // String mobileNumber = "+61" + binding.etPhone.getText().toString().trim();
                String mobileNumber = "+91" + binding.etPhone.getText().toString().trim();

                startActivity(new Intent(mContext, VerifyAct.class)
                        .putExtra("resgisterHashmap", params)
                        .putExtra("mobile", mobileNumber)
                        .putExtra("fileHashMap", fileHashMap)
                );

            }

//            startActivity(new Intent(mContext, VerifyAct.class)
//                    .putExtra(AppContant.TYPE, AppContant.USER)
//            );

        });
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