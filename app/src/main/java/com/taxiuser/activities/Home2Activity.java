package com.taxiuser.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.taxiuser.R;
import com.taxiuser.carpooling.activities.RideOptionCarPoolAct;
import com.taxiuser.databinding.ActivityHome2Binding;
import com.taxiuser.utils.AppConstant;

public class Home2Activity extends AppCompatActivity {

    Context mContext = Home2Activity.this;
    ActivityHome2Binding binding;
    String type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home2);
        type = getIntent().getStringExtra("type");
        itit();
    }

    private void itit() {

        binding.btnNext.setOnClickListener(v -> {
            if (type.equals(AppConstant.POOL)) {
                startActivity(new Intent(mContext, RideOptionCarPoolAct.class));
            } else {
                startActivity(new Intent(mContext, RideOptionAct.class));
            }
        });

    }

}