package com.example.restaurantmanager.common;

import android.app.Activity;
import android.view.View;

public class NavigationHelper {
    public static void onBackClicked(View view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity) view.getContext()).finish();
            }
        });
    }
}
