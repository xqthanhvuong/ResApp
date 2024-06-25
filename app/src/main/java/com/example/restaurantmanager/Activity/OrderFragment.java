package com.example.restaurantmanager.Activity;

import android.app.Activity;
import android.content.Context;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;


import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
import com.example.restaurantmanager.helper.DialogHelper;
import com.example.restaurantmanager.manager.PreferencesManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;


class Order {
    private String idTable;
    private String nameTable;
    private String idFood;
    private String nameFood;
    private String price;
    private String image;
    private String quantity;

    private String idOrder;

    public String getIdOrder() {
        return idOrder;
    }

    public void setIdOrder(String idOrder) {
        this.idOrder = idOrder;
    }

    public Order(String idTable, String nameTable, String idOrder, String idFood, String nameFood, String price, String image, String quantity) {
        this.idTable = idTable;
        this.idOrder = idOrder;
        this.nameTable = nameTable;
        this.idFood = idFood;
        this.nameFood = nameFood;
        this.price = price;
        this.image = image;
        this.quantity = quantity;
    }

    public Order(String idTable, String nameTable, String idFood, String nameFood, String price, String image, String quantity) {
        this.idTable = idTable;
        this.nameTable = nameTable;
        this.idFood = idFood;
        this.nameFood = nameFood;
        this.price = price;
        this.image = image;
        this.quantity = quantity;
    }

    public String getIdTable() {
        return idTable;
    }

    public String getNameTable() {
        return nameTable;
    }

    public String getIdFood() {
        return idFood;
    }

    public String getNameFood() {
        return nameFood;
    }

    public String getPrice() {
        return price;
    }

    public String getImage() {
        return image;
    }

    public String getQuantity() {
        return quantity;
    }
}

class OrderAdapter extends BaseAdapter {

    private OrderFragment orderFragment;
    private Context context;
    private List<Order> listOrder;

    public OrderAdapter(Context context, List<Order> listOrder, OrderFragment orderFragment) {
        this.context = context;
        this.listOrder = listOrder;
        this.orderFragment=orderFragment;
    }

    @Override
    public int getCount() {
        return listOrder.size();
    }

    @Override
    public Object getItem(int position) {
        return listOrder.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_order_detail, parent, false);
        }
        // Get order
        Order order = listOrder.get(position);
        // Get view
        TextView tvNameTable = view.findViewById(R.id.nameTable);
        TextView tvNameFood = view.findViewById(R.id.foodName);
        ImageView foodImage = view.findViewById(R.id.foodImage);
        TextView tvQuantity = view.findViewById(R.id.foodQuantity);
        ImageView done = view.findViewById(R.id.btnDone);
        ImageView delete = view.findViewById(R.id.btnDelete);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateOrder(order.getIdOrder(),PreferencesManager.getInstance().getRestaurantId());
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hiển thị AlertDialog khi nhấn vào ImageView delete
                showDeleteConfirmationDialog(order.getIdOrder(),PreferencesManager.getInstance().getRestaurantId());
            }
        });




        // Set data
        tvNameTable.setText("Bàn " + order.getNameTable());
        tvNameFood.setText(order.getNameFood());
        tvQuantity.setText("Số lượng: " + order.getQuantity());

        Glide.with(context)
                .load(order.getImage())
                .into(foodImage);

        return view;
    }

    private void showDeleteConfirmationDialog(String idOrder, String idRes) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Bạn có muốn hủy đơn này?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Xử lý khi nhấn nút OK trong AlertDialog
                        deleteOrder(idOrder,idRes);
                        Toast.makeText(context, "Đã hủy đơn", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Xử lý khi nhấn nút Cancel trong AlertDialog (nếu cần)
                        dialog.dismiss();
                    }
                })
                .show();
    }


    public void deleteOrder(String idOrder, String idRes) {
        // Tạo URL cho endpoint order-update
        String url = context.getString(R.string.base_url) + "/bills/order-cancel?idOrder=" + idOrder + "&idRes=" + idRes;

        // Tạo request PUT
        StringRequest request = new StringRequest(Request.Method.DELETE, url,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(String response) {
                        // Xử lý phản hồi thành công từ server
                        orderFragment.getData();
                        DialogHelper.showSuccessDialog("Cập nhật thành công!",context);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Xử lý lỗi từ server
                        DialogHelper.showFailDialog("Có lỗi vui lòng thử lại sau!!",context);
                    }
                }){
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                // Add Basic Authentication header
                String credentials = PreferencesManager.getInstance().getUserName() + ":" + PreferencesManager.getInstance().getPass();
                String auth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                params.put("Authorization", auth);

                return params;
            }
        };

        // Thêm request vào RequestQueue của Volley
        RequestQueue queue = VolleySingleton.getInstance().getRequestQueue();
        queue.add(request);
    }


    public void updateOrder(String idOrder, String idRes) {
        // Tạo URL cho endpoint order-update
        String url = context.getString(R.string.base_url) + "/bills/order-update?idOrder=" + idOrder + "&idRes=" + idRes;

        // Tạo request PUT
        StringRequest request = new StringRequest(Request.Method.PUT, url,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(String response) {
                        // Xử lý phản hồi thành công từ server
                        orderFragment.getData();
                        DialogHelper.showSuccessDialog("Cập nhật thành công!",context);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Xử lý lỗi từ server
                        DialogHelper.showFailDialog("Có lỗi vui lòng thử lại sau!!",context);
                    }
                }){
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                // Add Basic Authentication header
                String credentials = PreferencesManager.getInstance().getUserName() + ":" + PreferencesManager.getInstance().getPass();
                String auth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                params.put("Authorization", auth);

                return params;
            }
        };

        // Thêm request vào RequestQueue của Volley
        RequestQueue queue = VolleySingleton.getInstance().getRequestQueue();
        queue.add(request);
    }

}

@RequiresApi(api = Build.VERSION_CODES.O)
public class OrderFragment extends Fragment {

    private String idRestaurant = PreferencesManager.getInstance().getRestaurantId();
    private String username = PreferencesManager.getInstance().getUserName();
    private String password = PreferencesManager.getInstance().getPass();
    private final String serverUrl = "ws://192.168.1.32:1234" + "/socket?id=" + idRestaurant; // URL của server socket
    private FoodOrderAdapter foodOrderAdapter;
    private WebSocket webSocket;
    private OkHttpClient client;
    private Handler handler;

    private okhttp3.Request request;


    public OrderFragment() {
        // Required empty public constructor
    }
    public OrderFragment(String idRestaurant, String username, String password) {
        // Required empty public constructor
        this.idRestaurant = idRestaurant;
        this.username = username;
        this.password = password;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startWebSocketConnection();
        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for t his fragment
        View v = inflater.inflate(R.layout.fragment_order, container, false);
        getData();
        return v;
    }

    private void updateUI(JSONArray response) {
        /**
         * {
         *         "idTable": "98AFF73GPT",
         *         "nameTable": "Mơ ước",
         *         "idBill": "S11LTDD10L",
         *         "foodDetails": [],
         *         "total": 0.0
         *     }
         */
        // Update UI
        List<Order> listOrder = new ArrayList<>();
        try {
            for (int i = 0; i < response.length(); i++) {
                JSONObject table = response.getJSONObject(i);
                // Get food details
                JSONArray foodDetails = table.getJSONArray("foodDetails");
                String nameTable = table.getString("nameTable");
                String idTable = table.getString("idTable");

                for (int j = 0; j < foodDetails.length(); j++) {
                    JSONObject foodDetail = foodDetails.getJSONObject(j);
                    String idFood = foodDetail.getString("idFood");
                    String nameFood = foodDetail.getString("name");
                    String idOrder = foodDetail.getString("idOrder");
                    String price = foodDetail.getString("price");
                    String image = foodDetail.getString("image");
                    String quantity = foodDetail.getString("quantity");
                    listOrder.add(new Order(idTable, nameTable, idOrder, idFood, nameFood, price, image, quantity));
                }

                OrderAdapter orderAdapter = new OrderAdapter(getContext(), listOrder,this);
                ListView lvOrder = getView().findViewById(R.id.listOrder);
                lvOrder.setAdapter(orderAdapter);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void getData() {

        // Get data from API
        RequestQueue queue = VolleySingleton.getInstance().getRequestQueue();

        LoadingDialog loadingDialog = new LoadingDialog((Activity) getContext());
        loadingDialog.startLoadingDialog();
        // Request data from server
        String url = getString(R.string.base_url) + "/bills/get-all-order?idRestaurant=" + idRestaurant;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        loadingDialog.dissmissDialog();
                        // Parse dữ liệu JSON và cập nhật UI
                        updateUI(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingDialog.dissmissDialog();
                DialogHelper.showFailDialog("Có lỗi xảy ra vui lòng thử lại sau",getContext());
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

    private void startWebSocketConnection() {
        client = new OkHttpClient();
        request = new okhttp3.Request.Builder().url(serverUrl).build();
        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, okhttp3.Response response) {
                // WebSocket kết nối thành công
                Log.d("WebSocket", "Connected");
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                // Nhận thông điệp từ server
                onMessageReceived(text);
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                // Đóng kết nối
                Log.d("WebSocket", "Closed");
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, okhttp3.Response response) {
                // Lỗi khi kết nối
                Log.e("WebSocket", "Error: " + t.getMessage());
            }
        });
    }

    public void onMessageReceived(final String message) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                // Xử lý thông điệp ở đây
                if(!Objects.equals(message, "")) {
                    updateUISocket();
                }
            }
        });
    }

    public void closeWebSocket() {
        if (webSocket != null) {
            webSocket.close(1000, "Fragment destroyed");
        }
        if (client != null) {
            client.dispatcher().executorService().shutdown();
        }
    }

    private void updateUISocket () {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Cập nhật giao diện ở đây
                getData();
            }
        });
    }


    @Override
    public void onPause() {
        super.onPause();
        closeWebSocket();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        closeWebSocket();
    }
}