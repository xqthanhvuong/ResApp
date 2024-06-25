package com.example.restaurantmanager.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.restaurantmanager.API.VolleySingleton;
import com.example.restaurantmanager.R;
import com.example.restaurantmanager.adapter.BillAdapter;
import com.example.restaurantmanager.common.NavigationHelper;
import com.example.restaurantmanager.helper.DialogHelper;
import com.example.restaurantmanager.manager.PreferencesManager;
import com.example.restaurantmanager.model.Bill;
import com.example.restaurantmanager.model.FoodItemBill;

import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

class FoodOrderItem {
    private String id;
    private String name;
    private String imageUrl;
    private int quantity;
    private String statusOrder;
    private String statusPayment;

    public FoodOrderItem(String id, String name, String imageUrl, int quantity, String statusOrder, String statusPayment) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.quantity = quantity;
        this.statusOrder = statusOrder;
        this.statusPayment = statusPayment;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getId() {
        return id;
    }

    public int getQuantity () {
        return quantity;
    }

    public String getStatusOrder() {
        return  statusOrder;
    }

    public String getStatusPayment(){
        return statusPayment;
    }

}

class FoodOrderAdapter extends BaseAdapter {
    private Context context;
    private List<FoodOrderItem> foodItems;

    public FoodOrderAdapter(Context context, List<FoodOrderItem> foodItems) {
        this.context = context;
        this.foodItems = foodItems;
    }

    @Override
    public int getCount() {
        return foodItems.size();
    }

    @Override
    public Object getItem(int position) {
        return foodItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_food_order, parent, false);
        }

        ImageView foodImage = convertView.findViewById(R.id.foodImage);
        TextView foodName = convertView.findViewById(R.id.foodName);
        TextView quantity = convertView.findViewById(R.id.quantity);
        ImageView statusOrder = convertView.findViewById(R.id.statusOrder);
        ImageView statusPayment = convertView.findViewById(R.id.statusPayment);

        FoodOrderItem foodItem = foodItems.get(position);
        foodName.setText(foodItem.getName());
        quantity.setText("Số lượng: " + foodItem.getQuantity());
        if(!foodItem.getStatusOrder().equals("Processing")){
            statusOrder.setImageResource(R.drawable.done);
        }
        if(foodItem.getStatusPayment().equals("Paid")) {
            statusPayment.setImageResource(R.drawable.done);
        }

        Glide.with(context)
                .load(foodItem.getImageUrl())
                .into(foodImage);
        return convertView;
    }
}

public class ViewFoodOrder extends AppCompatActivity {

    private static final int REQUEST_CHILD_ACTIVITY = 1001;
    private String username;
    private String password;
    private String idTable;
    private String tableName;

    private String idBill;

    private TextView btnAdd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order);
        TextView title = findViewById(R.id.nameTable);
        btnAdd = findViewById(R.id.btnAdd);
        setupButtonAddFood(btnAdd);
        ImageView getBill = findViewById(R.id.getbill);


        idTable = Objects.requireNonNull(getIntent().getExtras()).getString("idTable");
        tableName = Objects.requireNonNull(getIntent().getExtras()).getString("tableName");

        title.setText(tableName);

        PreferencesManager preferencesManager = PreferencesManager.getInstance();
        username = preferencesManager.getUserName();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            password = preferencesManager.getPass();
        }
        //Set up list foods
        setupListFoods();

        //set up backbutton
        NavigationHelper.onBackClicked(findViewById(R.id.btnBack));


        //Set up button add food
        setupButtonAddFood(findViewById(R.id.btnAdd));



        getBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(idBill!=null){
                    fetchBillData(idBill);
                }else {
                    DialogHelper.showFailDialog("Không có bill nào được tìm thấy",ViewFoodOrder.this);
                }

            }
        });


        // Set up refresh layout
        setupRefreshLayout();
    }

    private void makeRequest() {
        String url = getString(R.string.base_url)+"/bills/get-id-bill?idTable=" + idTable;

        // Tạo RequestQueue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        // Tạo StringRequest
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        idBill = response;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });

        // Thêm request vào RequestQueue
        requestQueue.add(stringRequest);
    }

    private void setupRefreshLayout(){
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
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewFoodOrder.this, AddOrder.class);
                intent.putExtra("idTable", idTable);
                intent.putExtra("tableName", tableName);
                intent.putExtra("idBill",idBill);
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
        //set upp bill
        makeRequest();
        RequestQueue queue = Volley.newRequestQueue(this);
        // Request data from server
        String url = getString(R.string.base_url) + "/food/get-by-id-table?idTable=" + idTable;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Parse dữ liệu JSON và cập nhật UI
                        parseJsonAndUpdateUI(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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

        queue.add(request);
    }


    private void parseJsonAndUpdateUI(JSONArray jsonArray) {
        // Parse JSON data and update UI
        ListView listFoods = findViewById(R.id.listFoodOrder);
        List<FoodOrderItem> foodItems = new ArrayList<>();

        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject food = jsonArray.getJSONObject(i);
                // Add food item to list
                foodItems.add(new FoodOrderItem(food.getString("idFood"), food.getString("name"), food.getString("image"), Integer.parseInt(food.getString("quantity")), food.getString("statusOrder"), food.getString("statusPayment")));
            }
        } catch (Exception e) {
            Log.e("FoodManager", "Error when parsing JSON: " + e.getMessage());
        }


        FoodOrderAdapter adapter = new FoodOrderAdapter(this, foodItems);
        listFoods.setAdapter(adapter);
    }

    public void reloadData() {
        setupListFoods();
    }

    private void fetchBillData(String idBill) {
        RequestQueue requestQueue = VolleySingleton.getInstance().getRequestQueue();
        String url = getString(R.string.base_url)+"/bills/get?idBill=" + idBill;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Chuyển đổi kết quả trả về thành đối tượng Bill
                            Bill bill = parseBillResponse(response);
                            // Hiển thị Dialog với dữ liệu từ API
                            showBillDialog(bill);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ViewFoodOrder.this, "Lỗi khi xử lý dữ liệu", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ViewFoodOrder.this, "Lỗi khi gọi API", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        requestQueue.add(jsonObjectRequest);
    }

    private Bill parseBillResponse(JSONObject response) throws JSONException {
        Bill bill = new Bill();

        bill.setIdTable(response.getString("idTable"));

        JSONArray foodsArray = response.getJSONArray("foods");
        List<FoodItemBill> foodItems = new ArrayList<>();
        for (int i = 0; i < foodsArray.length(); i++) {
            JSONObject foodObject = foodsArray.getJSONObject(i);
            FoodItemBill foodItem = new FoodItemBill();
            foodItem.setIdFood(foodObject.getString("idFood"));
            foodItem.setName(foodObject.getString("name"));
            foodItem.setPrice(foodObject.getDouble("price"));
            foodItem.setImage(foodObject.getString("image"));
            foodItem.setQuantity(foodObject.getInt("quantity"));
            foodItems.add(foodItem);
        }
        bill.setFoods(foodItems);
        bill.setTotal(response.getDouble("total"));
        bill.setStatus(response.getString("status"));

        return bill;
    }

    private void showBillDialog(Bill bill) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_bill, null);
        builder.setView(dialogView);

        ListView listView = dialogView.findViewById(R.id.listViewBill);
        TextView textViewTotal = dialogView.findViewById(R.id.textViewTotal);
        Button buttonPay = dialogView.findViewById(R.id.buttonPay);

        BillAdapter adapter = new BillAdapter(this, bill.getFoods());
        listView.setAdapter(adapter);

        textViewTotal.setText(String.format("Tổng tiền: %,d VNĐ", (int) bill.getTotal()));

        buttonPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeBill(idBill);
                // Xử lý thanh toán
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void closeBill(String idBill) {
        RequestQueue requestQueue = VolleySingleton.getInstance().getRequestQueue();
        String url = getString(R.string.base_url)+ "/bills/close-bill?idBill=" + idBill;

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if ("OK".equals(response)) {
                            DialogHelper.showSuccessDialog("Thanh toán thành công",ViewFoodOrder.this);
                            Toast.makeText(ViewFoodOrder.this, "Bill closed successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            DialogHelper.showFailDialog("Thanh toán thất bại",ViewFoodOrder.this);
                            Toast.makeText(ViewFoodOrder.this, "Failed to close bill", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        DialogHelper.showFailDialog("Thanh toán thất bại",ViewFoodOrder.this);
                        Toast.makeText(ViewFoodOrder.this, "Failed to close bill", Toast.LENGTH_SHORT).show();
                    }
                }){
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

        requestQueue.add(stringRequest);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
