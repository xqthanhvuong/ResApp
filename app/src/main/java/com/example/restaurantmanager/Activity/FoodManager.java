package com.example.restaurantmanager.Activity;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.bumptech.glide.Glide;
import com.example.restaurantmanager.API.VolleySingleton;
import com.example.restaurantmanager.Dialog.LoadingDialog;
import com.example.restaurantmanager.R;
import com.example.restaurantmanager.common.NavigationHelper;
import com.example.restaurantmanager.helper.DialogHelper;
import com.example.restaurantmanager.helper.NumberHelper;
import com.example.restaurantmanager.manager.PreferencesManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

class FoodItem {
    private final String id;
    private final String name;
    private final float price;
    private final String imageUrl;

    public FoodItem(String id, String name, float price, String imageUrl) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public Float getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getId() {
        return id;
    }
}

class FoodAdapter extends BaseAdapter {
    private final Context context;
    private final List<FoodItem> foodItems;
    private List<FoodItem> filteredFoodItems;
    private final String username;
    private final String password;
    private final String idMenu;

    public FoodAdapter(Context context, List<FoodItem> foodItems, String username, String password, String idMenu) {
        this.context = context;
        this.foodItems = foodItems;
        this.username = username;
        this.password = password;
        this.idMenu = idMenu;
        this.filteredFoodItems = new ArrayList<>(foodItems); // initialize with all items
    }

    @Override
    public int getCount() {
        return filteredFoodItems.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredFoodItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_food, parent, false);
        }

        ImageView foodImage = convertView.findViewById(R.id.foodImage);
        TextView foodName = convertView.findViewById(R.id.foodName);
        TextView foodPrice = convertView.findViewById(R.id.foodPrice);
        ImageView btnEdit = convertView.findViewById(R.id.btnEdit);
        ImageView btnDelete = convertView.findViewById(R.id.btnDelete);
        ImageView btnDetail = convertView.findViewById(R.id.btnView);

        FoodItem foodItem = filteredFoodItems.get(position);
        foodName.setText(foodItem.getName());
        foodPrice.setText(NumberHelper.formatNumber(foodItem.getPrice()) + " VNĐ");

        Glide.with(context)
                .load(foodItem.getImageUrl())
                .into(foodImage);

        // Set event listeners
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                // Open AddFood activity
                Intent intent = new Intent(context, AddFood.class);
                intent.putExtra("idMenu", idMenu);
                intent.putExtra("idFood", foodItem.getId());
                intent.putExtra("foodName", foodItem.getName());
                intent.putExtra("foodPrice", foodItem.getPrice());
                intent.putExtra("foodImage", foodItem.getImageUrl());
                context.startActivity(intent);
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Xác nhận")
                        .setMessage("Bạn có chắc chắn muốn xóa món ăn này không?")
                        .setPositiveButton("Có", (dialog, which) -> {
                            // Delete food
                            // Call API to delete food
                            RequestQueue queue = VolleySingleton.getInstance().getRequestQueue();
                            LoadingDialog loadingDialog = new LoadingDialog((Activity) context);
                            loadingDialog.startLoadingDialog();

                            String url = context.getString(R.string.base_url) + "/food/delete?idFood=" + foodItem.getId();
                            StringRequest request = new StringRequest(Request.Method.GET, url,
                                    new Response.Listener<String>() {
                                        @RequiresApi(api = Build.VERSION_CODES.O)
                                        @Override
                                        public void onResponse(String response) {
                                            // Request successful
                                            Toast.makeText(context, "Xóa món ăn thành công", Toast.LENGTH_SHORT).show();
                                            // Reload data
                                            if (context instanceof FoodManager) {
                                                ((FoodManager) context).reloadData();
                                            }
                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                    // Handle error
                                    Log.e("FoodAdapter", "Có lỗi, vui lòng thử lại!: " + error.getMessage());
                                    Toast.makeText(context, "Có lỗi, vui lòng thử lại!: ", Toast.LENGTH_SHORT).show();
                                }
                            }) {
                                @Override
                                public Map<String, String> getHeaders() {
                                    Map<String, String> params = new HashMap<>();
                                    // Add Basic Authentication header
                                    String credentials = username + ":" + password;
                                    String auth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                                    params.put("Authorization", auth);
                                    return params;
                                }
                            };

                            queue.add(request);
                        })
                        .setNegativeButton("Không", null)
                        .show();
            }
        });

        btnDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open ViewFood activity
                Intent intent = new Intent(context, ViewFood.class);
                intent.putExtra("idFood", foodItem.getId());
                context.startActivity(intent);
            }
        });

        return convertView;
    }

    public void filter(String text) {
        filteredFoodItems.clear();
        if (text.isEmpty()) {
            filteredFoodItems.addAll(foodItems);
        } else {
            text = text.toLowerCase();
            for (FoodItem item : foodItems) {
                if (item.getName().toLowerCase().contains(text)) {
                    filteredFoodItems.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }
}

@RequiresApi(api = Build.VERSION_CODES.O)
public class FoodManager extends AppCompatActivity {
    private static final int REQUEST_CHILD_ACTIVITY = 1002;

    private String idMenu;
    private String username;
    private String password;
    private FoodAdapter adapter;
    private ListView listFoods;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_manager);

        PreferencesManager preferencesManager = PreferencesManager.getInstance();
        username = preferencesManager.getUserName();
        password = preferencesManager.getPass();

        idMenu = Objects.requireNonNull(getIntent().getExtras()).getString("idMenu");

        // Set up title for the activity
        TextView title = findViewById(R.id.title);
        title.setText("Quản lý thực đơn");

        // Set up list foods
        setupListFoods();

        // Set up button back
        NavigationHelper.onBackClicked(findViewById(R.id.btnBackLo));

        // Set up button add food
        setupButtonAddFood(findViewById(R.id.btnAdd));

        // Set up list view
        listFoods = findViewById(R.id.listFoods);

        // Set up refresh layout
        setupRefreshLayout();
        EditText txtSearch = findViewById(R.id.txtSearch);


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

    private void setupButtonAddFood(View v) {
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open AddFood activity
                Intent intent = new Intent(FoodManager.this, AddFood.class);
                intent.putExtra("idMenu", idMenu);
                startActivityForResult(intent, REQUEST_CHILD_ACTIVITY);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode,@Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CHILD_ACTIVITY) {
            // Đây là nơi để xử lý sau khi Activity con đã đóng
            setupListFoods();
        }
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
                DialogHelper.showFailDialog("Có lỗi vui lòng thử lại sau!",FoodManager.this);
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
        List<FoodItem> foodItems = new ArrayList<>();

        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject food = jsonArray.getJSONObject(i);

                // Add food item to list
                foodItems.add(new FoodItem(food.getString("idFood"), food.getString("name"),
                        Float.parseFloat(food.getString("price")), food.getString("image")));
            }
        } catch (Exception e) {
            Log.e("FoodManager", "Error when parsing JSON: " + e.getMessage());
        }

        adapter = new FoodAdapter(this, foodItems, username, password, idMenu);
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