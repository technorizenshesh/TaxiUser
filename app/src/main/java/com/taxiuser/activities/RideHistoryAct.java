package com.taxiuser.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.os.Bundle;

import com.taxiuser.R;
import com.taxiuser.adapters.AdapterRideHistory;
import com.taxiuser.databinding.ActivityRideHistoryBinding;

public class RideHistoryAct extends AppCompatActivity {

    Context mContext = RideHistoryAct.this;
    ActivityRideHistoryBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_ride_history);
        itit();
    }

    private void itit() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.rvRideHistory.setAdapter(new AdapterRideHistory(mContext));

    }


}