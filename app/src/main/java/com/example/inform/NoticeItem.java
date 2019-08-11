package com.example.inform;

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

    public void setTitle(String title) {
        Title = title;
    }

    public String getTitle(){
        return Title;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getDescription(){
        return Description;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getDate() {
        return Date;
    }

    public String getUsername(){
        return Username;
    }

    public void setImgResource(int imgResource) {
        this.imgResource = imgResource;
    }

    public int getImgResource() {
        return imgResource;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }
}
