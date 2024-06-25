package com.example.restaurantmanager;

import android.content.Context;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.restaurantmanager.helper.DatabaseHelper;
import com.example.restaurantmanager.manager.PreferencesManager;
import com.example.restaurantmanager.model.Noti;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;



class NotiAdaper extends BaseAdapter {
    private Context context;
    private List<Noti> notis;

    public NotiAdaper(Context context, List<Noti> menuItems) {
        this.context = context;
        this.notis = menuItems;
    }

    @Override
    public int getCount() {
        return notis.size();
    }

    @Override
    public Object getItem(int position) {
        return notis.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Noti menu = notis.get(position);

        //Get item layout
        LinearLayout item = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.item_noti, null);
        TextView message = item.findViewById(R.id.message);
        message.setText(menu.getMessage());

        return item;
    }
    public void add(Noti noti) {
        notis.add(noti);
        notifyDataSetChanged();
    }

    public void addAll(List<Noti> notis) {
        this.notis.addAll(notis);
        notifyDataSetChanged();
    }
}


public class NotiFragment extends Fragment {

    private final String serverUrl = "ws://10.78.0.251:1234/socket?id="+ PreferencesManager.getInstance().getRestaurantId(); // URL của server
    private NotiAdaper notiAdaper;
    private WebSocket webSocket;
    private OkHttpClient client;
    private Handler handler;
    private DatabaseHelper databaseHelper;
    private ListView notiListView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handler = new Handler(Looper.getMainLooper());

        // Khởi tạo adapter cho thông báo
        notiAdaper = new NotiAdaper(getContext(), new ArrayList<>());

        databaseHelper = new DatabaseHelper(getContext());

        // Bắt đầu kết nối socket trên luồng nền
        startWebSocketConnection();
    }

    private void startWebSocketConnection() {
        client = new OkHttpClient();
        Request request = new Request.Builder().url(serverUrl).build();
        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                // WebSocket kết nối thành công
                Log.d("WebSocket", "Connected");
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                // Nhận thông điệp từ server
                onMessageReceived(text);
                Log.d("WebSocket", "Message: " + text);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                t.printStackTrace();
            }
        });
    }

    public void onMessageReceived(final String message) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                // Xử lý thông điệp ở đây
                /*
                 * Xử lý làm mới giao diện đơn hàng
                 * if(message == "new_order") {
                 *   updateOrderUI();
                 * }
                 * */
                // Thêm message vào adapter
                notiAdaper.add(new Noti(message));
                // Lưu message vào cơ sở dữ liệu
                databaseHelper.addNotification(message);
                updateUI();
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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_noti, container, false);
        // Thiết lập ListView và Adapter
        notiListView = v.findViewById(R.id.listViewNoti);
        notiListView.setAdapter(notiAdaper);

        // Load thông báo từ cơ sở dữ liệu
        loadNotificationsFromDatabase();
        return v;
    }


    private void updateUI () {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Cập nhật giao diện ở đây
                ListView listView = getView().findViewById(R.id.listViewNoti);
                listView.setAdapter(notiAdaper);
            }
        });
    }

    private void loadNotificationsFromDatabase() {
        List<Noti> notis = databaseHelper.getAllNotifications();
        notiAdaper.addAll(notis);
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