package com.example.restaurantmanager.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.restaurantmanager.model.Chart;
import com.example.restaurantmanager.formatter.DayAxisValueFormatter;
import com.example.restaurantmanager.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

public class ChartAdapters extends RecyclerView.Adapter<ChartAdapters.viewHolder> {
    ArrayList<Chart> items;
    Context context;

    public ChartAdapters(ArrayList<Chart> items,Context context) {
        this.items = items;
        this.context=context;
    }

    @NonNull
    @Override
    public ChartAdapters.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chart,parent,false);
        return new viewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ChartAdapters.viewHolder holder, int position) {
        holder.name.setText(items.get(position).getName());
        LineDataSet dt1 =new LineDataSet(items.get(position).getEntries1(),items.get(position).getName1());
        dt1.setColor(Color.BLUE);
        dt1.setValueTextColor(Color.BLUE);
        LineDataSet dt2 =new LineDataSet(items.get(position).getEntries2(),items.get(position).getName2());
        dt2.setColor(Color.GREEN);
        dt2.setValueTextColor(Color.GREEN);
        LineData lineData = new LineData();
        lineData.addDataSet(dt1);
        lineData.addDataSet(dt2);
        holder.chart.setData(lineData);
        XAxis xAxis = holder.chart.getXAxis();
        xAxis.setValueFormatter(new DayAxisValueFormatter());
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // Đặt nhãn ở dưới cùng của biểu đồ
        xAxis.setGranularity(1f); // Giới hạn tần suất của nhãn, chỉ hiện mỗi khoảng cách 1 đơn vị
        xAxis.setGranularityEnabled(true);
        holder.chart.getXAxis().setTextColor(Color.WHITE);
        xAxis.setTextSize(15f);
// Tùy chỉnh màu chữ cho trục Y bên trái
        holder.chart.getAxisLeft().setTextColor(Color.WHITE);

// Tùy chỉnh màu chữ cho trục Y bên phải (nếu sử dụng)
        holder.chart.getAxisRight().setTextColor(Color.WHITE);
        Legend legend = holder.chart.getLegend();
        legend.setTextSize(15f);
        legend.setTextColor(Color.WHITE);
        legend.setEnabled(true);
        holder.chart.invalidate();

    }

    @Override
    public int getItemCount() {

        return items.size();
    }
    public class viewHolder extends RecyclerView.ViewHolder{
        TextView name;
        LineChart chart;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            chart = itemView.findViewById(R.id.lineChart);
        }
    }
}
