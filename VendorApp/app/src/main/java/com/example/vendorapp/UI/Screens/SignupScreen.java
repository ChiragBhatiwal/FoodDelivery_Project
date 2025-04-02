package com.example.vendorapp.UI.Screens;

import android.content.Intent;
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
import com.example.vendorapp.Utils.Constants;
import com.example.vendorapp.R;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

public class SignupScreen extends AppCompatActivity {

    TextInputEditText username,email,password;
    Button singupButton;
    TextView loginScreenText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        username = findViewById(R.id.username_Field_SignupScreen);
        email = findViewById(R.id.email_Field_SignupScreen);
        password = findViewById(R.id.password_Field_SignupScreen);
        singupButton = findViewById(R.id.signupButton_SignupScreen);
        loginScreenText =  findViewById(R.id.loginText_SignupScreen);

        loginScreenText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupScreen.this, LoginScreen.class);
                startActivity(intent);
            }
        });

        singupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestQueue requestQueue = Volley.newRequestQueue(SignupScreen.this);

                JSONObject jsonObject = new JSONObject();

                try {
                    jsonObject.put("email",email.getText().toString());
                    jsonObject.put("username",username.getText().toString());
                    jsonObject.put("password",password.getText().toString());
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constants.Url+"/vendor/create-vendor", jsonObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(SignupScreen.this, "Created SuccessFully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignupScreen.this, LoginScreen.class);
                        startActivity(intent);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(SignupScreen.this, "Some Error occured "+ error, Toast.LENGTH_SHORT).show();
                        Log.d("Error in Signup",error.toString());
                    }
                });
                requestQueue.add(jsonObjectRequest);
            }
        });
    }

}