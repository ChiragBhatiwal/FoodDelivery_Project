package com.example.vendorapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.vendorapp.UI.Screens.AddItemScreen;
import com.example.vendorapp.UI.Screens.CheckoutScreen;
import com.example.vendorapp.UI.Screens.LoginScreen;
import com.example.vendorapp.UI.Screens.MenuScreen;
import com.example.vendorapp.UI.Screens.ProfileScreen;

public class MainActivity extends AppCompatActivity {

    TextView logoutText;
    CardView menuView,addItemView,checkoutView,profileView;

    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        logoutText = findViewById(R.id.logoutText_MainScreen);
        menuView = findViewById(R.id.menuCardView_MainScreen);
        addItemView = findViewById(R.id.addItemView_MainScreen);
        checkoutView = findViewById(R.id.checkoutView_MainScreen);
        profileView = findViewById(R.id.profileView_MainScreen);

        menuView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = new Intent(MainActivity.this, MenuScreen.class);
               startActivity(intent);
            }
        });

        addItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddItemScreen.class);
                startActivity(intent);
            }
        });

        profileView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProfileScreen.class);
                startActivity(intent);
            }
        });

        checkoutView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CheckoutScreen.class);
                startActivity(intent);
            }
        });

        logoutText.setText("Log Out");

        logoutText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("Login",MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();
                Toast.makeText(MainActivity.this, "Logout Successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, LoginScreen.class);
                startActivity(intent);
                finish();
            }
        });
    }
}