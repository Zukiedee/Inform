package com.communityapp.inform.Model;

//model
public class Notification {
    private String Title, Status, Message, Date;

    public Notification(String header, String status, String msg, String date) {
        Title = header;
        Status = status;
        Message = msg;
        Date = date;
    }

    public String getTitle(){
        return Title;
    }

    public String getMessage(){
        return Message;
    }

    public String getDate(){
        return Date;
    }

    public String getStatus() {
        return "Status: "+ Status;
    }
}
