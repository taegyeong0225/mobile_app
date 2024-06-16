package com.example.android_final_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.android_final_app.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity"; // 디버깅을 위한 태그 추가
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Edge-to-edge 설정
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.btnWritePost), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setBottomNavigationView();

        // 글 작성 버튼
        Button btnWritePost = findViewById(R.id.btnWritePost);
        btnWritePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WritePostActivity.class);
                startActivity(intent);
            }
        });

        // 앱 초기 실행 시 홈화면으로 설정
        if (savedInstanceState == null) {
            binding.bottomNavigationView.setSelectedItemId(R.id.fragment_home);
        }
    }

    private void setBottomNavigationView() {
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Fragment selectedFragment = null;

            if (itemId == R.id.fragment_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.fragment_register) {
                selectedFragment = new RegisterFragment();
            } else if (itemId == R.id.fragment_frequency) {
                selectedFragment = new FrequencyFragment();
            } else if (itemId == R.id.fragment_settings) {
                SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                boolean isLoggedIn = preferences.getBoolean("isLoggedIn", false);
                Log.d(TAG, "isLoggedIn: " + isLoggedIn);

                if (isLoggedIn) {
                    selectedFragment = new MembershipFragment();
                } else {
                    selectedFragment = new SettingsFragment();
                }
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.main_container, selectedFragment).commit();
                return true;
            }

            return false;
        });
    }
}
