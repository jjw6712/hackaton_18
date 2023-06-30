package com.example.sw_18;

public class ChatRoom {

    private String roomId;
    private String roomName;
    private String hostUser;
    private String donateInfo;
    private String peopleCount;
    private String mPerPrice;
    private String donateDate;

    public ChatRoom(String roomId, String roomName, String hostUser, String donateInfo, String peopleCount, String mPerPrice, String donateDate) { //채팅방 게터 세터
        this.roomId = roomId;
        this.roomName = roomName;
        this.hostUser = hostUser;
        this.donateInfo = donateInfo;
        this.peopleCount = peopleCount;
        this.mPerPrice = mPerPrice;
        this.donateDate = donateDate;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getHostUser() {
        return hostUser;
    }

    public void setHostUser(String hostUser) {
        this.hostUser = hostUser;
    }

    public String getDonateInfo() {
        return donateInfo;
    }

    public void setDonateInfo(String donateInfo) {
        this.donateInfo = donateInfo;
    }

    public String getPeopleCount() {
        return peopleCount;
    }

    public void setPeopleCount(String peopleCount) {
        this.peopleCount = peopleCount;
    }

    public String getMPerPrice() {
        return mPerPrice;
    }

    public void setMPerPrice(String mPerPrice) {
        this.mPerPrice = mPerPrice;
    }

    public String getDonateDate() {
        return donateDate;
    }

    public void setDonateDate(String donateDate) {
        this.donateDate = donateDate;
    }
}