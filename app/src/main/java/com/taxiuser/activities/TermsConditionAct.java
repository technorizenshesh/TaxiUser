package com.taxiuser.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.os.Bundle;

import com.taxiuser.R;
import com.taxiuser.databinding.ActivityTermsConditionBinding;

public class TermsConditionAct extends AppCompatActivity {

    Context mContext = TermsConditionAct.this;
    ActivityTermsConditionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_terms_condition);
        itit();
    }

    private void itit() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

    }

}