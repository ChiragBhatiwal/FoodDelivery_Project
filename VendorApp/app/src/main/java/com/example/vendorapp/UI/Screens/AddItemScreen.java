package com.example.vendorapp.UI.Screens;

import static okhttp3.MultipartBody.*;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.vendorapp.MainActivity;
import com.example.vendorapp.Utils.Constants;
import com.example.vendorapp.R;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddItemScreen extends AppCompatActivity {

    TextInputEditText itemName,itemPrice,itemDescription;
    RadioButton vegButton,nonVegButton;
    TextView titleText;
    CardView imagePickerView;
    ImageView itemImage;
    boolean imagePath = false;
    Button saveButton;
    int Get_Code = 01234567;
    String imageActualPath;
    private String restaurantId,token,dishType = "";

    @SuppressLint({"MissingInflatedId", "SourceLockedOrientationActivity"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_item_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        itemName = findViewById(R.id.itemName_AddItemScreen);
        itemPrice = findViewById(R.id.itemPrice_AddItemScreen);
        itemDescription = findViewById(R.id.itemDescription_AddItemScreen);
        itemImage = findViewById(R.id.itemImage_AddItemScreen);
        imagePickerView = findViewById(R.id.itemImagePick_AddItemScreen);
        titleText = findViewById(R.id.addScreenText_AddItemScreen);
        saveButton = findViewById(R.id.saveItemButton_AddItemScreen);
        vegButton = findViewById(R.id.vegRadioButton_AddItem);
        nonVegButton = findViewById(R.id.non_vegRadioButton_AddItem);

        titleText.setText("Add Items");

        SharedPreferences sharedPreferences = getSharedPreferences("User",MODE_PRIVATE);
        restaurantId = sharedPreferences.getString("restaurantId","");

        SharedPreferences sharedPreferences1 = getSharedPreferences("Login",MODE_PRIVATE);
        token = sharedPreferences1.getString("refreshToken","");

        checkPermissions();

        imagePickerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,123);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadData(imageActualPath);
            }
        });

        vegButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    dishType = "Veg";
                }
            }
        });

        nonVegButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    dishType = "Non-Veg";
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123 && resultCode == RESULT_OK) {
            if (data != null) {
                imagePath = true;
            }
            assert data != null;
            Uri uri = data.getData();
            if (imagePath) {
                itemImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
            itemImage.setImageURI(uri);
            imageActualPath = getRealPathOfImage(uri); // Set the actual path
        }
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Get_Code);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Get_Code) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can access the images now
            } else {
                Toast.makeText(this, "Permission denied to read external storage", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Image Path
    String getRealPathOfImage(Uri uri)
    {
        String[] projection = {MediaStore.Images.Media.DATA};
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Cursor cursor = getContentResolver().query(uri,projection,null,null);
            if (cursor != null) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                String path = cursor.getString(column_index);
                cursor.close();
                return path;
            }
        }
         return null;
    }

    void uploadData(String imagePath)
    {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)  // Increase connect timeout
                .writeTimeout(20, TimeUnit.SECONDS)    // Increase write timeout
                .readTimeout(30, TimeUnit.SECONDS)     // Increase read timeout
                .build();

        File file = new File(imagePath);
        RequestBody fileBody = RequestBody.create(MediaType.parse("image/jpeg"), file);

        RequestBody body = new Builder()
                .setType(FORM)
                .addFormDataPart("itemImages",file.getName(),fileBody)
                .addFormDataPart("itemName", String.valueOf(itemName.getText()))
                .addFormDataPart("itemPrice", String.valueOf(itemPrice.getText()))
                .addFormDataPart("itemDescription", String.valueOf(itemDescription.getText()))
                .addFormDataPart("dishType",dishType)
                .build();

        Request request = new Request.Builder()
                .url(Constants.Url+"/item/create/"+restaurantId)
                .addHeader("Authorization",token)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    Log.d("Error is ",e.toString());
                    Toast.makeText(AddItemScreen.this, "Error While Adding Item "+e, Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    String responseData = response.body().string();
                    Log.d("Response", "Success: " + responseData);
                }
                runOnUiThread(() -> {
                    Intent intent = new Intent(AddItemScreen.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                });
            }
        });
    }
}