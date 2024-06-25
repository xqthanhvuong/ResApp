package com.example.restaurantmanager.helper;


import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.restaurantmanager.API.VolleySingleton;
import com.example.restaurantmanager.Activity.SignUp;
import com.example.restaurantmanager.Dialog.OTPDialog;
import com.example.restaurantmanager.R;
import com.example.restaurantmanager.manager.PreferencesManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class OTPHelper {
    public interface OTPResultListener {
        void onResult(boolean success);
    }

    public static void sendOTPToPhoneByMail(Context context, String mail, OTPResultListener listener) {
        RequestQueue requestQueue = VolleySingleton.getInstance().getRequestQueue();
        String url = context.getString(R.string.base_url);
        JSONObject postData = new JSONObject();
        try {
            postData.put("mail", mail);
        } catch (JSONException e) {
            e.printStackTrace();
            listener.onResult(false);
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url + "/otp/sendOTP-mail",
                postData,
                response -> listener.onResult(true),
                error -> listener.onResult(false)
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        // Add the request to the RequestQueue
        requestQueue.add(jsonObjectRequest);
    }

    public static void sendOTPToPhone(Context context, String phone, OTPResultListener listener) {
        RequestQueue requestQueue = VolleySingleton.getInstance().getRequestQueue();
        String url = context.getString(R.string.base_url);
        JSONObject postData = new JSONObject();
        try {
            postData.put("phone", phone);
        } catch (JSONException e) {
            e.printStackTrace();
            listener.onResult(false);
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url + "/otp/sendOTP",
                postData,
                response -> listener.onResult(true),
                error -> listener.onResult(false)
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        // Add the request to the RequestQueue
        requestQueue.add(jsonObjectRequest);
    }

    public static void activePhone(Context context, String phone, String otp, OTPResultListener listener) {
        RequestQueue requestQueue = VolleySingleton.getInstance().getRequestQueue();
        String url = context.getString(R.string.base_url);
        JSONObject postData = new JSONObject();
        try {
            postData.put("phone", phone);
            postData.put("otp_code",otp);
        } catch (JSONException e) {
            e.printStackTrace();
            listener.onResult(false);
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url + "/otp/activeOTP",
                postData,
                response -> listener.onResult(true),
                error -> listener.onResult(false)
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        // Add the request to the RequestQueue
        requestQueue.add(jsonObjectRequest);
    }
    public static void checkOTP(Context context, String phone, String otp, OTPResultListener listener) {
        RequestQueue requestQueue = VolleySingleton.getInstance().getRequestQueue();
        String url = context.getString(R.string.base_url);
        JSONObject postData = new JSONObject();
        try {
            postData.put("phone", phone);
            postData.put("otp_code",otp);
        } catch (JSONException e) {
            e.printStackTrace();
            listener.onResult(false);
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url + "/otp/checkOTP",
                postData,
                response -> listener.onResult(true),
                error -> listener.onResult(false)
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        // Add the request to the RequestQueue
        requestQueue.add(jsonObjectRequest);
    }

}
