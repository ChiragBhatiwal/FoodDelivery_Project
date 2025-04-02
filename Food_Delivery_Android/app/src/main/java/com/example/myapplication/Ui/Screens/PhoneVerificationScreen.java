package com.example.myapplication.Ui.Screens;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.Utility.Constant;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

public class PhoneVerificationScreen extends AppCompatActivity {
    TextView textView;
    TextInputLayout phoneVerificationLayout,otpVerificationLayout;
    TextInputEditText phoneNumber_PhoneVerification,otpNumber_PhoneVerification;
    Button sendOtp_Button,verifyOtp_Button;
    String mobile = "+91";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_phone_verification_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        phoneNumber_PhoneVerification = findViewById(R.id.PhoneNumber_PhoneVerificationScreen);
        otpNumber_PhoneVerification = findViewById(R.id.otpNumber_PhoneVerificationScreen);
        otpVerificationLayout = findViewById(R.id.otpNumberLayout_PhoneVerificationScreen);
        phoneVerificationLayout = findViewById(R.id.PhoneNumberLayout_PhoneVerificationScreen);
        sendOtp_Button = findViewById(R.id.sendOtpButton_PhoneVerificationScreen);
        verifyOtp_Button = findViewById(R.id.verifyOtpButton_PhoneVerificationScreen);
        textView = findViewById(R.id.textForSendingOtp_PhoneVerification);

        sendOtp_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendOtp();
            }
        });

        verifyOtp_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyOtp();
            }
        });
    }

    public void verifyOtp(){
        RequestQueue requestQueue = Volley.newRequestQueue(PhoneVerificationScreen.this);
        JSONObject jsonObject = new JSONObject();
        try {
            String otp = otpNumber_PhoneVerification.getText().toString();
            jsonObject.put("phoneNumber",mobile);
            Log.d("Mobile",mobile);
            jsonObject.put("code",otp);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constant.base_IP+"/user/login-user", jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String token = response.getString("refreshToken");
                    String _id = response.getString("_id");
                    SharedPreferences sharedPreferences = getSharedPreferences("Login",MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("refreshToken",token);
                    editor.putString("userId",_id);
                    editor.putBoolean("isLogin",true);
                    editor.apply();
                    Intent intent = new Intent(PhoneVerificationScreen.this, LocationForAddressScreen.class);
                    startActivity(intent);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error in Phone Verification is",error.toString());
                Toast.makeText(PhoneVerificationScreen.this, "Request Failed "+error, Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    public void sendOtp(){
        RequestQueue requestQueue = Volley.newRequestQueue(PhoneVerificationScreen.this);
        JSONObject jsonObject = new JSONObject();
        try {

            mobile += phoneNumber_PhoneVerification.getText().toString();
            jsonObject.put("phoneNumber",mobile);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constant.base_IP+"/user/create-user", jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
               if(response != null)
               {
                   phoneNumber_PhoneVerification.setVisibility(View.GONE);
                   phoneVerificationLayout.setVisibility(View.GONE);
                   sendOtp_Button.setVisibility(View.GONE);
                   verifyOtp_Button.setVisibility(View.VISIBLE);
                   otpVerificationLayout.setVisibility(View.VISIBLE);
                   otpNumber_PhoneVerification.setVisibility(View.VISIBLE);
                   textView.setVisibility(View.VISIBLE);
               }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(PhoneVerificationScreen.this, ""+ error, Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }
}