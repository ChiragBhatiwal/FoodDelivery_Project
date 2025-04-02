package com.example.vendorapp.UI.Screens;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.vendorapp.MainActivity;
import com.example.vendorapp.R;
import com.example.vendorapp.Utils.SharedPreference;

import java.util.Objects;

public class SplashScreen extends AppCompatActivity {
    ImageView appIcon;
    TextView textView;
    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        appIcon = findViewById(R.id.appIcon_splashScreen);
        textView = findViewById(R.id.textView_splashScreen);

        textView.setText("Admin Dashboard");

        new Thread()
        {
            @Override
            public void run() {
                super.run();
                try {
                    Thread.sleep(1500);
                    SharedPreference sharedPreference = SharedPreference.getInstance(SplashScreen.this);
                    String isLogin = sharedPreference.getLoginInfo("refreshToken","0");
                    if(!Objects.equals(isLogin, "0")) {
                        Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                        startActivity(intent);
                    }
                    else{
                        Intent intent = new Intent(SplashScreen.this, LoginScreen.class);
                        startActivity(intent);
                    }
                    SplashScreen.super.finish();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }.start();
    }
}