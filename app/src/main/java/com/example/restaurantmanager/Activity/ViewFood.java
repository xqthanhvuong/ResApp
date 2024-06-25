package com.example.restaurantmanager.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.restaurantmanager.Dialog.LoadingDialog;
import com.example.restaurantmanager.R;
import com.example.restaurantmanager.common.NavigationHelper;
import com.example.restaurantmanager.helper.DialogHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class ViewFood extends AppCompatActivity {

    private String idFood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_food);

        idFood = Objects.requireNonNull(getIntent().getExtras()).getString("idFood");

        // Set up title for the activity
        setTitle("Xem thực đơn");

        // Set up button back
        NavigationHelper.onBackClicked(findViewById(R.id.btnBackLo));

        setupViewFood();

        findViewById(R.id.btnDelete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFood();
            }
        });

    }

    private void setupViewFood() {
        LoadingDialog loadingDialog = new LoadingDialog(ViewFood.this);
        loadingDialog.startLoadingDialog();
        // Get food by idMenu
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = getString(R.string.base_url) + "/food/get-by-id?idFood=" + idFood;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            loadingDialog.dissmissDialog();
                            // Assuming the response itself is the food JSONObject
                            String id = response.getString("idFood");
                            String name = response.getString("name");
                            String price = response.getString("price");
                            String image = response.getString("image");
                            // Set up view food
                            updateUI(id, name, price, image);
                        } catch (JSONException e) {
                            Log.e("Error json: ", "Errol");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingDialog.dissmissDialog();
                DialogHelper.showFailDialog("Có lỗi vui lòng thử lại sau",ViewFood.this);
                Log.e("Error", "errol");
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    private void updateUI(String id, String name, String price, String image) {
        ImageView foodImage = findViewById(R.id.foodImage);
        TextView foodName = findViewById(R.id.foodName);
        TextView foodPrice = findViewById(R.id.foodPrice);
        TextView idFood = findViewById(R.id.idFood);

        foodName.setText(name);
        foodPrice.setText(price);
        idFood.setText(id);

        Glide.with(ViewFood.this)
                .load(image)
                .into(foodImage);

    }

    private void deleteFood() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = getString(R.string.base_url) + "/food/delete?idFood=" + idFood;
        StringRequest jsonObjectRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(ViewFood.this, "Xóa thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("Error", "Errol");
            }
        });

        requestQueue.add(jsonObjectRequest);
    }
}