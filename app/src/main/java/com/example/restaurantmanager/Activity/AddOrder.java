package com.example.restaurantmanager.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.restaurantmanager.API.VolleySingleton;
import com.example.restaurantmanager.Dialog.LoadingDialog;
import com.example.restaurantmanager.R;
//import com.example.restaurantmanager.adapter.OrderFoodAdapter;
import com.example.restaurantmanager.adapter.OrderFoodAdapter;
import com.example.restaurantmanager.common.NavigationHelper;
import com.example.restaurantmanager.helper.DialogHelper;
import com.example.restaurantmanager.manager.PreferencesManager;
import com.example.restaurantmanager.model.OrderItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;



@RequiresApi(api = Build.VERSION_CODES.O)
public class AddOrder extends AppCompatActivity {

    private String idMenu;
    private String username;
    private String password;

    private String idTable;

    private String idBill;
    private String tableName;

    private  OrderFoodAdapter adapter;
    ListView listFoods;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_manager);

        PreferencesManager preferencesManager = PreferencesManager.getInstance();
        username = preferencesManager.getUserName();
        password = preferencesManager.getPass();

        idMenu = preferencesManager.getIdMenu();
        idTable = Objects.requireNonNull(getIntent().getExtras()).getString("idTable");
        tableName = Objects.requireNonNull(getIntent().getExtras()).getString("tableName");
        idBill = getIntent().getExtras().getString("idBill");




        // Set up title for the activity
        TextView title = findViewById(R.id.title);
        title.setText(tableName);

        // Set up list foods
        setupListFoods();

        EditText txtSearch = findViewById(R.id.txtSearch);
        listFoods = findViewById(R.id.listFoods);

        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing before text changes
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Do nothing after text changes
            }
        });

        // Set up button back
        NavigationHelper.onBackClicked(findViewById(R.id.btnBackLo));


        View btnAdd = findViewById(R.id.btnAdd);

        if (btnAdd != null) {
            // Tìm ViewGroup chứa TextView đó
            ViewGroup parent = (ViewGroup) btnAdd.getParent();

            if (parent != null) {
                // Xóa TextView từ ViewGroup
                parent.removeView(btnAdd);
            }
        }

        // Set up refresh layout
        setupRefreshLayout();
    }

    private void setupRefreshLayout() {
        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reloadData();
                pullToRefresh.setRefreshing(false);
            }
        });
    }
    // Set up list foods => load list foods from database
    private void setupListFoods() {
        RequestQueue queue = VolleySingleton.getInstance().getRequestQueue();
        LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.startLoadingDialog();
        // Request data from server
        String url = getString(R.string.base_url) + "/food/get?idMenu=" + idMenu;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        loadingDialog.dissmissDialog();
                        // Parse dữ liệu JSON và cập nhật UI
                        parseJsonAndUpdateUI(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingDialog.dissmissDialog();
                DialogHelper.showFailDialog("Có lỗi vui lòng thử lại sau!",AddOrder.this);
                Log.e("TableManager", "Error when loading table list: " + error.getMessage());
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

    private void parseJsonAndUpdateUI(JSONArray jsonArray) {
        // Parse JSON data and update UI

        List<OrderItem> foodItems = new ArrayList<>();

        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject food = jsonArray.getJSONObject(i);

                // Add food item to list
                foodItems.add(new OrderItem(food.getString("idFood"), food.getString("name"),
                        Float.parseFloat(food.getString("price")), food.getString("image")));
            }
        } catch (Exception e) {
            Log.e("FoodManager", "Error when parsing JSON: " + e.getMessage());
        }

        adapter = new OrderFoodAdapter(this, foodItems, idBill, idTable);
        listFoods.setAdapter(adapter);
    }

    public void reloadData() {
        setupListFoods();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}