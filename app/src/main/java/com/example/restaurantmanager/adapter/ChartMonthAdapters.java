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
import com.example.restaurantmanager.model.ChartMonth;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;

public class ChartMonthAdapters extends RecyclerView.Adapter<ChartMonthAdapters.viewHolder> {
    ArrayList<ChartMonth> items;
    Context context;

    public ChartMonthAdapters(ArrayList<ChartMonth> items,Context context) {
        this.items = items;
        this.context=context;
    }

    @NonNull
    @Override
    public ChartMonthAdapters.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chart_month,parent,false);
        return new viewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ChartMonthAdapters.viewHolder holder, int position) {
        holder.name.setText(items.get(position).getName());
        LineDataSet dt2 =new LineDataSet(items.get(position).getEntries(),items.get(position).getNameData());
        dt2.setColor(Color.GREEN);
        dt2.setValueTextColor(Color.GREEN);
        LineData lineData = new LineData(dt2);
        holder.chart.setData(lineData);
// Tùy chỉnh màu chữ cho trục Y bên trái
        holder.chart.getAxisLeft().setTextColor(Color.WHITE);
// Tùy chỉnh màu chữ cho trục Y bên phải (nếu sử dụng)
        holder.chart.getAxisRight().setTextColor(Color.WHITE);
        XAxis xAxis = holder.chart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(15f);
        xAxis.setTextColor(Color.WHITE);
        holder.chart.setDragEnabled(true);
        holder.chart.setScaleEnabled(true);
        holder.chart.setPinchZoom(true);
        holder.chart.animateX(2500);
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
