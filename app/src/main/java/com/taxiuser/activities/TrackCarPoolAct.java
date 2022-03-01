package com.taxiuser.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.os.Bundle;

import com.taxiuser.R;
import com.taxiuser.databinding.ActivityTrackCarPoolBinding;

public class TrackCarPoolAct extends AppCompatActivity {

    Context mContext = TrackCarPoolAct.this;
    ActivityTrackCarPoolBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_track_car_pool);
        itit();
    }

    private void itit() {

        binding.btnBack.setOnClickListener(v -> {
            finish();
        });

    }


}