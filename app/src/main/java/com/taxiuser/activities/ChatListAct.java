package com.taxiuser.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.taxiuser.R;
import com.taxiuser.databinding.ActivityChatListBinding;
import com.taxiuser.databinding.ActivityChatingBinding;

public class ChatListAct extends AppCompatActivity {

    Context mContext = ChatListAct.this;
    ActivityChatListBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat_list);
        itit();
    }

    private void itit() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.cvMsg1.setOnClickListener(v -> {
            startActivity(new Intent(mContext, ChatingAct.class));
        });

    }


}