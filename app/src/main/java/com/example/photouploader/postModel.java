package com.example.photouploader;

import android.os.Parcelable;

import java.util.ArrayList;

public class postModel {
    public String img;
    public String desc;
    public String userID;
    public String postID;
    public ArrayList<String> likes = new ArrayList<>();

    public ArrayList<String> getLikes() {
        return likes;
    }

    public void setLikes(ArrayList<String> likes) {
        this.likes = likes;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getImg()
    {
        return img;
    }
    public String getDesc()
    {
        return desc;
    }
    public String getUserID()
    {
        return userID;
    }

    public void setImg(String img1)
    {
        this.img = img1;
    }

    public void setDesc(String desc1)
    {
        this.desc = desc1;
    }

    public void setUserID(String userID1)
    {
        this.userID = userID1;
    }
}
