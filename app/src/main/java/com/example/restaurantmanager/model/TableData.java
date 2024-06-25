package com.example.restaurantmanager.model;

import java.util.List;

public class TableData {
    private List<ChartData> chartListLastWeek;
    private List<ChartData> chartListThisWeek;
    private String tableName;

    public List<ChartData> getChartListLastWeek() {
        return chartListLastWeek;
    }

    public void setChartListLastWeek(List<ChartData> chartListLastWeek) {
        this.chartListLastWeek = chartListLastWeek;
    }

    public List<ChartData> getChartListThisWeek() {
        return chartListThisWeek;
    }

    public void setChartListThisWeek(List<ChartData> chartListThisWeek) {
        this.chartListThisWeek = chartListThisWeek;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
