package com.example.android_final_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.android_final_app.DB.memberDBHelper;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "LoginActivity"; // 디버깅을 위한 태그 추가
    private Button btnLogin; // 로그인 버튼
    private Button btnGoJoin; // 회원가입 버튼
    private Toast objToast; // 토스트 메시지 객체
    private EditText editTextId; // ID 입력 필드
    private EditText editTextPassword; // 비밀번호 입력 필드
    private memberDBHelper dbHelper; // 데이터베이스 헬퍼 객체

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // 레이아웃 설정

        dbHelper = new memberDBHelper(this); // 데이터베이스 헬퍼 초기화

        btnLogin = findViewById(R.id.btnLogin); // 로그인 버튼 초기화
        btnLogin.setOnClickListener(this); // 클릭 리스너 설정

        btnGoJoin = findViewById(R.id.btnGoJoin); // 회원가입 버튼 초기화
        btnGoJoin.setOnClickListener(this); // 클릭 리스너 설정

        editTextId = findViewById(R.id.editTextId); // ID 입력 필드 초기화
        editTextPassword = findViewById(R.id.editTextPassword); // 비밀번호 입력 필드 초기화

        // 시스템 바의 인셋을 적용하여 UI가 화면에 잘 맞도록 설정
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public void onClick(View v) {
        if (v == btnLogin) { // 로그인 버튼 클릭 시
            String id = editTextId.getText().toString(); // 입력된 ID 가져오기
            String password = editTextPassword.getText().toString(); // 입력된 비밀번호 가져오기
            Log.d(TAG, "ID: " + id); // 디버그 로그 출력

            if (isValidLogin(id, password)) { // 로그인 유효성 검사
                Log.d(TAG, "로그인 성공"); // 디버그 로그 출력

                // 로그인 상태 저장
                SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("isLoggedIn", true);
                editor.putString("userName", id); // 예시로 ID를 사용자 이름으로 저장
                editor.apply();

                Log.d(TAG, "로그인 상태 저장 완료"); // 디버그 로그 출력

                // 로그인 성공 메시지 표시
                Toast.makeText(LoginActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();

                // 메인 액티비티로 이동
                Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(mainIntent);
                finish(); // 현재 액티비티 종료
            } else {
                Log.d(TAG, "로그인 실패"); // 디버그 로그 출력
                Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show(); // 로그인 실패 메시지 표시
            }
        } else if (v == btnGoJoin) { // 회원가입 버튼 클릭 시
            Intent joinIntent = new Intent(LoginActivity.this, JoinActivity.class); // 회원가입 액티비티로 이동
            startActivity(joinIntent); // 액티비티 시작
        }
    }

    private boolean isValidLogin(String id, String password) {
        Cursor cursor = dbHelper.getMember(id, password); // 데이터베이스에서 회원 정보 가져오기
        boolean isValid = cursor != null && cursor.getCount() > 0; // 회원 정보가 존재하는지 확인
        if (cursor != null) {
            Log.d(TAG, "Cursor count: " + cursor.getCount()); // 디버그 로그 출력
            cursor.close();  // 커서 닫기
        } else {
            Log.d(TAG, "Cursor is null"); // 디버그 로그 출력
        }
        return isValid; // 로그인 유효성 결과 반환
    }
}
