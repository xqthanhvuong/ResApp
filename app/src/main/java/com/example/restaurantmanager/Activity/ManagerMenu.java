package com.example.restaurantmanager.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
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
import com.example.restaurantmanager.API.VolleySingleton;
import com.example.restaurantmanager.Dialog.LoadingDialog;
import com.example.restaurantmanager.R;
import com.example.restaurantmanager.common.NavigationHelper;
import com.example.restaurantmanager.helper.AlertHelper;
import com.example.restaurantmanager.helper.DialogHelper;
import com.example.restaurantmanager.manager.PreferencesManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Menu {
    private final String name;
    private final String id;

    public Menu(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

}

class MenuAdapter extends BaseAdapter {
    private final Context context;
    private final List<Menu> menuItems;

    public MenuAdapter(Context context, List<Menu> menuItems) {
        this.context = context;
        this.menuItems = menuItems;
    }

    @Override
    public int getCount() {
        return menuItems.size();
    }

    @Override
    public Object getItem(int position) {
        return menuItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Menu menu = menuItems.get(position);

        // Get item layout
        @SuppressLint("ViewHolder")
        ConstraintLayout item = (ConstraintLayout) LayoutInflater.from(context).inflate(R.layout.item_menu, null);
        TextView menuName = item.findViewById(R.id.tvMenuName);
        menuName.setText(menu.getName());

        return item;
    }
}

@RequiresApi(api = Build.VERSION_CODES.O)
public class ManagerMenu extends AppCompatActivity {

    private String idRestaurant;

    private String username;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_menu);

        PreferencesManager preferencesManager = PreferencesManager.getInstance();
        username = preferencesManager.getUserName();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            password = preferencesManager.getPass();
        }
        idRestaurant = PreferencesManager.getInstance().getRestaurantId();

        // Set up button back
        NavigationHelper.onBackClicked(findViewById(R.id.btnBackLo));

        // Set up button add menu
        setupButtonAddMenu(findViewById(R.id.btnAddMenu));

        setupViewMenu();

        setupRefreshLayout();
    }

    private void setupRefreshLayout() {
        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setupViewMenu();
                pullToRefresh.setRefreshing(false);
            }
        });
    }

    private void setupViewMenu() {
        RequestQueue queue = VolleySingleton.getInstance().getRequestQueue();
        String url = getString(R.string.base_url) + "/menu/get?idRestaurant=" + idRestaurant;

        LoadingDialog loadingDialog = new LoadingDialog(ManagerMenu.this);
        loadingDialog.startLoadingDialog();

        // Request data from server
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        loadingDialog.dissmissDialog();
                        // Handle response
                        if(response.length()>0){
                            try {
                                JSONObject jsonObject = response.getJSONObject(0);
                                PreferencesManager.getInstance().saveIdMenu(jsonObject.getString("idMenu"));
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        updateUI(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingDialog.dissmissDialog();
                DialogHelper.showFailDialog("Có lỗi xảy ra vui lòng thử lại sau!",ManagerMenu.this);
                // Handle error
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

    private void updateUI(JSONArray response) {
        // Parse JSON data and update UI
        List<Menu> menus = new ArrayList<>();
        try {
            ListView listMenus = findViewById(R.id.listMenus);
            if(response.length()>0){
                findViewById(R.id.btnAddMenu).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertHelper.showAlert(ManagerMenu.this,"Chức năng này cần trả phí","Vui lòng bỏ ra 20$ để kích hoạt chức năng này!!!");
                    }
                });
            }
            for (int i = 0; i < response.length(); i++) {
                JSONObject table = response.getJSONObject(i);
                // Create menu item
                Menu menu = new Menu(table.getString("name"), table.getString("idMenu"));
                // Add menu item to list
                menus.add(menu);

            }

            // Create adapter
            MenuAdapter adapter = new MenuAdapter(this, menus);
            listMenus.setAdapter(adapter);

            listMenus.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // Xử lý sự kiện click
                    Menu item = (Menu) parent.getItemAtPosition(position);
                    Intent intent = new Intent(ManagerMenu.this, FoodManager.class);
                    intent.putExtra("idMenu", item.getId());
                    startActivity(intent);
                }
            });

            listMenus.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    // Xử lý sự kiện long click
                    Menu item = (Menu) parent.getItemAtPosition(position);
                    // Show choose dialog
                    String[] actions = { "Sửa tên", "Xóa" };
                    new AlertDialog.Builder(ManagerMenu.this)
                            .setTitle("Chọn hành động")
                            .setItems(actions, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case 0: // Edit
                                            Intent intent = new Intent(ManagerMenu.this, AddMenu.class);
                                            intent.putExtra("idMenu", item.getId());
                                            intent.putExtra("idRestaurant", idRestaurant);
                                            intent.putExtra("menuName", item.getName());
                                            startActivity(intent);
                                            break;
                                        case 1: // Delete
                                            RequestQueue queue = Volley.newRequestQueue(ManagerMenu.this);
                                            String url = getString(R.string.base_url) + "/menu/delete?idMenu=" + item.getId();

                                            // Request data from server
                                            // Create headers
                                            Map<String, String> headers = new HashMap<>();
                                            String credentials = username + ":" + password;
                                            headers.put("Authorization",
                                                    "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP));

                                            LoadingDialog loadingDialog = new LoadingDialog(ManagerMenu.this);
                                            loadingDialog.startLoadingDialog();

                                            // Create request
                                            StringRequest request = new StringRequest(Request.Method.GET, url,
                                                    new Response.Listener<String>() {
                                                        @Override
                                                        public void onResponse(String response) {
                                                            loadingDialog.dissmissDialog();
                                                            // Request successful
                                                            Toast.makeText(ManagerMenu.this, "Xóa thành công", Toast.LENGTH_SHORT).show();
                                                            setupViewMenu();
                                                        }
                                                    }, new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    loadingDialog.dissmissDialog();
                                                    DialogHelper.showFailDialog("Có lỗi xảy ra vui lòng thử lại sau!", ManagerMenu.this);
                                                    // Xử lý lỗi
                                                    Log.e("Error", "Errol");
                                                    Toast
                                                            .makeText(ManagerMenu.this, "Lỗi xóa menu: " + error.getMessage(), Toast.LENGTH_SHORT)
                                                            .show();
                                                }
                                            }) {
                                                @Override
                                                public String getBodyContentType() {
                                                    return "application/json; charset=utf-8";
                                                }

                                                @Override
                                                public Map<String, String> getHeaders() throws AuthFailureError {
                                                    return headers;
                                                }
                                            };
                                            queue.add(request);
                                            break;
                                    }
                                }
                            })
                            .show();
                    return true;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupButtonAddMenu(View v) {
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManagerMenu.this, AddMenu.class);
                intent.putExtra("idRestaurant", idRestaurant);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}