package com.example.android_final_app.DB;

public class Post {
    private int id;
    private String title;
    private String content;
    private String author;
    private String restaurant;
    private boolean recommend;

    public Post(int id, String title, String content, String author, String restaurant, boolean recommend) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.author = author;
        this.restaurant = restaurant;
        this.recommend = recommend;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getAuthor() {
        return author;
    }

    public String getRestaurant() {
        return restaurant;
    }

    public boolean isRecommend() {
        return recommend;
    }
}
