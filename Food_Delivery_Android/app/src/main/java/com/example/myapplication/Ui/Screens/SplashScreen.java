package com.example.myapplication.Ui.Screens;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.Utility.Sockets;

public class SplashScreen extends AppCompatActivity {
    private Sockets socketHelper;
    ImageView imageView;
    TextView textView;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        imageView = findViewById(R.id.imageView_SplashScreen);
        textView = findViewById(R.id.tagLine_SplashScreen);
        textView.setText("Made with Love, For the Loved ones..");
        new Thread()
        {
            @Override
            public void run() {
                super.run();
                try {
                    Thread.sleep(2500);
                    SharedPreferences sharedPreferences = getSharedPreferences("Login",MODE_PRIVATE);
                    String token = sharedPreferences.getString("refreshToken","");
                    String userId = sharedPreferences.getString("userId", "");
                    Intent intent;
                    if(token == "")
                    {
                        Toast.makeText(SplashScreen.this, "Token Is Empty", Toast.LENGTH_SHORT).show();
                    }
                    socketHelper = Sockets.getInstance(SplashScreen.this, token);
                    socketHelper.connect();
                    socketHelper.emit("register",userId);
                    if(token != "")
                    {
                        intent = new Intent(SplashScreen.this,MainActivity.class);
                    }else{
                        intent = new Intent(SplashScreen.this, PhoneVerificationScreen.class);
                    }
                    startActivity(intent);
                    SplashScreen.super.finish();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }.start();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        SplashScreen.super.finish();
    }
}