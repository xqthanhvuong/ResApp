package com.example.restaurantmanager.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.nsd.NsdManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.restaurantmanager.API.VolleySingleton;
import com.example.restaurantmanager.Dialog.LoadingDialog;
import com.example.restaurantmanager.Dialog.OTPDialog;
import com.example.restaurantmanager.MainActivity;
import com.example.restaurantmanager.R;
import com.example.restaurantmanager.helper.AlertHelper;
import com.example.restaurantmanager.helper.CheckHelper;
import com.example.restaurantmanager.helper.DialogHelper;
import com.example.restaurantmanager.helper.OTPHelper;
import com.example.restaurantmanager.helper.PhoneNumberHelper;
import com.example.restaurantmanager.manager.PreferencesManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {
    TextView register;
    EditText email,pass;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        register = findViewById(R.id.textregister);
        email = findViewById(R.id.edittextmail);
        pass = findViewById(R.id.edittextpass);
        btnLogin = findViewById(R.id.buttonlogin);


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, SignUp.class);
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickLogin();
            }
        });
    }

    private void clickLogin(){
//        if(!checkData()){
//            return;
//        }
        RequestQueue requestQueue = VolleySingleton.getInstance().getRequestQueue();
        //url
        String url = getString(R.string.base_url);
        JSONObject postData = new JSONObject();
        try {
            postData.put("username", email.getText().toString());
            postData.put("password",pass.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.startLoadingDialog();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url+"/account/login",
                postData,
                new Response.Listener<JSONObject>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            loadingDialog.dissmissDialog();
                            String id = response.getString("id");
                            String username = response.getString("username");
                            String role = response.getString("role");
                            String name = response.getString("name");
                            String phone = response.getString("phone");
                            String idRes = response.getString("idRestaurant");
                            String birthday = response.getString("birthDay");
                            String avt = response.getString("avt");
                            PreferencesManager preferencesManager = PreferencesManager.getInstance();
                            preferencesManager.savePhone(phone);
                            preferencesManager.saveUserName(username);
                            preferencesManager.saveRole(role);
                            preferencesManager.saveName(name);
                            preferencesManager.saveId(id);
                            if(!idRes.equals("null")){
                                preferencesManager.saveRestaurantId(idRes);
                            }
                            if(!birthday.equals("null")){
                                preferencesManager.saveBirtday(birthday);
                            }
                            if(!avt.equals("null")){
                                preferencesManager.saveAvt(avt);
                            }

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                preferencesManager.savePass(pass.getText().toString());
                            }
                            Intent intent = new Intent(Login.this, MainActivity.class);
                            startActivity(intent);
                            finish();

                        } catch (JSONException e) {
                            System.out.println(e);
                            DialogHelper.showFailDialog("có lỗi xảy ra vui lòng thử lại sau!",Login.this);
                            throw new RuntimeException(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loadingDialog.dissmissDialog();
                        DialogHelper.showFailDialog("Sai tài khoản hoặc mật khẩu",Login.this);
                        // Handle the error
                        if (error.networkResponse != null) {
                            int statusCode = error.networkResponse.statusCode;
                            Log.e("LOGIN", "Error: " + error.toString() + " Status Code: " + statusCode);
                        }
                    }
                }) {
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

    private boolean checkData(){
        if(!CheckHelper.isValidEmail(email.getText().toString())){
            email.requestFocus();
            AlertHelper.showAlert(Login.this,"Field Invalid","Email is invalid");
            return false;
        }
        if(!CheckHelper.isNullOrEmpty(pass.getText().toString())){
            pass.requestFocus();
            AlertHelper.showAlert(Login.this,"Mising password","Please enter the password");
            return false;
        }
        return true;
    }
}