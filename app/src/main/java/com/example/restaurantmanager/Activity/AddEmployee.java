package com.example.restaurantmanager.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.restaurantmanager.API.VolleySingleton;
import com.example.restaurantmanager.Dialog.LoadingDialog;
import com.example.restaurantmanager.R;
import com.example.restaurantmanager.common.NavigationHelper;
import com.example.restaurantmanager.helper.DialogHelper;
import com.example.restaurantmanager.manager.PreferencesManager;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class AddEmployee extends AppCompatActivity {
    private EditText nameEmployee;
    private EditText phoneEmployee;
    private EditText username;
    private EditText password;
    private String usernames;
    private String passwords;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employee);

        // Set up button back
        NavigationHelper.onBackClicked(findViewById(R.id.btnBack));

        // Set up button save
        setupButtonSave(findViewById(R.id.btnSave));

        // Lấy thông tin tài khoản
        usernames = PreferencesManager.getInstance().getUserName();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            passwords = PreferencesManager.getInstance().getPass();
        }

        nameEmployee = findViewById(R.id.nameEmployee);
        phoneEmployee = findViewById(R.id.phoneEmployee);
        username = findViewById(R.id.usernameEmployee);
        password = findViewById(R.id.passEmployee);

        // bắt sự kiện thay đổi giá trị của EditText và hiển thị
        nameEmployee.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String name = nameEmployee.getText().toString();
//                username.setText(createUsername(name));
                password.setText(createPassword(name));
            }
        });

        loadingDialog = new LoadingDialog(AddEmployee.this);
    }

    private String createShortName(String name) {
        String[] words = name.split(" ");
        StringBuilder shortName = new StringBuilder();
        for (String word : words) {
            shortName.append(word.charAt(0));
        }
        shortName = new StringBuilder(shortName.toString().toUpperCase());
        return shortName.toString();
    }

    private String createUsername(String name) {
        return createShortName(name) + "_restaurant1";
    }

    private String createPassword(String name) {
        return createShortName(name) + "restaurant1";
    }

    private void setupButtonSave(View v) {
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.startLoadingDialog();

                // Chuyển đổi sang String
                String name = nameEmployee.getText().toString();
                String phone = phoneEmployee.getText().toString();
                String user = username.getText().toString();
                String pass = password.getText().toString();


                String url = getString(R.string.base_url) + "/account/create-employee?idMenu=";

                Map<String, String> headers = new HashMap<>();
                String credentials = usernames + ":" + passwords;
                headers.put("Authorization", "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP));

                //Call API to save employee
                RequestQueue queue = VolleySingleton.getInstance().getRequestQueue();
                JSONObject postData = new JSONObject();
                try{
                    postData.put("idRestaurant", PreferencesManager.getInstance().getRestaurantId());
                    postData.put("name", name);
                    postData.put("phone", phone);
                    postData.put("username", user);
                    postData.put("password", pass);
                }catch (Exception e){
                    e.printStackTrace();
                }

                StringRequest request = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                loadingDialog.dissmissDialog();
                                Toast.makeText(AddEmployee.this, "Tạo nhân viên thành công!", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loadingDialog.dissmissDialog();
                        Toast.makeText(AddEmployee.this, "Tài khoản đã tồn tại", Toast.LENGTH_SHORT).show();
                    }
                }){
                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        return postData.toString().getBytes(StandardCharsets.UTF_8);
                    }

                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8";
                    }

                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> headers = new HashMap<String, String>();
                        String credentials = usernames + ":" + passwords;
                        headers.put("Authorization", "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP));
                        return headers;
                    }
                };
                queue.add(request);
            }
        });
    }
}