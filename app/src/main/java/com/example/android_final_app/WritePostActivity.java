package com.example.android_final_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.android_final_app.DB.postDBHelper;
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

        // 게시글 저장 버튼 클릭 리스너 설정
        binding.btnSubmit.setOnClickListener(v -> savePost());
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
            Log.e("WritePostActivity", "Error parsing restaurant list JSON", e);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, restaurantList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerRestaurant.setAdapter(adapter);
    }

    private void savePost() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String author = sharedPreferences.getString("userName", "defaultUser"); // 로그인한 사용자 ID 가져오기

        String title = binding.etTitle.getText().toString();
        String content = binding.etContent.getText().toString();
        String restaurant = binding.spinnerRestaurant.getSelectedItem().toString();
        boolean recommend = binding.radioGroup.getCheckedRadioButtonId() == R.id.radioRecommended;

        postDBHelper dbHelper = new postDBHelper(this);
        long rowId = dbHelper.addPost(title, content, author, restaurant, recommend);

        if (rowId != -1) {
            Log.d("WritePostActivity", "Post saved with ID: " + rowId);
            Toast.makeText(this, "게시글이 저장되었습니다!", Toast.LENGTH_SHORT).show();
            // 메인 화면으로 돌아가기
            Intent mainIntent = new Intent(WritePostActivity.this, MainActivity.class);
            startActivity(mainIntent);
            finish();
        } else {
            Log.e("WritePostActivity", "Failed to save post");
            Toast.makeText(this, "게시글 저장 실패했습니다.", Toast.LENGTH_SHORT).show();
        }
    }
}
