package com.example.android_final_app;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_final_app.DB.Post;
import com.example.android_final_app.DB.postDB;
import com.example.android_final_app.DB.postDBHelper;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView; // 리사이클러 뷰
    private PostAdapter postAdapter; // 포스트 어댑터
    private List<Post> postList; // 포스트 리스트

    private Spinner spinnerFilter; // 스피너 필터
    private postDBHelper dbHelper; // 데이터베이스 헬퍼

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // 리사이클러 뷰 초기화
        recyclerView = view.findViewById(R.id.recyclerViewPosts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 스피너 초기화
        spinnerFilter = view.findViewById(R.id.spinnerFilter);

        // 데이터베이스 헬퍼 초기화
        dbHelper = new postDBHelper(getContext());

        // 초기 포스트 목록 로드
        postList = getAllPostsFromDB(null);

        // 포스트 어댑터 설정
        postAdapter = new PostAdapter(postList);
        recyclerView.setAdapter(postAdapter);

        // 필터 옵션 로드
        loadRestaurantList();

        // 스피너 아이템 선택 리스너 설정
        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = (String) parent.getItemAtPosition(position);
                postList = getAllPostsFromDB(selectedCategory.equals("All") ? null : selectedCategory);
                postAdapter.updatePosts(postList);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 아무것도 선택되지 않았을 때
            }
        });

        return view;
    }

    // 음식점 목록을 불러와 스피너에 설정
    private void loadRestaurantList() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", getActivity().MODE_PRIVATE);
        String restaurantListJson = sharedPreferences.getString("restaurantList", "[]");
        Log.d("HomeFragment", "Loaded restaurant list JSON: " + restaurantListJson); // 로드 확인 로그

        List<String> restaurantList = new ArrayList<>();
        restaurantList.add("All"); // 모든 음식점 옵션 추가

        try {
            JSONArray jsonArray = new JSONArray(restaurantListJson);
            for (int i = 0; i < jsonArray.length(); i++) {
                restaurantList.add(jsonArray.getString(i));
            }
        } catch (JSONException e) {
            Log.e("HomeFragment", "Error parsing restaurant list JSON", e);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, restaurantList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilter.setAdapter(adapter);
    }

    // 데이터베이스에서 모든 포스트 가져오기
    private List<Post> getAllPostsFromDB(String category) {
        List<Post> posts = new ArrayList<>();
        Cursor cursor;

        // 카테고리가 null이면 모든 포스트 가져오기
        if (category == null || category.equals("All")) {
            cursor = dbHelper.getAllPosts();
        } else {
            cursor = dbHelper.getPostsByCategory(category);
        }

        // 커서에서 포스트 정보 읽어오기
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(postDB.COL_NO));
                String title = cursor.getString(cursor.getColumnIndex(postDB.COL_TITLE));
                String content = cursor.getString(cursor.getColumnIndex(postDB.COL_CONTENT));
                String author = cursor.getString(cursor.getColumnIndex(postDB.COL_AUTHOR));
                String restaurant = cursor.getString(cursor.getColumnIndex(postDB.COL_RESTAURANT));
                boolean recommend = cursor.getInt(cursor.getColumnIndex(postDB.COL_RECOMMEND)) > 0;
                Post post = new Post(id, title, content, author, restaurant, recommend);
                posts.add(post);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return posts;
    }
}
