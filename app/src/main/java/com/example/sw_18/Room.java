package com.example.sw_18;

import com.google.gson.annotations.SerializedName;

public class Room { // 채팅방 인원수, 방이름 불러오는 게터세터
    @SerializedName("room_name")
    private String roomName;

    @SerializedName("people_count")
    private int peopleCount;

    public Room(String roomName, int peopleCount) {
        this.roomName = roomName;
        this.peopleCount = peopleCount;
    }

    public String getRoomName() {
        return roomName;
    }

    public int getPeopleCount() {
        return peopleCount;
    }
}