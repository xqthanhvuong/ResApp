package com.example.restaurantmanager.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.restaurantmanager.Activity.SignUp;
import com.example.restaurantmanager.R;
import com.example.restaurantmanager.helper.OTPHelper;


public class OTPDialog extends Dialog {
    private EditText otp1, otp2, otp3, otp4, otp5;
    private TextView resendBtn;
    private Button verifyBtn;

    private int resendTimer = 60;
    private boolean resendEnable = false;

    private final String mobileNumberTxt;
    private int selectedPosition = 0;

    private OTPDialogListener listener;
    public OTPDialog(@NonNull Context context, String mobileNumber, OTPDialogListener listener) {
        super(context);
        this.mobileNumberTxt = mobileNumber;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(getContext().getResources().getColor(android.R.color.transparent)));
        setContentView(R.layout.otp_dialog);

        otp1=findViewById(R.id.otp1);
        otp2=findViewById(R.id.otp2);
        otp3=findViewById(R.id.otp3);
        otp4=findViewById(R.id.otp4);
        otp5=findViewById(R.id.otp5);

        resendBtn=findViewById(R.id.resendBtn);
        verifyBtn=findViewById(R.id.verifyBtn);
        final TextView mobileNumber = findViewById(R.id.mobileNumber);

        otp1.addTextChangedListener(textWatcher);
        otp2.addTextChangedListener(textWatcher);
        otp3.addTextChangedListener(textWatcher);
        otp4.addTextChangedListener(textWatcher);
        otp5.addTextChangedListener(textWatcher);

        showKeyboard(otp1);

        startCountDownTimer();

        mobileNumber.setText(mobileNumberTxt);

        resendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(resendEnable){
                    //resend code in here
                    OTPHelper.sendOTPToPhone(OTPDialog.super.getContext(),mobileNumberTxt,null);
                    startCountDownTimer();
                }
            }
        });

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String otp = otp1.getText().toString()+
                        otp2.getText().toString()+
                        otp3.getText().toString()+
                        otp4.getText().toString()+
                        otp5.getText().toString();
                if(otp.length()==5){
                    //send otp code to sever
                    if(isDialogFromSignUpActivity()){
                        OTPHelper.activePhone(OTPDialog.super.getContext(), mobileNumberTxt, otp, new OTPHelper.OTPResultListener() {
                            @Override
                            public void onResult(boolean success) {
                                if(success){
                                    listener.onSuccess();
                                    dismiss();
                                }else {
                                    listener.onFailure();
                                }
                            }
                        });
                    }else{

                    }
                }
            }
        });

    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(s.length() > 0){
                if(selectedPosition==0){
                    selectedPosition=1;
                    showKeyboard(otp2);
                } else if (selectedPosition==1) {
                    selectedPosition=2;
                    showKeyboard(otp3);
                } else if (selectedPosition==2) {
                    selectedPosition=3;
                    showKeyboard(otp4);
                } else if (selectedPosition==3) {
                    selectedPosition=4;
                    showKeyboard(otp5);
                }else {
                    verifyBtn.setBackgroundColor(R.drawable.round_back_red_100);
                }
            }
        }
    };
    private void showKeyboard(EditText otpET){
        otpET.requestFocus();

        InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(otpET, InputMethodManager.SHOW_IMPLICIT);
    }

    private void startCountDownTimer(){
        resendEnable=false;
        resendBtn.setTextColor(Color.parseColor("#99000000"));
        new CountDownTimer(resendTimer*1000,1000){

            @Override
            public void onTick(long millisUntilFinished) {
                resendBtn.setText("Resend code ("+(millisUntilFinished / 1000)+")");
            }

            @Override
            public void onFinish() {
                resendEnable=true;
                resendBtn.setText("Resend code");
                resendBtn.setTextColor(getContext().getResources().getColor(android.R.color.holo_blue_dark));
            }
        }.start();
    }

    @Override
    public boolean onKeyUp(int keyCode, @NonNull KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_DEL){
            if(selectedPosition==4){
                selectedPosition=3;
                showKeyboard(otp4);
            } else if (selectedPosition==3) {
                selectedPosition=2;
                showKeyboard(otp3);
            } else if (selectedPosition==2) {
                selectedPosition=1;
                showKeyboard(otp2);
            } else if (selectedPosition==1) {
                selectedPosition=0;
                showKeyboard(otp1);
            }
            verifyBtn.setBackgroundResource(R.drawable.round_back_brown);
            return true;
        }else{
            return super.onKeyUp(keyCode, event);
        }
    }
    public boolean isDialogFromSignUpActivity() {
        Context context = getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof SignUp) {
                return true;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return false;
    }

    public interface OTPDialogListener{
        void onSuccess();
        void onFailure();
    }
}
