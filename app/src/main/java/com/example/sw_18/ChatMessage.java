package com.example.sw_18;

public class ChatMessage {
    private String userId;
    private String message;

    public ChatMessage(String userId, String message) { //메세지 게터 세터
        this.userId = userId;
        this.message = message;
    }

    public String getUserId() {
        return userId;
    }

    public String getMessage() {
        return message;
    }
}
