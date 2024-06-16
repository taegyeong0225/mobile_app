package com.example.android_final_app;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_final_app.DB.postDB;
import com.example.android_final_app.DB.postDBHelper;
import com.example.android_final_app.DB.Post;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private postDBHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewPosts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        dbHelper = new postDBHelper(getContext());
        List<Post> postList = getAllPostsFromDB();
        postAdapter = new PostAdapter(postList);
        recyclerView.setAdapter(postAdapter);

        return view;
    }

    private List<Post> getAllPostsFromDB() {
        List<Post> posts = new ArrayList<>();
        Cursor cursor = dbHelper.getAllPosts();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(postDB.COL_NO));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(postDB.COL_TITLE));
                String content = cursor.getString(cursor.getColumnIndexOrThrow(postDB.COL_CONTENT));
                String author = cursor.getString(cursor.getColumnIndexOrThrow(postDB.COL_AUTHOR));
                String restaurant = cursor.getString(cursor.getColumnIndexOrThrow(postDB.COL_RESTAURANT));
                boolean recommend = cursor.getInt(cursor.getColumnIndexOrThrow(postDB.COL_RECOMMEND)) > 0;

                Post post = new Post(id, title, content, author, restaurant, recommend);
                posts.add(post);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return posts;
    }
}
