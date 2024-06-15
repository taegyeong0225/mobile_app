package com.example.android_final_app;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.android_final_app.DB.memberDBHelper;

public class JoinActivity extends AppCompatActivity {

    private EditText editName, editEmail, editID, editPW, editCheckPW;
    private Button joinButton, idCheckButton;
    private memberDBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_join);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.btnWritePost), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new memberDBHelper(this);

        editName = findViewById(R.id.editName);
        editEmail = findViewById(R.id.editEmail);
        editID = findViewById(R.id.editID);
        editPW = findViewById(R.id.editPW);
        editCheckPW = findViewById(R.id.editCheckPW);
        joinButton = findViewById(R.id.btnJoin);
        idCheckButton = findViewById(R.id.btnCheckId);

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editName.getText().toString();
                String email = editEmail.getText().toString();
                String id = editID.getText().toString();
                String password = editPW.getText().toString();
                String checkPassword = editCheckPW.getText().toString();

                // 비밀번호 일치 여부 확인
                if (!password.equals(checkPassword)) {
                    Toast.makeText(JoinActivity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // ID가 비어 있는지 확인
                if (id.isEmpty()) {
                    Toast.makeText(JoinActivity.this, "ID를 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 회원 정보 삽입
                long newRowId = dbHelper.addMember(name, email, id, password);

                if (newRowId != -1) {
                    Toast.makeText(JoinActivity.this, "가입이 완료되었습니다!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(JoinActivity.this, "가입을 실패하였습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // ID 중복 확인 버튼 클릭 리스너
        idCheckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = editID.getText().toString();
                if (id.isEmpty()) {
                    Toast.makeText(JoinActivity.this, "ID를 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.d("JoinActivity", "Checking if ID exists: " + id);

                if (dbHelper.isUserIdExists(id)) {
                    Log.d("JoinActivity", "ID exists: " + id);
                    Toast.makeText(JoinActivity.this, "이미 존재하는 ID입니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("JoinActivity", "ID does not exist: " + id);
                    Toast.makeText(JoinActivity.this, "사용 가능한 ID입니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
