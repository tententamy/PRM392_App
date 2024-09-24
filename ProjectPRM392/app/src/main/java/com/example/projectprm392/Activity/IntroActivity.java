package com.example.projectprm392.Activity;

import android.content.Intent;
import android.os.Bundle;

import com.example.projectprm392.databinding.ActivityIntroBinding;

public class IntroActivity extends BaseActivity {

    private ActivityIntroBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIntroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.startBtn.setOnClickListener(v ->{
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

    }
}