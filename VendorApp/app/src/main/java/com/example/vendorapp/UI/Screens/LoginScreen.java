package com.example.vendorapp.UI.Screens;

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
import com.example.vendorapp.Utils.Constants;
import com.example.vendorapp.MainActivity;
import com.example.vendorapp.R;
import com.example.vendorapp.Utils.SharedPreference;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class LoginScreen extends AppCompatActivity {

    TextInputEditText username,password;
    Button loginButton;
    TextView signUpText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        username = findViewById(R.id.username_Field_LoginScreen);
        password = findViewById(R.id.password_Field_LoginScreen);
        loginButton = findViewById(R.id.loginButton_LoginScreen);
        signUpText = findViewById(R.id.signupText_LoginScreen);

        signUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginScreen.this, SignupScreen.class);
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestQueue requestQueue = Volley.newRequestQueue(LoginScreen.this);

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("username",username.getText().toString());
                    jsonObject.put("password",password.getText().toString());
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constants.Url + "/vendor/login-vendor", jsonObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject jsonObject1 = response.getJSONObject("data");
                            for (int i=0;i<jsonObject1.length();i++)
                            {
                                String refreshToken = jsonObject1.getString("refreshToken");
                                SharedPreference sharedPreference = SharedPreference.getInstance(LoginScreen.this);
                                sharedPreference.saveLoginInfo("refreshToken",refreshToken);
                                String restaurantId =jsonObject1.has("restaurantId") ? jsonObject1.getString("restaurantId") : "";
                                Log.d("Login Screen",restaurantId);
                                if(!restaurantId.isEmpty() && !restaurantId.isBlank() && !Objects.isNull(restaurantId))
                                {
                                    sharedPreference.saveUserInfo("restaurantId",restaurantId);
                                    Intent intent = new Intent(LoginScreen.this, MainActivity.class);
                                    startActivity(intent);
                                }
                                else{
                                    Intent intent = new Intent(LoginScreen.this, RestaurantSetupScreen.class);
                                    startActivity(intent);
                                }

                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error in Login",error.toString());
                        Toast.makeText(LoginScreen.this, "error occured while login", Toast.LENGTH_SHORT).show();
                    }
                });
                requestQueue.add(jsonObjectRequest);
            }
        });
    }

}