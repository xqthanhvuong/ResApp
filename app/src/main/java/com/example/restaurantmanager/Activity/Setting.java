package com.example.restaurantmanager.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.example.restaurantmanager.R;
import com.example.restaurantmanager.common.NavigationHelper;
import com.example.restaurantmanager.manager.PreferencesManager;

public class Setting extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        LinearLayout btnLogout = findViewById(R.id.logout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
        setupTitle();
        NavigationHelper.onBackClicked(findViewById(R.id.btnBack));

    }
    private void setupTitle() {
        setTitle("Cài đặt");
    }

    private void logout() {
        PreferencesManager preferencesManager = PreferencesManager.getInstance();
        preferencesManager.saveRestaurantId(null);
        preferencesManager.saveName(null);
        preferencesManager.savePhone(null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            preferencesManager.savePass(null);
        }
        preferencesManager.saveId(null);
        preferencesManager.saveBirtday(null);
        preferencesManager.saveUserName(null);
        preferencesManager.saveRole(null);
        preferencesManager.saveAvt(null);
        preferencesManager.saveIdMenu(null);
        Intent intent = new Intent(getApplicationContext(), Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish(); // Gọi finish() nếu bạn cũng muốn đóng `Activity` hiện tại

    }
}