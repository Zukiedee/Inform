package com.communityapp.inform.Model;

public class User {
    private String Username;
    private String[] Communities;

    public User (String username, String[] communities){
        this.Username = username;
        this.Communities = communities;
    }


    public String getUsername() {
        return Username;
    }

    public String[] getCommunities() {
        return Communities;
    }
}
