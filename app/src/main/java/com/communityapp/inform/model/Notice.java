package com.communityapp.inform.model;

public class Notice {
    private String Title, Category, Description, Username, Date, Id, Image;

    public Notice(String cat, String date, String body, String id, String img, String title, String username) {
        Title = title;
        Category = cat;
        Description = body;
        Username = username;
        Date = date;
        Image = img;
        Id = id;
    }

    public String getCategory() {
        return Category;
    }

    public String getDate() {
        return Date;
    }

    public String getDescription(){
        return Description;
    }

    public String getId() { return  Id; }

    public String getImage() {
        return Image;
    }

    public String getTitle(){
        return Title;
    }

    public String getUsername(){
        return Username;
    }
}
