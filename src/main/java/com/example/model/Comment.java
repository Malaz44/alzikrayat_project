package com.example.model;

import java.sql.Timestamp;

public class Comment {
    private int id;
    private int photoId;
    private int userId;
    private String userName;
    private String comment;
    private Timestamp dateTime;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getPhotoId() { return photoId; }
    public void setPhotoId(int photoId) { this.photoId = photoId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public Timestamp getDateTime() { return dateTime; }
    public void setDateTime(Timestamp dateTime) { this.dateTime = dateTime; }
}