package com.example.restaurantmanager.formatter;

import com.github.mikephil.charting.formatter.ValueFormatter;

public class DayAxisValueFormatter extends ValueFormatter {
    private String[] days = new String[]{"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};

    @Override
    public String getFormattedValue(float value) {
        int index = (int) value;
        if (index < 0 || index >= days.length) {
            return "";  // Trả về chuỗi rỗng nếu chỉ số ngoài phạm vi
        }
        return days[index];
    }
}

