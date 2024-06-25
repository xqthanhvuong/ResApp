package com.example.restaurantmanager.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import com.android.volley.toolbox.JsonObjectRequest;
import com.example.restaurantmanager.API.VolleySingleton;
import com.example.restaurantmanager.Dialog.LoadingDialog;
import com.example.restaurantmanager.R;
import com.example.restaurantmanager.common.NavigationHelper;
import com.example.restaurantmanager.helper.DialogHelper;
import com.example.restaurantmanager.manager.PreferencesManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class PaymentInfoActivity extends AppCompatActivity {
    private String idRestaurant;
    private String username;
    private String password;
    private String idPayment;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_infor);

        // Set up button back
        NavigationHelper.onBackClicked(findViewById(R.id.btnBack));

        // Get idRestaurant from PreferencesManager
        PreferencesManager preferencesManager = PreferencesManager.getInstance();
        idRestaurant = preferencesManager.getRestaurantId();
        username = preferencesManager.getUserName();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            password = preferencesManager.getPass();
        }

        checkPaymentExist();

        loadingDialog = new LoadingDialog(PaymentInfoActivity.this);

    }

    private void setupButtonSave(View v) {
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                loadingDialog.startLoadingDialog();
                // Lấy dữ liệu từ các EditText
                EditText txtPartnerCode = findViewById(R.id.editTextPartnerCode);
                EditText txtSecretKey = findViewById(R.id.editTextSecretKey);
                EditText txtAccessKey = findViewById(R.id.editTextAccessKey);

                // Chuyển đổi sang String
                String partnerCode = txtPartnerCode.getText().toString();
                String secretKey = txtSecretKey.getText().toString();
                String accesskey = txtAccessKey.getText().toString();

//                Log.d("PaymentInfoActivity", "onClick: " + partnerCode + " " + secretKey + " " + accesskey + " " + idRestaurant);

                if(partnerCode.isEmpty() || secretKey.isEmpty() || accesskey.isEmpty()){
                    Toast.makeText(PaymentInfoActivity.this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                    return;
                }


                String url = getString(R.string.base_url) + "/payment/create-payment";

                //Call API to save employee
                RequestQueue queue = VolleySingleton.getInstance().getRequestQueue();
                JSONObject body = new JSONObject();
                try {
                    body.put("idRestaurant", idRestaurant);
                    body.put("partnerCode", partnerCode);
                    body.put("accessKey", accesskey);
                    body.put("secretKey", secretKey);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(PaymentInfoActivity.this, "Lỗi tạo body", Toast.LENGTH_SHORT).show();
                    return;
                }
                StringRequest request = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
//                                loadingDialog.dissmissDialog();
                                Toast.makeText(PaymentInfoActivity.this, "Cấu hình thành công!", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        loadingDialog.dissmissDialog();
                        Toast.makeText(PaymentInfoActivity.this, "Lỗi", Toast.LENGTH_SHORT).show();
                    }
                }){
                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        return body.toString().getBytes(StandardCharsets.UTF_8);
                    }

                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8";
                    }

                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> headers = new HashMap<String, String>();
                        String credentials = username + ":" + password;
                        headers.put("Authorization", "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP));
                        return headers;
                    }
                };
                queue.add(request);
            }
        });
    }

    private void checkPaymentExist() {
//        loadingDialog.startLoadingDialog();
        String url = getString(R.string.base_url) + "/payment/get-payment";
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        loadingDialog.dissmissDialog();
                        // Handle response
                        if(response.length()>0){
                            try {
                                EditText txtPartnerCode = findViewById(R.id.editTextPartnerCode);
                                EditText txtSecretKey = findViewById(R.id.editTextSecretKey);
                                EditText txtAccessKey = findViewById(R.id.editTextAccessKey);

                                idPayment = response.getString("idPayment");
                                txtPartnerCode.setText(response.getString("partnerCode"));
                                txtSecretKey.setText(response.getString("secretKey"));
                                txtAccessKey.setText(response.getString("accessKey"));

                                setupButtonUpdate(findViewById(R.id.btnSave));
                                TextView btnSave = findViewById(R.id.btnSave);
                                btnSave.setText("Cập nhật");
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        } else  {
                            setupButtonSave(findViewById(R.id.btnSave));
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                loadingDialog.dissmissDialog();
                Toast.makeText(PaymentInfoActivity.this, "Chưa cấu hình thanh toán ", Toast.LENGTH_SHORT).show();
                Log.e("Error Payment", error.toString());
                // Handle error
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                // Add Basic Authentication header
                String credentials = username + ":" + password;
                String auth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                params.put("Authorization", auth);

                return params;
            }
        };
        queue.add(jsonArrayRequest);
    }

    private void setupButtonUpdate(View v) {
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy dữ liệu từ các EditText
                EditText txtPartnerCode = findViewById(R.id.editTextPartnerCode);
                EditText txtSecretKey = findViewById(R.id.editTextSecretKey);
                EditText txtAccessKey = findViewById(R.id.editTextAccessKey);

                // Chuyển đổi sang String
                String partnerCode = txtPartnerCode.getText().toString();
                String secretKey = txtSecretKey.getText().toString();
                String accesskey = txtAccessKey.getText().toString();

                if(partnerCode.isEmpty() || secretKey.isEmpty() || accesskey.isEmpty()){
                    Toast.makeText(PaymentInfoActivity.this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                    return;
                }


                String url = getString(R.string.base_url) + "/payment/update-payment";

                //Call API to save employee
                RequestQueue queue = VolleySingleton.getInstance().getRequestQueue();
                JSONObject body = new JSONObject();
                try {
                    body.put("idPayment", idPayment);
                    body.put("idRestaurant", idRestaurant);
                    body.put("partnerCode", partnerCode);
                    body.put("accessKey", accesskey);
                    body.put("secretKey", secretKey);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(PaymentInfoActivity.this, "Lỗi tạo body", Toast.LENGTH_SHORT).show();
                    return;
                }
                StringRequest request = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
//                                loadingDialog.dissmissDialog();
                                Toast.makeText(PaymentInfoActivity.this, "Cấu hình thành công!", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        loadingDialog.dissmissDialog();
                        Toast.makeText(PaymentInfoActivity.this, "Lỗi", Toast.LENGTH_SHORT).show();
                    }
                }){
                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        return body.toString().getBytes(StandardCharsets.UTF_8);
                    }

                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8";
                    }

                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> headers = new HashMap<String, String>();
                        String credentials = username + ":" + password;
                        headers.put("Authorization", "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP));
                        return headers;
                    }
                };
                queue.add(request);
            }
        });
    }
}
