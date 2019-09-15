package com.example.photouploader;

public class userModel
{
    public String name;
    public  String age;
    public  String userID;

    public String getDpLink() {
        return DpLink;
    }

    public void setDpLink(String dpLink) {
        DpLink = dpLink;
    }

    public  String DpLink;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
