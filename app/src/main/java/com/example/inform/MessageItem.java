package com.example.inform;

//model
public class MessageItem {
    private String Title, Message, Date;

    protected MessageItem(String header, String msg, String date) {
        Title = header;
        Message = msg;
        Date = date;
    }

    protected String getTitle(){
        return Title;
    }

    protected String getMessage(){
        return Message;
    }

    protected String getDate(){
        return Date;
    }
}
