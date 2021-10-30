package com.taxiuser.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.taxiuser.R;
import com.taxiuser.databinding.ActivityHome2Binding;

public class Home2Activity extends AppCompatActivity {

    Context mContext = Home2Activity.this;
    ActivityHome2Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home2);
        itit();
    }

    private void itit() {

        binding.btnNext.setOnClickListener(v -> {
            startActivity(new Intent(mContext, RideOptionAct.class));
        });

    }

}