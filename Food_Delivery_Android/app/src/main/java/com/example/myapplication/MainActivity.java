 package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.myapplication.Ui.Fragments.CartFragment;
import com.example.myapplication.Ui.Fragments.HomeFragment;
import com.example.myapplication.Ui.Fragments.ProfileFragment;
import com.example.myapplication.Ui.Fragments.SearchFragment;
import com.example.myapplication.Utility.Sockets;
import com.google.android.material.bottomnavigation.BottomNavigationView;

 public class MainActivity extends AppCompatActivity {
    private String token;
    BottomNavigationView bottomNavigationView;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        SharedPreferences sharedPreferences = getSharedPreferences("Login",MODE_PRIVATE);
        token = sharedPreferences.getString("refreshToken","");


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.BLACK);

            // Set icons to black for better visibility on light backgrounds
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
               if (itemId == R.id.Home_BottomNavigation)
               {
                   getFragment(new HomeFragment(),false);
               } else if (itemId == R.id.profile_BottomNavigation) {
                   getFragment(new ProfileFragment(),false);
               } else if (itemId == R.id.Search_BottomNavigation) {
                   getFragment(new SearchFragment(),false);
               }else{
                   getFragment(new CartFragment(),false);
               }
               return true;
            }
        });
        getFragment(new HomeFragment(),true);
    }

    void getFragment(Fragment fragment,boolean isInitialized)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if(isInitialized)
        {
            fragmentTransaction.replace(R.id.frameLayout_Main,fragment);
        }else{
            fragmentTransaction.replace(R.id.frameLayout_Main,fragment);
        }
        fragmentTransaction.commit();
    }

     @Override
     protected void onDestroy() {
         super.onDestroy();
         Sockets.getInstance(MainActivity.this,token).disconnect();
     }
 }