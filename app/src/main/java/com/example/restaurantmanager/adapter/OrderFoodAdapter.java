package com.example.restaurantmanager.adapter;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.example.restaurantmanager.API.VolleySingleton;
import com.example.restaurantmanager.R;
import com.example.restaurantmanager.helper.NumberHelper;
import com.example.restaurantmanager.manager.PreferencesManager;
import com.example.restaurantmanager.model.OrderItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderFoodAdapter extends BaseAdapter {
    private final Context context;
    private final List<OrderItem> foodItems;

    private final String idBill;

    private final String idTable;

    private List<OrderItem> filteredFoodItems;
    public OrderFoodAdapter(Context context, List<OrderItem> foodItems, String idBill, String idTable) {
        this.context = context;
        this.foodItems = foodItems;
        this.idBill = idBill;
        this.idTable = idTable;
        this.filteredFoodItems =  new ArrayList<>(foodItems);
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_add_food, parent, false);
        }
        OrderItem foodItem = filteredFoodItems.get(position);

        ImageView foodImage = convertView.findViewById(R.id.foodImage);
        TextView foodName = convertView.findViewById(R.id.foodName);
        TextView foodPrice = convertView.findViewById(R.id.foodPrice);
        EditText quantity = convertView.findViewById(R.id.quantity);
        Button buttonIncrease = convertView.findViewById(R.id.buttonIncrease);
        Button buttonDecrease = convertView.findViewById(R.id.buttonDecrease);

        LinearLayout linearLayout =(LinearLayout) convertView.findViewById(R.id.foodorder);

        linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                PopupMenu popup = new PopupMenu(context, v);
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getItemId() == R.id.option_add) {
                            makeRequest(idBill,foodItem.getId(),Integer.parseInt(quantity.getText().toString()), PreferencesManager.getInstance().getRestaurantId(), idTable);
                            return true;
                        }else {
                            return false;
                        }
                    }
                });
                popup.show();
                return true;
            }
        });

        buttonIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentNumber = Integer.parseInt(quantity.getText().toString());
                quantity.setText(String.valueOf(currentNumber + 1));
            }
        });
        buttonDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentNumber = Integer.parseInt(quantity.getText().toString());
                if (currentNumber > 1) { // Để tránh số lượng âm
                    quantity.setText(String.valueOf(currentNumber - 1));
                }
            }
        });

        foodName.setText(foodItem.getName());
        foodPrice.setText(NumberHelper.formatNumber(foodItem.getPrice())+" Đ");

        Glide.with(context)
                .load(foodItem.getImageUrl())
                .into(foodImage);


        return convertView;
    }

    public void filter(String text) {
        filteredFoodItems.clear();
        if (text.isEmpty()) {
            filteredFoodItems.addAll(foodItems);
        } else {
            text = text.toLowerCase();
            for (OrderItem item : foodItems) {
                if (item.getName().toLowerCase().contains(text)) {
                    filteredFoodItems.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    private void makeRequest(String idBill, String idFood, int quantity, String idRestaurant, String idTable) {
        String url = context.getString(R.string.base_url)+"/bills/order";

        // Tạo dữ liệu JSON để gửi lên server
        JSONObject postData = new JSONObject();
        try {
            postData.put("idBill", idBill);
            postData.put("idFood", idFood);
            postData.put("quantity", quantity);
            postData.put("idRestaurant", idRestaurant);
            postData.put("idTable", idTable);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Tạo RequestQueue
        RequestQueue requestQueue = VolleySingleton.getInstance().getRequestQueue();

        // Tạo JsonObjectRequest
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                postData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Xử lý response thành công
                        ((Activity) context).finish();

                        Toast.makeText(context, "Đơn hàng đã được đặt thành công", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Xử lý lỗi
                        String errorMessage = "Lỗi không xác định";

                        if (error.networkResponse != null) {
                            if (error.networkResponse.statusCode == 400) {
                                errorMessage = "Bad Request: Dữ liệu không hợp lệ.";
                            } else if (error.networkResponse.statusCode == 500) {
                                errorMessage = "Internal Server Error: Có lỗi xảy ra trên server.";
                            } else if (error.networkResponse.statusCode == 409) {
                                errorMessage = "Conflict: Đơn hàng bị xung đột.";
                            } else {
                                errorMessage = "Lỗi: " + error.networkResponse.statusCode;
                            }
                        }

                        // Hiển thị lỗi lên màn hình
                        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        // Thêm request vào RequestQueue
        requestQueue.add(jsonObjectRequest);
    }
}