package com.example.android_final_app;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.android_final_app.DB.postDBHelper;

public class HomeFragment extends Fragment {

    private TextView txtCommend;
    private TextView txtManyReview;
    private postDBHelper dbHelper;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // TextView 초기화
        txtCommend = view.findViewById(R.id.txtCommend);
        txtManyReview = view.findViewById(R.id.txtManyReview);

        // DB Helper 초기화
        dbHelper = new postDBHelper(getContext());

        // 가장 추천이 많은 식당 텍스트 설정
        txtCommend.setText(dbHelper.getTopRecommendedRestaurant());

        // 가장 리뷰가 많은 식당 텍스트 설정
        txtManyReview.setText(dbHelper.getMostReviewedRestaurant());

        return view;
    }
}
