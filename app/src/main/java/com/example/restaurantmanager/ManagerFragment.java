package com.example.restaurantmanager;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.restaurantmanager.Activity.AddEmployee;
import com.example.restaurantmanager.Activity.ChartActivity;
import com.example.restaurantmanager.Activity.ManagerMenu;
import com.example.restaurantmanager.Activity.PaymentInfoActivity;
import com.example.restaurantmanager.Activity.TableManager;

@RequiresApi(api = Build.VERSION_CODES.O)
public class ManagerFragment extends Fragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_manager, container, false);

        // Set up button for table manager
        openTable(view.findViewById(R.id.btnTableManager));
        openTable(view.findViewById(R.id.btnTableManager2));

        // Set up button for food manager
        openFood(view.findViewById(R.id.btnFoodManager));
        openFood(view.findViewById(R.id.btnFoodManager2));

        // Set up button for statistic manager
        openChart(view.findViewById(R.id.btnStatisticManager));
        openChart(view.findViewById(R.id.btnStatisticManager2));

        openPayment(view.findViewById(R.id.payment));
        openPayment(view.findViewById(R.id.payment2));

        openEmployee(view.findViewById(R.id.btnEmployeeManager));
        openEmployee(view.findViewById(R.id.btnEmployeeManager2));

        return view;
    }



    private void openIntent(Class<?> cls) {
        Intent intent = new Intent(getActivity(), cls);
        startActivity(intent);
    }
    public void openTable(View view){
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openIntent(TableManager.class);
            }
        });
    }

    public void openPayment(View view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openIntent(PaymentInfoActivity.class);
            }
        });
    }
    public void openChart(View view){
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openIntent(ChartActivity.class);
            }
        });
    }

    public void openFood(View view){
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openIntent(ManagerMenu.class);
            }
        });
    }

    public void openEmployee(View view){
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openIntent(AddEmployee.class);
            }
        });
    }
}