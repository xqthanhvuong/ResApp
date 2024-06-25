package com.example.restaurantmanager.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.restaurantmanager.API.VolleySingleton;
import com.example.restaurantmanager.Dialog.LoadingDialog;
import com.example.restaurantmanager.Dialog.OTPDialog;
import com.example.restaurantmanager.MainActivity;
import com.example.restaurantmanager.R;
import com.example.restaurantmanager.helper.AlertHelper;
import com.example.restaurantmanager.helper.CheckHelper;
import com.example.restaurantmanager.helper.DialogHelper;
import com.example.restaurantmanager.helper.KeystoreHelper;
import com.example.restaurantmanager.helper.OTPHelper;
import com.example.restaurantmanager.helper.PhoneNumberHelper;
import com.example.restaurantmanager.manager.PreferencesManager;
import com.example.restaurantmanager.model.UserAccount;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

public class SignUp extends AppCompatActivity {


    Button btnSignUp;

    String TAG = "SIGN UP";
    EditText name,email,pass,rePass,phone;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        //binder view to var
        name=findViewById(R.id.signupname);
        email=findViewById(R.id.signupmail);
        pass=findViewById(R.id.signuppass);
        rePass=findViewById(R.id.signuprepass);
        phone=findViewById(R.id.signupphone);
        btnSignUp=findViewById(R.id.buttonsignup);

        //set event click
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });


    }

    private void signUp() {

//        if(!checkData()){
//            return;
//        }

        RequestQueue requestQueue = VolleySingleton.getInstance().getRequestQueue();
        LoadingDialog loadingDialog = new LoadingDialog(SignUp.this);
        loadingDialog.startLoadingDialog();
        //url
        String url = getString(R.string.base_url);
        UserAccount userAccount = new UserAccount
                (email.getText().toString(), pass.getText().toString(),
                        name.getText().toString(), PhoneNumberHelper.convertToInternationalPhone(phone.getText().toString()));
        JSONObject postData = userAccount.toJson();
        // Create a JsonObjectRequest
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url+"/account/register",
                postData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        PreferencesManager preferencesManager = PreferencesManager.getInstance();
                        preferencesManager.savePhone(PhoneNumberHelper.convertToInternationalPhone(userAccount.getPhone()));
                        OTPHelper.sendOTPToPhone(SignUp.this, preferencesManager.getPhone(), new OTPHelper.OTPResultListener() {
                            @Override
                            public void onResult(boolean success) {
                                if(success){
                                    loadingDialog.dissmissDialog();
                                    OTPDialog otpDialog = new OTPDialog(SignUp.this, preferencesManager.getPhone(), new OTPDialog.OTPDialogListener() {
                                        @Override
                                        public void onSuccess() {
                                            Toast.makeText(SignUp.this, "Your account has been activated", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                        @Override
                                        public void onFailure() {
                                            AlertHelper.showAlert(SignUp.this, "Error", "Your OTP is wrong");
                                        }
                                    });
                                    otpDialog.setCancelable(true);
                                    otpDialog.show();
                                }
                            }
                        });

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loadingDialog.dissmissDialog();
                        // Xử lý lỗi
                        String errorMessage = "Lỗi không xác định";

                        // Kiểm tra nếu response có lỗi JSON
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            String errorString = new String(error.networkResponse.data);
                            try {
                                JSONObject errorJson = new JSONObject(errorString);
                                errorMessage = errorJson.getString("error");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        // Hiển thị lỗi lên màn hình
                        DialogHelper.showFailDialog(errorMessage,SignUp.this);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }


        };

        // Add the request to the RequestQueue
        requestQueue.add(jsonObjectRequest);

    }

    private boolean checkData() {
        if(CheckHelper.isNullOrEmpty(name.getText().toString())){
            name.requestFocus();
            AlertHelper.showAlert(SignUp.this,"Required Field","This field name cannot be left blank");
            return false;
        }
        if(!CheckHelper.isValidEmail(email.getText().toString())){
            email.requestFocus();
            AlertHelper.showAlert(SignUp.this,"Field Invalid","Email is invalid");
            return false;
        }
        if(!CheckHelper.isValidPhoneNumber(phone.getText().toString())){
            email.requestFocus();
            AlertHelper.showAlert(SignUp.this,"Phone number is not valid","Please enter the correct phone number    ");
            return false;
        }
        if(!CheckHelper.isValidPassword(pass.getText().toString())){
            pass.requestFocus();
            AlertHelper.showAlert(SignUp.this,"The password is not safe","Password must have at least 8 characters including 1 special character, 1 number, 1 uppercase letter and 1 lowercase letter");
            return false;
        }
        if(!pass.getText().toString().equals(rePass.getText().toString())){
            rePass.requestFocus();
            AlertHelper.showAlert(SignUp.this,"unmatch","Re_Password don't match to Password");
            return false;
        }

        return true;
    }
}