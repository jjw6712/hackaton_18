package com.example.sw_18;

public class MessageBody {
    private int roomId;
    private String userId;
    private String ment;

    public MessageBody(int roomId, String userId, String ment) {
        this.roomId = roomId;
        this.userId = userId;
        this.ment = ment;
    }

    // 필요한 경우 getter와 setter 메소드 추가...
}
