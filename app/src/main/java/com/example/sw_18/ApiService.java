package com.example.sw_18;

import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @GET("/room")
    Call<List<ChatRoom>> getChatRooms(@Query("userId") String userId);

    @GET("room/{roomId}")
    Call<Room> getRoomDetails(@Path("roomId") String roomId);

    @POST("room/chat")
    Call<Void> sendUserDetailsToServer(@Body MessageBody messageBody);

    @POST("room/chat")
    Call<RoomChat> sendMessage(@Body MessageBody messageBody);


    @POST("/room/chat")
    Call<RoomChat> getMessages(@Body InitMessage messageBody);

    @POST("promise/create")
    Call<ResponseBody> setPromise(@Body Map<String, String> promiseData);

    //@POST("promise/create")
    //Call<JsonObject> getPromise(@Query("roomId") String roomId, @Query("promiseDate") String promiseDate);

    @POST("promise")
    Call<Void> sendRoomId(@Body RoomIdBody roomIdBody);

    @POST("promise/promiseDate")
    Call<List<PromiseData>> getPromiseDataForDate(@Body Map<String, String> data);
    @POST("room")
    Call<Void> joinChatRoom(@Body JoinChatRoomRequest request);

    @POST("promise/join")
    Call<ResponseBody> joinPromise(@Body Map<String, String> joinData);
    // Update the endpoint and request type accordingly
    @POST("promise/live")
    Call<Void> sendLiveLocationData(@Body JsonObject request);
    @POST("promise/live")
    Call<Void> sendLiveLocationData(@Body LiveLocationDataRequest request);

}