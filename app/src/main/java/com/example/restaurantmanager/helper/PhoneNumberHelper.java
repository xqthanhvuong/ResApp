package com.example.restaurantmanager.helper;

public class PhoneNumberHelper {
    public static String convertToInternationalPhone(String phoneNumber){
        if (phoneNumber.startsWith("+")) {
            return phoneNumber;
        }
        if(phoneNumber.startsWith("0")){
            return "+84" + phoneNumber.substring(1);
        }else {
            return "+84" + phoneNumber;
        }
    }
}
