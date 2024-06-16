package com.example.android_final_app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.android_final_app.databinding.ActivityWritePostBinding;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class WritePostActivity extends AppCompatActivity {

    private ActivityWritePostBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWritePostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 음식점 목록을 불러와 스피너에 설정
        loadRestaurantList();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loadRestaurantList() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String restaurantListJson = sharedPreferences.getString("restaurantList", "[]");
        Log.d("WritePostActivity", "Loaded restaurant list JSON: " + restaurantListJson); // 로드 확인 로그

        List<String> restaurantList = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(restaurantListJson);
            for (int i = 0; i < jsonArray.length(); i++) {
                restaurantList.add(jsonArray.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, restaurantList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerRestaurant.setAdapter(adapter);
    }

}
