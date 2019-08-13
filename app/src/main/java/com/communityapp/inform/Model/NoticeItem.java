package com.communityapp.inform.Model;

public class NoticeItem {
    private int imgResource;
    private String Title, Category, Description, Username, Date;

    public NoticeItem(String title, String cat, String body, String username, String date, int img) {
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
