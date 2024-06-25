package com.example.restaurantmanager.Activity;

import androidx.annotation.RequiresApi;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.restaurantmanager.Dialog.LoadingDialog;
import com.example.restaurantmanager.R;
import com.example.restaurantmanager.common.NavigationHelper;
import com.example.restaurantmanager.helper.DialogHelper;
import com.example.restaurantmanager.manager.PreferencesManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

@RequiresApi(api = Build.VERSION_CODES.O)
public class AddTable extends AppCompatActivity {

    private String username = PreferencesManager.getInstance().getUserName();
    private String password = PreferencesManager.getInstance().getPass();

    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_table);
        loadingDialog = new LoadingDialog(AddTable.this);
        NavigationHelper.onBackClicked(findViewById(R.id.btnBack));

        setupButtonSave(findViewById(R.id.btnSave));
    }

    private void setupButtonSave(View v) {
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestQueue queue = Volley.newRequestQueue(AddTable.this);
                String url = getString(R.string.base_url) + "/table/create";

                Map<String, String> headers = new HashMap<>();
                String credentials = username + ":" + password;
                headers.put("Authorization", "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP));
                EditText nametable = findViewById(R.id.edtTableName);

                if(nametable.equals("")){
                    Toast.makeText(AddTable.this, "Tên bàn không được để trống", Toast.LENGTH_SHORT).show();
                    return;
                }

                JSONObject postData = new JSONObject();
                try{
                    postData.put("tableName", nametable.getText().toString());
                    postData.put("idRestaurant", PreferencesManager.getInstance().getRestaurantId());
                }catch (Exception e){
                    e.printStackTrace();
                }

                loadingDialog.startLoadingDialog();
                // Request a JSON response from the provided URL.
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, postData,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                loadingDialog.dissmissDialog();
                                Toast.makeText(AddTable.this, "Tạo bàn thành công!", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loadingDialog.dissmissDialog();
                        DialogHelper.showFailDialog("Có lỗi vui lòng thử lại sau!",AddTable.this);
                        Log.e("TableManager", "Error: " + error);
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        return headers;
                    }
                };

                // Add the request to the RequestQueue.
                queue.add(jsonObjectRequest);
            }
        });
    }
}