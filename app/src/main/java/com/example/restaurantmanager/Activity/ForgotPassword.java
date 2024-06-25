package com.example.restaurantmanager.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.restaurantmanager.R;
import com.example.restaurantmanager.helper.OTPHelper;

public class ForgotPassword extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpass);
        Button buttonsend = findViewById(R.id.buttonsend);
        buttonsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText mail = findViewById(R.id.edittextmail);
                OTPHelper.sendOTPToPhoneByMail(ForgotPassword.this, mail.getText().toString(), new OTPHelper.OTPResultListener() {
                    @Override
                    public void onResult(boolean success) {
                        if(success){

                        }
                    }
                });

            }
        });

    }
}