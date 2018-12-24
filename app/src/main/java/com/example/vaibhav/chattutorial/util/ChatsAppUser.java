package com.example.vaibhav.chattutorial.util;

import android.net.Uri;

public class ChatsAppUser {
    private String lastMessage;
    private String userName;
    private String userId;
    private Uri imageUri;

    public ChatsAppUser(String lastMessage, String name, String userId, Uri imageUri) {
        this.lastMessage = lastMessage;
        this.userId = userId;
        this.imageUri = imageUri;
        userName = name;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserId() {
        return userId;
    }

    public Uri getImageUri() {
        return imageUri;
    }


}
