package com.communityapp.inform.model;

public class Notice {
    private int imgResource;
    private String Title, Category, Description, Username, Date;

    public Notice(String title, String cat, String body, String username, String date, int img) {
        Title = title;
        Category = cat;
        Description = body;
        Username = username;
        Date = date;
        imgResource = img;
    }

    public String getTitle(){
        return Title;
    }

    public String getDescription(){
        return Description;
    }

    public String getDate() {
        return Date;
    }

    public String getUsername(){
        return Username;
    }

    public int getImgResource() {
        return imgResource;
    }

    public String getCategory() {
        return Category;
    }
}
