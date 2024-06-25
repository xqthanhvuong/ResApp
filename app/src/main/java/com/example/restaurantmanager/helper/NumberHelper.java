package com.example.restaurantmanager.helper;

import java.text.DecimalFormat;

public class NumberHelper {

    public static String formatNumber(float number) {
        // Chuyển số float thành kiểu int để loại bỏ phần thập phân
        int integerValue = (int) number;

        // Đảo ngược chuỗi phần nguyên để dễ dàng thêm dấu "."
        StringBuilder reversedInteger = new StringBuilder(String.valueOf(integerValue)).reverse();

        // Thêm dấu "." sau mỗi 3 chữ số
        StringBuilder formattedBuilder = new StringBuilder();
        for (int i = 0; i < reversedInteger.length(); i++) {
            formattedBuilder.append(reversedInteger.charAt(i));
            if ((i + 1) % 3 == 0 && i != reversedInteger.length() - 1) {
                formattedBuilder.append(".");
            }
        }

        // Đảo ngược lại chuỗi để đưa về đúng thứ tự ban đầu
        String formattedNumber = formattedBuilder.reverse().toString();

        return formattedNumber;

    }
}
