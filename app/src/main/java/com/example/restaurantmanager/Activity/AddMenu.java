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
import com.android.volley.toolbox.StringRequest;
import com.example.restaurantmanager.API.VolleySingleton;
import com.example.restaurantmanager.Dialog.LoadingDialog;
import com.example.restaurantmanager.R;
import com.example.restaurantmanager.common.NavigationHelper;
import com.example.restaurantmanager.manager.PreferencesManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


public class AddMenu extends AppCompatActivity {

    private String idRestaurant;
    private String username ;
    private String password ;
    private String idMenu;

    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_menu);

        loadingDialog = new LoadingDialog(AddMenu.this);

        PreferencesManager preferencesManager = PreferencesManager.getInstance();
        username=preferencesManager.getUserName();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            password=preferencesManager.getPass();
        }

        idRestaurant = preferencesManager.getRestaurantId();

        NavigationHelper.onBackClicked(findViewById(R.id.btnBackLo));

        if(getIntent().getExtras().getString("idMenu") != null){
            idMenu = getIntent().getExtras().getString("idMenu");
            String menuName = getIntent().getExtras().getString("menuName");
            EditText menuNameEditText = findViewById(R.id.editMenuName);
            menuNameEditText.setText(menuName);
            TextView btn = findViewById(R.id.btnSave);
            btn.setText("Cập nhật");
            setupButtonUpdate(findViewById(R.id.btnSave));
        } else {
            setupButtonSave(findViewById(R.id.btnSave));
        }

    }

    private void setupButtonUpdate(View v) {
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestQueue queue = VolleySingleton.getInstance().getRequestQueue();
                String url = getString(R.string.base_url) + "/menu/update-name";

                // Get menu
                EditText menuNameEditText = findViewById(R.id.editMenuName);
                String menuNameChange = menuNameEditText.getText().toString();

                if(menuNameChange.isEmpty()){
                    Toast.makeText(AddMenu.this, "Tên menu không được để trống", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Body
                JSONObject body = new JSONObject();
                try {
                    body.put("idMenu", idMenu);
                    body.put("name", menuNameChange);
                    body.put("idRestaurant", idRestaurant);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(AddMenu.this, "Lỗi tạo body", Toast.LENGTH_SHORT).show();
                    return;
                }
                loadingDialog.startLoadingDialog();

                // Request a JSON response from the provided URL.
                StringRequest request = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                loadingDialog.dissmissDialog();
                                Toast.makeText(AddMenu.this, "Cập nhật menu thành công!", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loadingDialog.dissmissDialog();
                        Toast.makeText(AddMenu.this, "Lỗi cập nhật menu", Toast.LENGTH_SHORT).show();
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

    private void setupButtonSave(View v) {
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestQueue queue = VolleySingleton.getInstance().getRequestQueue();
                String url = getString(R.string.base_url) + "/menu/create";

                // Get food name
                EditText menuNameEditText = findViewById(R.id.editMenuName);
                String menuName = menuNameEditText.getText().toString();

                // Body
                JSONObject body = new JSONObject();
                try {
                    body.put("idRestaurant", idRestaurant);
                    body.put("name", menuName);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(AddMenu.this, "Lỗi tạo body", Toast.LENGTH_SHORT).show();
                    return;
                }
                loadingDialog.startLoadingDialog();

                // Request a JSON response from the provided URL.
                StringRequest request = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                loadingDialog.dissmissDialog();
                                Toast.makeText(AddMenu.this, "Tạo menu thành công!", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loadingDialog.dissmissDialog();
                        Toast.makeText(AddMenu.this, "Lỗi tạo món", Toast.LENGTH_SHORT).show();
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



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}