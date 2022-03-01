package com.taxiuser.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.taxiuser.R;
import com.taxiuser.models.ModelLogin;
import com.taxiuser.utils.AppConstant;
import com.taxiuser.utils.ProjectUtil;
import com.taxiuser.utils.SharedPref;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SplashAct extends AppCompatActivity {

    Context mContext = SplashAct.this;
    int PERMISSION_ID = 44;
    SharedPref sharedPref;
    ModelLogin modelLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        sharedPref = SharedPref.getInstance(mContext);
        printHashKey(mContext);
    }

    public static void printHashKey(Context pContext) {
        Log.i("dsadadsdad", "printHashKey() Hash Key: aaya ander");
        try {
            PackageInfo info = pContext.getPackageManager().getPackageInfo(pContext.getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.i("dsadadsdad", "printHashKey() Hash Key: " + hashKey);
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e("dsadadsdad", "printHashKey()", e);
        } catch (Exception e) {
            Log.e("dsadadsdad", "printHashKey()", e);
        }
    }

    @Override
    protected void onResume() {
        if (checkPermissions()) {
            // Toast.makeText(this, "Location enabled " + isLocationEnabled(), Toast.LENGTH_LONG).show();
            if (isLocationEnabled()) {
                if ("en".equals(sharedPref.getLanguage("lan"))) {
                    ProjectUtil.updateResources(mContext, "en");
                } else if ("ar".equals(sharedPref.getLanguage("lan"))) {
                    ProjectUtil.updateResources(mContext, "ar");
                } else if ("fr".equals(sharedPref.getLanguage("lan"))) {
                    ProjectUtil.updateResources(mContext, "fr");
                } else if ("ur".equals(sharedPref.getLanguage("lan"))) {
                    ProjectUtil.updateResources(mContext, "ur");
                } else if ("zh".equals(sharedPref.getLanguage("lan"))) {
                    ProjectUtil.updateResources(mContext, "zh");
                } else {
                    sharedPref.setlanguage("lan", "en");
                    ProjectUtil.updateResources(mContext, "en");
                }
                processNextActivity();
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
        super.onResume();
    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE},
                PERMISSION_ID
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if ("en".equals(sharedPref.getLanguage("lan"))) {
                    ProjectUtil.updateResources(mContext, "en");
                } else if ("ar".equals(sharedPref.getLanguage("lan"))) {
                    ProjectUtil.updateResources(mContext, "ar");
                } else if ("fr".equals(sharedPref.getLanguage("lan"))) {
                    ProjectUtil.updateResources(mContext, "fr");
                } else if ("ur".equals(sharedPref.getLanguage("lan"))) {
                    ProjectUtil.updateResources(mContext, "ur");
                } else if ("zh".equals(sharedPref.getLanguage("lan"))) {
                    ProjectUtil.updateResources(mContext, "zh");
                } else {
                    sharedPref.setlanguage("lan", "en");
                    ProjectUtil.updateResources(mContext, "en");
                }
                processNextActivity();
            }
        }
    }

    private void processNextActivity() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (sharedPref.getBooleanValue(AppConstant.IS_REGISTER)) {
                    startActivity(new Intent(mContext, HomeAct.class));
                    finish();
                } else {
                    startActivity(new Intent(mContext, LoginAct.class));
                    finish();
                }
            }
        }, 2000);

    }

}