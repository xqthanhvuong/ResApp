package com.example.restaurantmanager.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.restaurantmanager.Dialog.LoadingDialog;
import com.example.restaurantmanager.R;
import com.example.restaurantmanager.common.NavigationHelper;
import com.example.restaurantmanager.helper.DialogHelper;
import com.example.restaurantmanager.manager.PreferencesManager;
import com.google.android.material.snackbar.Snackbar;

import net.glxn.qrgen.android.QRCode;

import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TableManager extends AppCompatActivity {
    private static final int REQUEST_CHILD_ACTIVITY = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_manager);

        //Set up title for the activity
        TextView title = findViewById(R.id.title);
        title.setText("Quản lý bàn");

        setupTableList();
        setupRefreshLayout();

        NavigationHelper.onBackClicked(findViewById(R.id.btnBackLo));

        setupButtonAddTable(findViewById(R.id.btnAddTable));
    }

    private void setupButtonAddTable(View v) {
        v.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TableManager.this, AddTable.class);
                startActivityForResult(intent, REQUEST_CHILD_ACTIVITY);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode,@Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CHILD_ACTIVITY) {
            // Đây là nơi để xử lý sau khi Activity con đã đóng
            setupTableList();
        }
    }


    // Set up table list => load table list from api
    private void setupTableList() {
        LoadingDialog loadingDialog = new LoadingDialog(TableManager.this);
        loadingDialog.startLoadingDialog();
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = getString(R.string.base_url) + "/table/get?idRestaurant=" + PreferencesManager.getInstance().getRestaurantId();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Parse dữ liệu JSON và cập nhật UI
                        loadingDialog.dissmissDialog();
                        parseJsonAndUpdateUI(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingDialog.dissmissDialog();
                DialogHelper.showFailDialog("Có lỗi vui lòng thử lại sau",TableManager.this);
                Log.e("TableManager", "Error when loading table list: " + error.getMessage());
            }
        }) {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                // Add Basic Authentication header
                String username = PreferencesManager.getInstance().getUserName();
                String password = PreferencesManager.getInstance().getPass();
                String credentials = username + ":" + password;
                String auth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                params.put("Authorization", auth);

                return params;
            }
        };

        // Add the request to the RequestQueue.
        queue.add(jsonArrayRequest);
    }

    private void parseJsonAndUpdateUI(JSONArray jsonArray) {
        try {
            GridLayout tableList = findViewById(R.id.gridTable);
            tableList.removeAllViews();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject table = jsonArray.getJSONObject(i);

                String idTable = table.getString("idTable");
                String status = table.getString("status");
                String name = table.getString("tableName");

                // Tạo table item từ layout
                LinearLayout tableItem = (LinearLayout) getLayoutInflater().inflate(R.layout.item_table, null);
                TextView tableName = tableItem.findViewById(R.id.txtTableName);
                ImageView imageTable = tableItem.findViewById(R.id.imageTable);
                if(status.equals("Unavailable")){
                    imageTable.setImageResource(R.drawable.table_red);
                }
                tableItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(TableManager.this, ViewFoodOrder.class);
                        intent.putExtra("idTable", idTable);
                        intent.putExtra("tableName", name);
                        startActivityForResult(intent, REQUEST_CHILD_ACTIVITY);
                    }
                });
                tableItem.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        PopupMenu popup = new PopupMenu(TableManager.this, v);
                        popup.getMenuInflater().inflate(R.menu.popup_menu_table, popup.getMenu());
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                if(item.getItemId() == R.id.option_qrcode) {
                                    showAlertDialog(getString(R.string.web_url)+"/menu?a="+PreferencesManager.getInstance().getIdMenu()+"&b="+PreferencesManager.getInstance().getRestaurantId()+"&c="+idTable);
                                    return true;
                                } else {
                                    return false;
                                }
                            }
                        });
                        popup.show();
                        return true;
                    }
                });
                // Đặt tên bàn
                tableName.setText(name);

                // Đặt layout params cho table item
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = 0; // Sử dụng 0dp width
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
                tableItem.setLayoutParams(params);

                // Thêm table item vào table list
                tableList.addView(tableItem);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public void reloadData() {
        setupTableList();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void showAlertDialog(String url){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_qr_code, null);
        builder.setView(dialogView);
        ImageView imageViewQRCode = dialogView.findViewById(R.id.imageQRCode);
        Bitmap qrBitmap = generateQRCode(url);
        imageViewQRCode.setImageBitmap(qrBitmap);
        AlertDialog dialog = builder.create();

        Button buttonSave = dialogView.findViewById(R.id.buttonSave);
        Button buttonClose = dialogView.findViewById(R.id.btnClose);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveQRCode(qrBitmap);
            }
        });

        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    private Bitmap generateQRCode(String text) {
        return QRCode.from(text).withSize(500, 500).bitmap(); // Độ lớn của mã QR Code
    }


    private void saveQRCode(Bitmap bitmap) {
        // Kiểm tra và tạo thư mục lưu trữ
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "QR Codes");
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        // Tạo tên file duy nhất dựa trên thời gian
        String fileName = "QR_" + System.currentTimeMillis() + ".png";
        File file = new File(storageDir, fileName);

        try {
            // Lưu bitmap vào file
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();

            // Cập nhật MediaStore để hệ thống biết về file mới
            updateMediaStore(file);

            // Hiển thị thông báo lưu thành công
            DialogHelper.showSuccessDialog("Đã lưu thành công " + fileName, this);

        } catch (IOException e) {
            e.printStackTrace();
            // Hiển thị thông báo lưu thất bại
            DialogHelper.showFailDialog("Lưu thât bại", this);
        }
    }

    private void updateMediaStore(File file) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
    }


}