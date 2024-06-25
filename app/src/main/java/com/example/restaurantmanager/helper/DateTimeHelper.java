package com.example.restaurantmanager.helper;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@RequiresApi(api = Build.VERSION_CODES.O)
public class DateTimeHelper {
    // Múi giờ cho Việt Nam
    private static final ZoneId VIETNAM_ZONE_ID = ZoneId.of("Asia/Ho_Chi_Minh");


    // Phương thức để lấy ngày hiện tại
    public static int getCurrentDay() {
        ZonedDateTime nowInVietnam = ZonedDateTime.now(VIETNAM_ZONE_ID);
        return nowInVietnam.getDayOfMonth();
    }

    // Phương thức để lấy tháng hiện tại
    public static int getCurrentMonth() {
        ZonedDateTime nowInVietnam = ZonedDateTime.now(VIETNAM_ZONE_ID);
        return nowInVietnam.getMonthValue();
    }

    // Phương thức để lấy năm hiện tại
    public static int getCurrentYear() {
        ZonedDateTime nowInVietnam = ZonedDateTime.now(VIETNAM_ZONE_ID);
        return nowInVietnam.getYear();
    }

    // Phương thức để lấy số ngày trong tháng
    public static int getDaysOfMonth(int month, int year) {
        YearMonth yearMonth = YearMonth.of(year, month);
        return yearMonth.lengthOfMonth();
    }


}
