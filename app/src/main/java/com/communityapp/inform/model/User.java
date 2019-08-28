package com.communityapp.inform.model;

import java.util.ArrayList;

/**
 * User profile account details containing username and
 */
public class User {
    private String Username;
    private String Email;
    private String Type;
    private ArrayList<String> Communities;

    /**
     * Constructor method to set username and email
     * @param username User's username
     */
    public User (String username, String email){
        this.Username = username;
        this.Email = email;
    }

    /**
     * Sets the users list of communities they wish to follow
     * @param communities list of selected communities
     */
    public void setCommunities(ArrayList<String> communities){
        this.Communities = communities;
    }

    /**
     * Sets the users account type
     * @param type account type of user
     */
    public void setType(String type) { Type = type; }

    /**
     * Returns User's username.
     * @return User's username.
     */
    public String getUsername() { return Username; }

    /**
     * Returns User's email address.
     * @return User's email address.
     */
    public String getEmail() { return Email; }

    /**
     * Returns User's account type.
     * @return User's type of account.
     */
    public String getType(){ return Type; }

    /**
     * Returns user's communities they follow
     * @return list of communities
     */
    public ArrayList<String> getCommunities() { return Communities; }

}
