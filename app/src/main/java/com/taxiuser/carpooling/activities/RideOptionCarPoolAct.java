package com.taxiuser.carpooling.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.WindowManager;

import com.taxiuser.R;
import com.taxiuser.activities.TrackCarPoolAct;
import com.taxiuser.databinding.ActivityRideOptionCarPoolBinding;
import com.taxiuser.databinding.PassengerDialogBinding;

public class RideOptionCarPoolAct extends AppCompatActivity {

    Context mContext = RideOptionCarPoolAct.this;
    ActivityRideOptionCarPoolBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_ride_option_car_pool);
        itit();
    }

    private void itit() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.btSend.setOnClickListener(v -> {
            openPassengerDialog();
        });

    }

    private void openPassengerDialog() {

        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        PassengerDialogBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                R.layout.passenger_dialog, null, false);
        dialog.setContentView(dialogBinding.getRoot());
        dialog.getWindow().setBackgroundDrawableResource(R.color.white2);

        dialogBinding.btSubmit.setOnClickListener(v -> {
            dialog.dismiss();
            startActivity(new Intent(mContext, TrackCarPoolAct.class));
        });

        dialog.show();

    }


}