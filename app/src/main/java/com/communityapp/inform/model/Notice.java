package com.communityapp.inform.model;

/**
 * Model Notice class.
 */
public class Notice {
    private String Title, Category, Description, Username, Date, Id, Image, Community;

    /**
     * Empty default constructor method
     */
    public Notice(){}

    /**
     * Constructor method for all Notices.
     * @param cat Category of the notice
     * @param date Date notice is posted
     * @param body Description of the notice
     * @param id Notice ID
     * @param img Image attachment of the Notice
     * @param title Title of the notice
     * @param username Username of user who posted the notice
     * @param community Community notice is posted to
     */
    public Notice(String cat, String date, String body, String id, String img, String title, String username, String community) {
        Title = title;
        Category = cat;
        Description = body;
        Username = username;
        Date = date;
        Image = img;
        Id = id;
        Community = community;
    }

    /**
     * Returns the category of the notice.
     * @return Notice category
     */
    public String getCategory() { return Category; }

    /**
     * Returns the date the notice was posted.
     * @return Date notice posted
     */
    public String getDate() { return Date; }

    /**
     * Returns the description of the notice.
     * @return Notice description
     */
    public String getDescription(){ return Description; }

    /**
     * Returns the ID of the notice.
     * @return Notice ID
     */
    public String getId() { return  Id; }

    /**
     * Returns the image attachment of the notice.
     * @return Image attachment to notice
     */
    public String getImage() { return Image; }

    /**
     * Returns the title of the notice.
     * @return Notice Title
     */
    public String getTitle(){ return Title; }

    /**
     * Returns the username of user who posted the notice.
     * @return User's username
     */
    public String getUsername(){ return Username; }

    /**
     * Returns the community the notice is posted to.
     * @return Community
     */
    public String getCommunity() { return Community; }
}
