package com.example.restaurantmanager;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.restaurantmanager.API.VolleySingleton;
import com.example.restaurantmanager.Activity.Login;
import com.example.restaurantmanager.Activity.ManagerMenu;
import com.example.restaurantmanager.Activity.OrderFragment;
import com.example.restaurantmanager.Dialog.OTPDialog;
import com.example.restaurantmanager.databinding.ActivityMainBinding;
import com.example.restaurantmanager.helper.CheckHelper;
import com.example.restaurantmanager.manager.PreferencesManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


@RequiresApi(api = Build.VERSION_CODES.O)
public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    private String username;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        PreferencesManager preferencesManager = PreferencesManager.getInstance();
        username=preferencesManager.getUserName();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            password=preferencesManager.getPass();
        }
        setContentView(binding.getRoot());

        if(CheckHelper.isNullOrEmpty(PreferencesManager.getInstance().getIdMenu()) && !CheckHelper.isNullOrEmpty(PreferencesManager.getInstance().getRestaurantId())){
            RequestQueue queue = VolleySingleton.getInstance().getRequestQueue();
            String url = getString(R.string.base_url) + "/menu/get?idRestaurant=" + PreferencesManager.getInstance().getRestaurantId();

            // Request data from server
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            // Handle response
                            try {
                                JSONObject js = response.getJSONObject(0);
                                PreferencesManager.getInstance().saveIdMenu(js.getString("idMenu"));
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
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

        if(PreferencesManager.getInstance().getRestaurantId()==null){
            RequestQueue requestQueue = VolleySingleton.getInstance().getRequestQueue();
            //url
            String url = getString(R.string.base_url);
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Nhập tên nhà hàng của bạn!");
            EditText input = new EditText(MainActivity.this);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

//                    if(CheckHelper.isNullOrEmpty(input.getText().toString())){
//                        Toast.makeText(MainActivity.this, "Vui lòng nhập tên nhà hàng", Toast.LENGTH_SHORT).show();
//                        // hiện dialog
//                        builder.show();
//                    }

                    Map<String, String> headers = new HashMap<>();
                    String credentials = username + ":" + password;
                    headers.put("Authorization", "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP));
                    JSONObject postData = new JSONObject();
                    String userInput = input.getText().toString();
                    try{
                        postData.put("name",userInput);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                            url + "/restaurant/create", postData, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                PreferencesManager.getInstance().saveRestaurantId(response.getString("idRestaurant"));
                                Toast.makeText(MainActivity.this, "Nhà hàng của bạn đã tạo thành công", Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(MainActivity.this, "Lỗi khi tạo nhà hàng: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }){
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            return headers;
                        }
                    };
                    requestQueue.add(jsonObjectRequest);
                }
            });
            builder.setCancelable(false);
            builder.show();
        }



        replaceFragment(new HomeFragment());
        binding.bottomNavigationView.setSelectedItemId(R.id.home);


        // Set up the toolbar
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.home) {
                replaceFragment(new HomeFragment());
            } else if (itemId == R.id.profile) {
                replaceFragment(new ProfileFragment());
            } else if (itemId == R.id.manager) {
                replaceFragment(new ManagerFragment());
            } else if (itemId == R.id.noti) {
                replaceFragment(new NotiFragment());
            } else if (itemId == R.id.order) {
                replaceFragment(new OrderFragment(preferencesManager.getRestaurantId(),username,password));
            }
            else {
                return false;
            }
            return true;
        });
    }


    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}