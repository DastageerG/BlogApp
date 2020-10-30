package com.example.blogapp.model;

public class User
{
    private String userId;
    private String name;
    private String imageUrl;

    public User(String userId, String name, String imageUrl)
    {
        this.userId = userId;
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public User()
    {
    }

    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getImageUrl()
    {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl)
    {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString()
    {
        return "User{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
