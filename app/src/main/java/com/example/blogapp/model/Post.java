package com.example.blogapp.model;

import com.google.firebase.Timestamp;

public class Post
{
    private String currentUserId;
    private String postDescription;
    private String postImageUrl;
    private Timestamp timestamp;


    public Post()
    {
        // Empty Constructor
    }

    public Post(String currentUserId, String postDescription, String postImageUrl, Timestamp timestamp)
    {
        this.currentUserId = currentUserId;
        this.postDescription = postDescription;
        this.postImageUrl = postImageUrl;
        this.timestamp = timestamp;
    } //

    public String getCurrentUserId()
    {
        return currentUserId;
    }

    public void setCurrentUserId(String currentUserId)
    {
        this.currentUserId = currentUserId;
    }

    public String getPostDescription()
    {
        return postDescription;
    }

    public void setPostDescription(String postDescription)
    {
        this.postDescription = postDescription;
    }

    public String getPostImageUrl()
    {
        return postImageUrl;
    }

    public void setPostImageUrl(String postImageUrl)
    {
        this.postImageUrl = postImageUrl;
    }

    public Timestamp getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp)
    {
        this.timestamp = timestamp;
    } //

    @Override
    public String toString()
    {
        return "Post{" +
                "currentUserId='" + currentUserId + '\'' +
                ", postDescription='" + postDescription + '\'' +
                ", postImageUrl='" + postImageUrl + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
