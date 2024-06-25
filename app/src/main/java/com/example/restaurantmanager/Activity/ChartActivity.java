package com.example.restaurantmanager.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.restaurantmanager.R;
import com.example.restaurantmanager.adapter.ChartAdapters;
import com.example.restaurantmanager.adapter.ChartMonthAdapters;
import com.example.restaurantmanager.common.NavigationHelper;
import com.example.restaurantmanager.helper.DateTimeHelper;
import com.example.restaurantmanager.manager.PreferencesManager;
import com.example.restaurantmanager.model.Chart;
import com.example.restaurantmanager.model.ChartData;
import com.example.restaurantmanager.model.ChartMonth;
import com.example.restaurantmanager.model.TableData;
import com.github.mikephil.charting.data.Entry;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiresApi(api = Build.VERSION_CODES.O)
public class ChartActivity extends AppCompatActivity {

    private String idRestaurant;

    private RecyclerView.Adapter adapterChartMonth;

    private RecyclerView.Adapter adapterChartTable;

    private RecyclerView recyclerViewTable;

    private RecyclerView recyclerViewMonth;
    private int month;
    private int year;

    private String TAG = "CHART";

    private int day;

    private String username;
    private String password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        idRestaurant = PreferencesManager.getInstance().getRestaurantId();
        NavigationHelper.onBackClicked(findViewById(R.id.btnBack));
        setupViewChartMonth();
        makeRequestTable();


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void updateUIChartMonth(JSONArray rp){
        List<Entry> entries = new ArrayList<>();
        ArrayList<ChartMonth> items = new ArrayList<>();
        try{
            recyclerViewMonth = findViewById(R.id.view3);
            for (int i = 1; i < day; i++) {
                entries.add(new Entry(i,0));
            }
            for(int i=0;i<rp.length();i++){
                JSONObject js = rp.getJSONObject(i);
                int dayIndex = js.getInt("day")-1;
                if(dayIndex<entries.size()){
                    entries.set(dayIndex,new Entry(dayIndex+1,(float) js.getDouble("totalPrice")));
                }
            }
            items.add(new ChartMonth(entries,month+"/"+year,"Thống kê tháng"));
            recyclerViewMonth.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
            adapterChartMonth = new ChartMonthAdapters(items,this);
            recyclerViewMonth.setAdapter(adapterChartMonth);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void setupViewChartMonth() {
        ArrayList<ChartMonth> items =new ArrayList<>();
        RequestQueue queue = Volley.newRequestQueue(ChartActivity.this);
        month= DateTimeHelper.getCurrentMonth();
        year = DateTimeHelper.getCurrentYear();
        day = DateTimeHelper.getDaysOfMonth(month,year);
        username = PreferencesManager.getInstance().getUserName();
        password = PreferencesManager.getInstance().getPass();

        String url = getString(R.string.base_url) + "/chart/getMonthlyChart?idRestaurant=" + idRestaurant+ "&month=" + month+"&year="+year;

        // Request data from server
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Handle response
                        updateUIChartMonth(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle error
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
        queue.add(jsonArrayRequest);
    }


    private void makeRequestTable(){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String URL = getString(R.string.base_url)+"/chart/getWeekChartTable?idRestaurant="+idRestaurant;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        recyclerViewTable = findViewById(R.id.view1);
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<TableData>>() {}.getType();
                        List<TableData> tableDataList = gson.fromJson(response.toString(), listType);
                        ArrayList<Chart> charts = new ArrayList<>();
                        for (TableData tableData : tableDataList) {
                            List<Entry> entries1 = new ArrayList<>();
                            List<Entry> entries2 = new ArrayList<>();
                            for (int i = 0; i < 7; i++) {
                                entries1.add(new Entry(i,0));
                                entries2.add(new Entry(i,0));
                            }
                            for(ChartData day : tableData.getChartListLastWeek()){
                                entries1.set(day.getDayOfWeek()-1,new Entry(day.getDayOfWeek()-1,(float) day.getTotalSales()));
                            }
                            for(ChartData day : tableData.getChartListThisWeek()){
                                entries2.set(day.getDayOfWeek()-1,new Entry(day.getDayOfWeek()-1,(float) day.getTotalSales()));
                            }
                            Chart chart = new Chart(tableData.getTableName(),entries1,entries2,"Tuần trước","Tuần này");
                            charts.add(chart);
                        }
                        recyclerViewTable.setLayoutManager(new LinearLayoutManager(ChartActivity.this,LinearLayoutManager.HORIZONTAL,false));

                        adapterChartTable = new ChartAdapters(charts,ChartActivity.this);
                        recyclerViewTable.setAdapter(adapterChartTable);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error: " + error.getMessage());
                    }
                }
        ){
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

        requestQueue.add(jsonArrayRequest);
    }
}