package com.taxiuser.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.os.Bundle;

import com.taxiuser.R;
import com.taxiuser.databinding.ActivityUpdateProfileBinding;

public class UpdateProfileAct extends AppCompatActivity {

    Context mContext = UpdateProfileAct.this;
    ActivityUpdateProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_update_profile);
        itit();
    }

    private void itit() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.btnSave.setOnClickListener(v -> {
            finish();
        });

    }


}