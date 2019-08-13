package com.communityapp.inform.Model;

//model
public class MessageItem {
    private String Title, Status, Message, Date;

    public MessageItem(String header, String status, String msg, String date) {
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
        return Status;
    }
}
