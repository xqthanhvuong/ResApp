package com.example.restaurantmanager.API;

import android.annotation.SuppressLint;
import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.restaurantmanager.MyApplication;

public class VolleySingleton {
    @SuppressLint("StaticFieldLeak")
    private static VolleySingleton instance;
    private RequestQueue requestQueue;
    @SuppressLint("StaticFieldLeak")
    private static Context ctx;

    private VolleySingleton() {
        ctx = MyApplication.getAppContext();
        requestQueue = getRequestQueue();
    }

    public static synchronized VolleySingleton getInstance() {
        if (instance == null) {
            instance = new VolleySingleton();
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(ctx);
        }
        return requestQueue;
    }
}
