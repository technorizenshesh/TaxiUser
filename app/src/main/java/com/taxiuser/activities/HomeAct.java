package com.taxiuser.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.taxiuser.R;
import com.taxiuser.databinding.ActivityHomeBinding;
import com.taxiuser.databinding.ChangeLanguageDialogBinding;
import com.taxiuser.models.ModelLogin;
import com.taxiuser.utils.AppConstant;
import com.taxiuser.utils.InternetConnection;
import com.taxiuser.utils.MyApplication;
import com.taxiuser.utils.ProjectUtil;
import com.taxiuser.utils.SharedPref;

public class HomeAct extends AppCompatActivity implements OnMapReadyCallback {

    Context mContext = HomeAct.this;
    ActivityHomeBinding binding;
    SupportMapFragment mapFragment;
    GoogleMap mMap;
    SharedPref sharedPref;
    ModelLogin modelLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);

        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);

        if (!InternetConnection.checkConnection(mContext)) {
            MyApplication.showConnectionDialog(mContext);
        }

        itit();

    }

    private void itit() {

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(HomeAct.this);

        binding.childNavDrawer.tvEmail.setText(modelLogin.getResult().getEmail());
        binding.childNavDrawer.tvUsername.setText(modelLogin.getResult().getUser_name());
        Glide.with(mContext).load(modelLogin.getResult().getImage()).into(binding.childNavDrawer.userImg);

        binding.childNavDrawer.signout.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            ProjectUtil.logoutAppDialog(mContext);
        });

        binding.chlidDashboard.navbar.setOnClickListener(v -> {
            binding.drawerLayout.openDrawer(GravityCompat.START);
        });

        binding.childNavDrawer.tvHome.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        });

        binding.childNavDrawer.tvProfile.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(mContext, UpdateProfileAct.class));
        });

        binding.chlidDashboard.btnNext.setOnClickListener(v -> {
            startActivity(new Intent(mContext, Home2Activity.class));
        });

        binding.childNavDrawer.tvMessage.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(mContext, ChatListAct.class));
        });

        binding.childNavDrawer.tvWallet.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(mContext, WalletAct.class));
        });

        binding.childNavDrawer.tvRideHistory.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(mContext, RideHistoryAct.class));
        });

        binding.childNavDrawer.tvPromo.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            startActivity(new Intent(mContext, PromocodesAct.class));
        });

        binding.childNavDrawer.tvChnageLang.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            changeLangDialog();
        });

    }

    private void changeLangDialog() {

        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        ChangeLanguageDialogBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext)
                , R.layout.change_language_dialog, null, false);
        dialog.setContentView(dialogBinding.getRoot());

        dialogBinding.ivBack.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialogBinding.rbEnglish.setOnClickListener(v -> {
            dialogBinding.rbItalian.setChecked(false);
            dialogBinding.rbSpanish.setChecked(false);
            dialogBinding.rbGerman.setChecked(false);
        });

        dialogBinding.rbItalian.setOnClickListener(v -> {
            dialogBinding.rbEnglish.setChecked(false);
            dialogBinding.rbSpanish.setChecked(false);
            dialogBinding.rbGerman.setChecked(false);
        });

        dialogBinding.rbSpanish.setOnClickListener(v -> {
            dialogBinding.rbEnglish.setChecked(false);
            dialogBinding.rbItalian.setChecked(false);
            dialogBinding.rbGerman.setChecked(false);
        });

        dialogBinding.rbGerman.setOnClickListener(v -> {
            dialogBinding.rbEnglish.setChecked(false);
            dialogBinding.rbItalian.setChecked(false);
            dialogBinding.rbSpanish.setChecked(false);
        });

        dialogBinding.btnSaveLang.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

}


