package com.example.restaurantmanager.helper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckHelper {
    public static boolean isNullOrEmpty(String s) {
        if (s == null) {
            return true;
        }
        s = s.trim();
        return s.isEmpty();
    }
    public static boolean isValidEmail(String email) {
        if (isNullOrEmpty(email)) {
            return false;
        }

        String emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean isValidPassword(String password) {
        if (isNullOrEmpty(password)) {
            return false;
        }
        // Biểu thức chính quy kiểm tra mật khẩu
        String passwordPattern =
                "^(?=.*[0-9])" +        // ít nhất một chữ số
                        "(?=.*[a-z])" +          // ít nhất một chữ thường
                        "(?=.*[A-Z])" +          // ít nhất một chữ hoa
                        "(?=.*[@#$%^&+=!])" +    // ít nhất một ký tự đặc biệt
                        "(?=\\S+$).{8,}$";       // ít nhất 8 ký tự và không chứa khoảng trắng

        Pattern pattern = Pattern.compile(passwordPattern);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }
    public static boolean isValidPhoneNumber(String phoneNumber) {
        if (isNullOrEmpty(phoneNumber)) {
            return false;
        }

        // Biểu thức chính quy kiểm tra số điện thoại
        String phoneNumberPattern = "^[+]?[0-9]{10,15}$";
        Pattern pattern = Pattern.compile(phoneNumberPattern);
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }
}

