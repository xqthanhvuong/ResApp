package com.example.restaurantmanager.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.app.DatePickerDialog;
import android.widget.DatePicker;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.cloudinary.Cloudinary;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.restaurantmanager.Dialog.LoadingDialog;
import com.example.restaurantmanager.R;
import com.example.restaurantmanager.common.NavigationHelper;
import com.example.restaurantmanager.manager.CloudinarySingleton;
import com.example.restaurantmanager.manager.PreferencesManager;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;



public class EditProfile extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private Cloudinary cloudinary;
    private String username;
    private String password;
    private ImageView imgImage;
    private String idAccount;
    private String avtSource;
    private EditText txtName;

    private EditText txtBirthDate;
    private TextView avtResource;

    private LoadingDialog loadingDialog;

    private PreferencesManager preferencesManager;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        // Set up button back
        NavigationHelper.onBackClicked(findViewById(R.id.btnBack));

        preferencesManager = PreferencesManager.getInstance();

        // Get food name and price from input
        txtName = findViewById(R.id.txtName);
        txtBirthDate = findViewById(R.id.txtBirthDate);
        avtResource = findViewById(R.id.avtResource);
        txtName.setText(PreferencesManager.getInstance().getName());
        loadingDialog = new LoadingDialog(EditProfile.this);

        txtBirthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(EditProfile.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                txtBirthDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });


        // Set up button choose image
        chooseImage(findViewById(R.id.btnAddAvt));

        // Set up button save
        setupButtonSave(findViewById(R.id.btnSave));

        imgImage = findViewById(R.id.avt);

        cloudinary = CloudinarySingleton.getInstance();

        idAccount = preferencesManager.getId();
        username = preferencesManager.getUserName();
        password = preferencesManager.getPass();
        if(preferencesManager.getAvt()!=null){
            avtSource = preferencesManager.getAvt();
            Glide.with(this)
                    .load(avtSource)
                    .into(imgImage);
        }
        if(preferencesManager.getBirthday()!=null){
            txtBirthDate.setText(preferencesManager.getBirthday());
        }
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
                String name = "";
                String avt = "";
                String birth = "";


                name = txtName.getText().toString();
                avt = avtResource.getText().toString();
                birth = txtBirthDate.getText().toString();

                // Validate input
                if (StringUtils.isBlank(name) || StringUtils.isBlank(birth)) {
                    Toast.makeText(EditProfile.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }

                String url = getString(R.string.base_url) + "/account/update";

                // Call API to save food
                RequestQueue queue = Volley.newRequestQueue(EditProfile.this);

                // Create JSON body
                JSONObject body = new JSONObject();
                try {
                    body.put("idAccount", idAccount);
                    body.put("name", name);
                    body.put("birthDate", birth);
                    body.put("avt", avt);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(EditProfile.this, "Lỗi tạo body", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create headers
                Map<String, String> headers = new HashMap<>();
                String credentials = username + ":" + password;
                headers.put("Authorization", "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP));

                loadingDialog.startLoadingDialog();
                // Create request
                StringRequest request = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                loadingDialog.dissmissDialog();
                                // Request successful
                                PreferencesManager preferencesManager = PreferencesManager.getInstance();
                                preferencesManager.saveBirtday(txtBirthDate.getText().toString());
                                preferencesManager.saveName(txtName.getText().toString());
                                preferencesManager.saveAvt(avtResource.getText().toString());

                                Toast.makeText(EditProfile.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                                // Đóng Activity nếu cần
                                finish();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loadingDialog.dissmissDialog();
                        Toast.makeText(EditProfile.this, "Lỗi cập nhật", Toast.LENGTH_SHORT).show();
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
        ImageView imageView = findViewById(R.id.avt);
        Glide.with(EditProfile.this)
                .load(url)
                .into(imageView);
        avtResource.setText(url);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}