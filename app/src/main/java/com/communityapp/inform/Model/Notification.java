package com.communityapp.inform.Model;

/**
 * Notifications sent to the user regarding their notice requests from the admin.
 * Notifications appear in the user's Inbox.
 */
public class Notification {
    private String Title, Status, Message, Date;

    /**
     * Default system notification sent to user, when request is made
     * @param header Title of the notification i.e. requested post name.
     * @param date Date the notification is sent
     */
    public Notification(String header, String date) {
        Title = header;
        Status = "Pending";
        Message = "Your request to post a notice has been received.\nWe are currently reviewing your notice and will let you know of the outcome as soon as possible";
        Date = date;
    }

    /**
     * Sets the notification status and message description to rejected.
     */
    public void reject() {
        Message = "Unfortunately, your post has been rejected as it does not meet the community standard.\nPlease adjust it accordingly and try again.";
        Status = "Rejected";
    }

    /**
     * Sets the notification status and message description to approved.
     */
    public void approve() {
        Message = "Your post has been approved!\nYour notice now appears in the newsfeed";
        Status = "Approved";
    }

    /**
     * Returns notification title.
     * @return Title of the notification  i.e. requested post name.
     */
    public String getTitle(){
        return Title;
    }

    /**
     * Returns message sent to user regarding status of requested notice
     * @return Message describing whether post has been pending, accepted or rejected.
     */
    public String getMessage(){
        return Message;
    }

    /**
     * Returns date the notification is sent.
     * @return Date of notification.
     */
    public String getDate(){
        return Date;
    }

    /**
     * Returns the status of the notification.
     * @return Status of the notice request.
     */
    public String getStatus() {
        return "Status: "+ Status;
    }
}
