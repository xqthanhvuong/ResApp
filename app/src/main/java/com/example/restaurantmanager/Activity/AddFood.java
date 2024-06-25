package com.example.restaurantmanager.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.util.Function;
import androidx.core.util.Consumer;
import androidx.core.util.Predicate;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.cloudinary.Cloudinary;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.restaurantmanager.API.VolleySingleton;
import com.example.restaurantmanager.Dialog.LoadingDialog;
import com.example.restaurantmanager.R;
import com.example.restaurantmanager.common.NavigationHelper;
import com.example.restaurantmanager.manager.CloudinarySingleton;
import com.example.restaurantmanager.manager.PreferencesManager;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@RequiresApi(api = Build.VERSION_CODES.O)
public class AddFood extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Cloudinary cloudinary;
    private String username = PreferencesManager.getInstance().getUserName();
    private String password = PreferencesManager.getInstance().getPass();
    private String idMenu;
    private EditText txtFoodName;
    private EditText txtFoodPrice;
    private TextView txtImageSrc;
    private ImageView imgFoodImage;
    private String idFood;
    private TextView btnSave;
    private TextView title;

    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);

        //Setup layout
        txtFoodName = findViewById(R.id.foodName);
        txtFoodPrice = findViewById(R.id.foodPrice);
        txtImageSrc = findViewById(R.id.imageSrc);
        imgFoodImage = findViewById(R.id.foodImage);
        btnSave = findViewById(R.id.btnSave);
        title = findViewById(R.id.title);

        loadingDialog = new LoadingDialog(AddFood.this);

        // Get idMenu from intent
        idMenu = Objects.requireNonNull(getIntent().getExtras()).getString("idMenu");

        // Set up button back
        NavigationHelper.onBackClicked(findViewById(R.id.btnBack));

        // Set up button choose image
        chooseImage(findViewById(R.id.btnChooseImage));


        cloudinary = CloudinarySingleton.getInstance();

        if(getIntent().getExtras().getString("idFood") != null) {
            idFood = getIntent().getExtras().getString("idFood");
            txtFoodName.setText(getIntent().getExtras().getString("foodName"));
            txtFoodPrice.setText(getIntent().getExtras().getString("foodPrice"));
            txtImageSrc.setText(getIntent().getExtras().getString("foodImage"));
            Glide.with(this)
                    .load(txtImageSrc.getText().toString())
                    .into(imgFoodImage);
            title.setText("Cập nhật món");
            btnSave.setText("Cập nhật");
            setupButtonUpdate(btnSave);
        } else {
            // Set up button save
            setupButtonSave(btnSave);
            title.setText("Thêm món");
        }

    }

    private void setupButtonUpdate(View v) {
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String foodName = "";
                String foodPrice = "";
                String foodImage = "";

                // Get food name and price from input
                EditText txtFoodName = findViewById(R.id.foodName);
                EditText txtFoodPrice = findViewById(R.id.foodPrice);
                EditText txtImageSrc = findViewById(R.id.imageSrc);

                foodImage = txtImageSrc.getText().toString();
                foodName = txtFoodName.getText().toString();
                foodPrice = txtFoodPrice.getText().toString();

                // Validate input
                if (StringUtils.isBlank(foodName) || StringUtils.isBlank(foodPrice) || foodImage.equals("logo")) {
                    Toast.makeText(AddFood.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }

                String url = getString(R.string.base_url) + "/food/update";

                // Call API to save food
                RequestQueue queue = VolleySingleton.getInstance().getRequestQueue();



                loadingDialog.startLoadingDialog();
                // Create JSON body
                JSONObject body = new JSONObject();
                try {
                    body.put("idMenu", idMenu);
                    body.put("idFood", idFood);
                    body.put("name", foodName);
                    body.put("price", foodPrice);
                    body.put("image", foodImage);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(AddFood.this, "Error creating JSON body", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create headers
                Map<String, String> headers = new HashMap<>();
                String credentials = username + ":" + password;
                headers.put("Authorization", "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP));

                // Create request
                StringRequest request = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                loadingDialog.dissmissDialog();
                                // Request successful
                                Toast.makeText(AddFood.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                                // Đóng Activity nếu cần
                                 finish();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loadingDialog.dissmissDialog();
                        Toast.makeText(AddFood.this, "Lỗi cập nhật món: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        return body.toString().getBytes(StandardCharsets.UTF_8);
                    }

                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8";
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        return headers;
                    }
                };

                // Add request to RequestQueue
                queue.add(request);
            }});
    }

    //Button choose image
    public void chooseImage(View view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Call API to choose image
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });
    }

    //Call API to save food and close this activity
    private void setupButtonSave(View v) {
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String foodName = "";
                String foodPrice = "";
                String foodImage = "";

                // Get food name and price from input
                EditText txtFoodName = findViewById(R.id.foodName);
                EditText txtFoodPrice = findViewById(R.id.foodPrice);
                EditText txtImageSrc = findViewById(R.id.imageSrc);

                foodImage = txtImageSrc.getText().toString();
                foodName = txtFoodName.getText().toString();
                foodPrice = txtFoodPrice.getText().toString();

                // Validate input
                if (StringUtils.isBlank(foodName) || StringUtils.isBlank(foodPrice) || foodImage.equals("logo")) {
                    Toast.makeText(AddFood.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }

                String url = getString(R.string.base_url) + "/food/create";

                // Call API to save food
                RequestQueue queue = VolleySingleton.getInstance().getRequestQueue();

                // Create JSON body
                JSONObject body = new JSONObject();
                try {
                    body.put("idMenu", idMenu);
                    body.put("name", foodName);
                    body.put("price", foodPrice);
                    body.put("image", foodImage);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(AddFood.this, "Lỗi tạo body", Toast.LENGTH_SHORT).show();
                    return;
                }

                loadingDialog.startLoadingDialog();
                // Create headers
                Map<String, String> headers = new HashMap<>();
                String credentials = username + ":" + password;
                headers.put("Authorization", "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP));

                // Create request
                StringRequest request = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                loadingDialog.dissmissDialog();
                                // Request successful
                                Toast.makeText(AddFood.this, "Thêm thành công", Toast.LENGTH_SHORT).show();
                                // Đóng Activity nếu cần
                                finish();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loadingDialog.dissmissDialog();
                        Toast.makeText(AddFood.this, "Lỗi lưu món", Toast.LENGTH_SHORT).show();
                    }
                }) {
                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        return body.toString().getBytes(StandardCharsets.UTF_8);
                    }

                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8";
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        return headers;
                    }
                };

                // Add request to RequestQueue
                queue.add(request);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            //Get image from gallery
            Uri uri = data.getData();

            //Upload to cloudinary
            uploadImage(uri);
        }
    }

    //Upload image to cloudinary
    public void uploadImage(Uri imageUri) {
        MediaManager.get().upload(imageUri)
                .unsigned("uwkwlwgp") // Nếu bạn sử dụng preset unsigned
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                        // Upload started
                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {
                        // Progress
                    }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        // Success
                        String url = Objects.requireNonNull(resultData.get("url")).toString();
                        Log.d("Cloudinary", "Upload successful: " + url);
                        displayImage(url);
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        // Error
                        Log.e("Cloudinary", "Upload error: " + error.getDescription());
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {
                        // Reschedule
                    }
                })
                .dispatch();
    }

    private void displayImage(String url) {
        ImageView imageView = findViewById(R.id.foodImage);
        Glide.with(this)
                .load(url)
                .into(imageView);
        EditText imageSrc = findViewById(R.id.imageSrc);
        imageSrc.setText(url);
    }

    // Download image from url and set to ImageView


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}