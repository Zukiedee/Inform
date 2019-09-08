package com.communityapp.inform.model;

public class Comments {
    private String Username, Comment, Date;

    /**
     * Empty default constructor method
     */
    public Comments(){}

    public Comments(String username, String comment, String date) {
        Username = username;
        Comment = "Your request to post a notice has been received.\nWe are currently reviewing your notice and will let you know of the outcome as soon as possible";
        Date = date;
    }


    /**
     * Returns notification title.
     * @return Title of the notification  i.e. requested post name.
     */
    public String getUsername(){ return Username; }

    /**
     * Returns message sent to user regarding status of requested notice
     * @return Message describing whether post has been pending, accepted or rejected.
     */
    public String getComment(){
        return Comment;
    }

    /**
     * Returns date the notification is sent.
     * @return Date of notification.
     */
    public String getDate(){ return Date; }
}
