package com.example.android_final_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_final_app.DB.Post;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> postList;

    public PostAdapter(List<Post> postList) {
        this.postList = postList;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.tvPostTitle.setText(post.getTitle());
        holder.tvPostAuthor.setText("작성자: " + post.getAuthor());
        holder.tvPostRecommend.setText(post.isRecommend() ? "추천" : "비추천");
        holder.tvPostContent.setText(post.getContent());
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    // 포스트 목록 업데이트 메서드
    public void updatePosts(List<Post> newPostList) {
        this.postList = newPostList;
        notifyDataSetChanged();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        public TextView tvPostTitle, tvPostAuthor, tvPostRecommend, tvPostContent;

        public PostViewHolder(View view) {
            super(view);
            tvPostTitle = view.findViewById(R.id.tvPostTitle);
            tvPostAuthor = view.findViewById(R.id.tvPostAuthor);
            tvPostRecommend = view.findViewById(R.id.tvPostRecommend);
            tvPostContent = view.findViewById(R.id.tvPostContent);
        }
    }
}
