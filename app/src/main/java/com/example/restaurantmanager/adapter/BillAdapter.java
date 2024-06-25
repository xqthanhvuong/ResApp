package com.example.restaurantmanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.restaurantmanager.R;
import com.example.restaurantmanager.model.FoodItemBill;

import java.util.List;

public class BillAdapter extends ArrayAdapter<FoodItemBill> {
    private final Context context;
    private final List<FoodItemBill> items;

    public BillAdapter(Context context, List<FoodItemBill> items) {
        super(context, 0, items);
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_food_bill, parent, false);
        }

        FoodItemBill item = items.get(position);

        TextView textViewName = convertView.findViewById(R.id.textViewName);
        TextView textViewQuantity = convertView.findViewById(R.id.textViewQuantity);
        TextView textViewPrice = convertView.findViewById(R.id.textViewPrice);

        textViewName.setText(item.getName());
        textViewQuantity.setText(String.valueOf(item.getQuantity()));
        textViewPrice.setText(String.format("%,d VNĐ", (int) item.getPrice()));

        return convertView;
    }
}
