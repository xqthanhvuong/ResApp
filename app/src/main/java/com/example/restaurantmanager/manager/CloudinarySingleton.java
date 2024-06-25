package com.example.restaurantmanager.manager;

import com.cloudinary.Cloudinary;
import com.cloudinary.android.MediaManager;
import com.example.restaurantmanager.MyApplication;

import java.util.HashMap;
import java.util.Map;

public class CloudinarySingleton {
    private static Cloudinary instance;
    private static Cloudinary CreateCloudinary(){
        Map config = new HashMap();
        config.put("cloud_name", "dcweof28t");
        config.put("api_key", "457243198976178");
        config.put("api_secret", "SiFIPqb_P4a8XdcrO0e4ItkWKnI");
        MediaManager.init(MyApplication.getAppContext(), config);
        return new Cloudinary(config);
    }

    public static Cloudinary getInstance(){
        if(instance == null){
            instance = CreateCloudinary();
            return instance;
        }else {
            return instance;
        }
    }

}
