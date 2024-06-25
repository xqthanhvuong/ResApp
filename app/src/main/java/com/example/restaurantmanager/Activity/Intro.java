package com.example.restaurantmanager.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.restaurantmanager.MainActivity;
import com.example.restaurantmanager.R;
import com.example.restaurantmanager.helper.CheckHelper;
import com.example.restaurantmanager.manager.PreferencesManager;

public class Intro extends AppCompatActivity {

    Button btnGetStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferencesManager preferencesManager = PreferencesManager.getInstance();
        if (preferencesManager.getIsFirstTime()){
            preferencesManager.setIsFirstTime(false);
            setContentView(R.layout.activiti_intro);
            btnGetStart = findViewById(R.id.buttonGetStarted);
            btnGetStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openLogin();
                }
            });
        }else if(CheckHelper.isNullOrEmpty(preferencesManager.getUserName())) {
            openLogin();
        }else {
            Intent intent = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                intent = new Intent(Intro.this, MainActivity.class);
            }
            startActivity(intent);
            finish();
        }
    }
    private void openLogin(){
        Intent intent = new Intent(Intro.this, Login.class);
        startActivity(intent);
        finish();
    }
}