package com.example.inform;

public class NoticeItem {
    private String Title, Description, Username, Date;

    public NoticeItem(String title, String body, String username, String date) {
        Title = title;
        Description = body;
        Username = username;
        Date = date;
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
}
