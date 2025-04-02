package com.example.vendorapp.UI.Screens;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vendorapp.Data.Models.MenuScreen_Model;
import com.example.vendorapp.R;
import com.example.vendorapp.UI.Adapters.MenuRecyclerAdapter;
import com.example.vendorapp.Utils.SharedPreference;
import com.example.vendorapp.ViewModel.MenuScreen_ViewModel;

import java.util.List;

public class MenuScreen extends AppCompatActivity {
    TextView restaurantName;
    RecyclerView recyclerView;
    MenuRecyclerAdapter menuRecyclerAdapter;
    MenuScreen_ViewModel menuScreenViewModel;

    @SuppressLint({"MissingInflatedId", "SourceLockedOrientationActivity"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu_screen);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        menuScreenViewModel = new ViewModelProvider(this).get(MenuScreen_ViewModel.class);

        SharedPreference sharedPreference = SharedPreference.getInstance(MenuScreen.this);
        String token = sharedPreference.getLoginInfo("refreshToken","0");
        String restaurantId = sharedPreference.getUserInfo("restaurantId","0");
         Log.d("Restaurant Id in Menu Screen is :",restaurantId);
        menuScreenViewModel.fetchItem(getApplicationContext(),restaurantId,token);
        recyclerView = findViewById(R.id.menuRecyclerView_MenuScreen);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        menuRecyclerAdapter = new MenuRecyclerAdapter(MenuScreen.this);
        recyclerView.setAdapter(menuRecyclerAdapter);

        menuScreenViewModel.getItems().observe(this, new Observer<List<MenuScreen_Model>>() {
            @Override
            public void onChanged(List<MenuScreen_Model> menuScreenModels) {
                menuRecyclerAdapter.updateItems(menuScreenModels);
            }
        });
    }

}