package com.example.sw_18;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatListActivity extends AppCompatActivity implements ChatRoomAdapter.OnItemClickListener { //채팅방 리스트 불러오는 액티비티

    private RecyclerView recyclerView;
    private ChatRoomAdapter adapter;
    private List<ChatRoom> chatRoomList;


    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_list);

        recyclerView = findViewById(R.id.recyclerView);

        SharedPreferences preferences = getSharedPreferences("user_preferences", MODE_PRIVATE);
        userId = preferences.getString("user_id", null);

        if (userId != null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://gongdong-ktduj.run.goorm.site/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            ApiService apiService = retrofit.create(ApiService.class);

            Call<List<ChatRoom>> call = apiService.getChatRooms(userId);
            call.enqueue(new Callback<List<ChatRoom>>() {
                @Override
                public void onResponse(Call<List<ChatRoom>> call, Response<List<ChatRoom>> response) { //응답
                    if (response.isSuccessful()) {
                        chatRoomList = response.body();
                        adapter = new ChatRoomAdapter(ChatListActivity.this, chatRoomList);
                        adapter.setOnItemClickListener(ChatListActivity.this);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(ChatListActivity.this));
                    }
                }

                @Override
                public void onFailure(Call<List<ChatRoom>> call, Throwable t) {
                    Toast.makeText(ChatListActivity.this, "채팅방을 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onItemClick(int position) {
        ChatRoom clickedRoom = chatRoomList.get(position);
        String roomId = clickedRoom.getRoomId();
        String roomName = clickedRoom.getRoomName();
        String peopleCount = clickedRoom.getPeopleCount();

        AlertDialog.Builder builder = new AlertDialog.Builder(ChatListActivity.this);
        builder.setTitle("가입 확인");
        builder.setMessage(roomName + " 모임에 가입하시겠습니까?");
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 가입 요청 처리
                //joinChatRoom(roomId, userId);
                Intent intent = new Intent(ChatListActivity.this, ChatRoomActivity.class);
                intent.putExtra("roomId", roomId);
                intent.putExtra("userId", userId);
                //intent.putExtra("roomName", roomName);
                //intent.putExtra("peopleCount", peopleCount);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("아니요", null);
        builder.show();
    }

    private void joinChatRoom(String roomId, String userId) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://gongdong-ktduj.run.goorm.site/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        JoinChatRoomRequest request = new JoinChatRoomRequest(roomId, userId); // 요청 객체 생성

        Call<Void> call = apiService.joinChatRoom(request);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ChatListActivity.this, "모임 가입이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                    // 가입 성공 시 필요한 동작 수행
                    // 채팅방으로 이동
                    Intent intent = new Intent(ChatListActivity.this, ChatRoomActivity.class);
                    intent.putExtra("roomId", roomId);
                    intent.putExtra("userId", userId);
                    //intent.putExtra("roomName", roomName);
                    //intent.putExtra("peopleCount", peopleCount);
                    startActivity(intent);
                } else {
                    Toast.makeText(ChatListActivity.this, "모임 가입에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    // 가입 실패 시 필요한 동작 수행
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ChatListActivity.this, "서버 에러.", Toast.LENGTH_SHORT).show();
                // 가입 실패 시 필요한 동작 수행
            }
        });
    }
}