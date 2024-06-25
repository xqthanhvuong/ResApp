package com.example.restaurantmanager.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.restaurantmanager.MyApplication;
import com.example.restaurantmanager.helper.CheckHelper;
import com.example.restaurantmanager.helper.EncryptionHelper;
import com.example.restaurantmanager.helper.KeystoreHelper;

import javax.crypto.SecretKey;

public class PreferencesManager {
    private static final String PREF_NAME = "UserSettingsRestaurantAppEE";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    private static PreferencesManager instance;

    // Constructor
    private PreferencesManager() {
        this.context = MyApplication.getAppContext();
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static synchronized PreferencesManager getInstance() {
        if (instance == null) {
            instance = new PreferencesManager();
        }
        return instance;
    }


    public void saveIdMenu(String idMenu) {
        editor.putString("idmenu", idMenu);
        editor.apply();
    }

    public String getIdMenu() {
        return sharedPreferences.getString("idmenu", null); // Trả về "DefaultUser" nếu không tìm thấy key
    }
    // Lưu trữ tên người dùng
    public void saveUserName(String userName) {
        editor.putString("username", userName);
        editor.apply();
    }

    // Lấy tên người dùng
    public String getUserName() {
        return sharedPreferences.getString("username", null); // Trả về "DefaultUser" nếu không tìm thấy key
    }

    public void saveAvt(String url){
        if(url != null){
            if(url.equals("null") || url.isEmpty()){
                editor.putString("avt",null);
                editor.apply();
                return;
            }
        }
        editor.putString("avt",url);
        editor.apply();
    }

    public String getAvt(){
        return sharedPreferences.getString("avt",null);
    }

    public String getBirthday(){
        return sharedPreferences.getString("birthday","19/05/1999");
    }

    public void saveBirtday(String date){
        if(date!=null){
            if(date.equals("null")){
                editor.putString("birthday",null);
                editor.apply();
                return;
            }
        }
        editor.putString("birthday",date);
        editor.apply();
    }

    public void setIsFirstTime(boolean firstTime) {
        editor.putBoolean("firstTime", firstTime);
        editor.apply();
    }

    public boolean getIsFirstTime() {
        return sharedPreferences.getBoolean("firstTime", true);
    }

    public void savePhone(String phone) {
        editor.putString("phone", phone);
        editor.apply();
    }

    public String getPhone() {
        return sharedPreferences.getString("phone", null);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void savePass(String pass) {
        if(pass==null){
            editor.putString("password", null);
            editor.apply();
            return;
        }
        try {
            SecretKey key = KeystoreHelper.createKeyIfNotExists();
            String hashPass = EncryptionHelper.encrypt(pass, key);
            editor.putString("password", hashPass);
            editor.apply();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getPass() {
        try {
            SecretKey key = KeystoreHelper.createKeyIfNotExists();
            String hashPass = sharedPreferences.getString("password", null);
            if (CheckHelper.isNullOrEmpty(hashPass)) {
                return null;
            }
            return EncryptionHelper.decrypt(hashPass, key);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void saveName(String name) {
        editor.putString("name", name);
        editor.apply();
    }

    public String getName(){
        return sharedPreferences.getString("name",null);
    }
    public void saveRole(String role) {
        editor.putString("role", role);
        editor.apply();
    }

    // Lấy tên người dùng
    public String getRole() {
        return sharedPreferences.getString("role", null); // Trả về "DefaultUser" nếu không tìm thấy key
    }
    public void saveId(String id) {
        editor.putString("id", id);
        editor.apply();
    }

    // Lấy tên người dùng
    public String getId() {
        return sharedPreferences.getString("id", null); // Trả về "DefaultUser" nếu không tìm thấy key
    }


    public void saveRestaurantId(String id) {
        editor.putString("idrestaurant", id);
        editor.apply();
    }

    public String getRestaurantId() {
        return sharedPreferences.getString("idrestaurant", null); // Trả về "DefaultUser" nếu không tìm thấy key
    }

}
