package com.example.sw_18;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

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

public class ChatRoomActivity extends AppCompatActivity {

    private RecyclerView chatRecyclerView;
    private EditText inputMessage;
    private ImageButton sendMessageButton;
    private TextView roomName;
    private TextView peopleCount;

    private String userId;
    private String roomId;
    private ApiService apiService;

    private List<RoomChat.Chat> chatMessages = new ArrayList<>();
    private ChatAdapter chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        // Assuming you're passing the roomId and userId as Intent extras
        Intent intent = getIntent();
        roomId = intent.getStringExtra("roomId");
        userId = intent.getStringExtra("userId");
        if (roomId == null) {
            Log.e("ChatRoomActivity", "Room ID 가 없음");
            return;
        } else Log.e("ChatRoomActivity", "Room ID : " + roomId);

        // Initialize views
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        inputMessage = findViewById(R.id.inputMessage);
        sendMessageButton = findViewById(R.id.sendMessageButton);
        roomName = findViewById(R.id.roomName);
        peopleCount = findViewById(R.id.peopleCount);

        intent = getIntent();
        String roomNameValue = intent.getStringExtra("roomName");
        String peopleCountValue = intent.getStringExtra("peopleCount");

        roomName.setText(roomNameValue);
        peopleCount.setText(peopleCountValue);

        // Initialize apiService
        apiService = new Retrofit.Builder()
                .baseUrl("https://gongdong-ktduj.run.goorm.site/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService.class);

        // Initialize chatAdapter
        chatAdapter = new ChatAdapter(this, chatMessages, userId);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);

        // Load chat messages
        loadChatMessages();

        // Setup send message button
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = inputMessage.getText().toString();
                if (!message.trim().isEmpty()) {
                    sendMessageToServer(userId, Integer.parseInt(roomId), message);
                    inputMessage.setText("");
                }
            }
        });
        //loadRoomDetails();
        sendUserDetailsToServer(userId, roomId);

        ImageButton calendarButton = findViewById(R.id.calendarButton);
        calendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 서버로 roomId를 전송
                sendRoomIdToServer(roomId, new Runnable() {
                    @Override
                    public void run() {
                        // 캘린더로 이동
                        Intent intent = new Intent(ChatRoomActivity.this, CalendarActivity.class);
                        intent.putExtra("roomId", roomId);
                        intent.putExtra("roomName", roomNameValue);
                        startActivity(intent);
                    }
                });
            }
        });
    }

    private void sendRoomIdToServer(String roomId, final Runnable onSuccess) {
        ApiService apiService = new Retrofit.Builder()
                .baseUrl("https://gongdong-ktduj.run.goorm.site/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService.class);
        RoomIdBody roomIdBody = new RoomIdBody(roomId);

        apiService.sendRoomId(roomIdBody).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("ChatActivity", "roomId가 서버에 성공적으로 전송되었습니다." + roomId);
                    onSuccess.run();
                } else {
                    Log.d("ChatActivity", "roomId 전송 실패: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("ChatActivity", "API 호출 실패: " + t.getMessage());
            }
        });
    }
    private void sendUserDetailsToServer(String userId, String roomId) {
        ApiService apiService = new Retrofit.Builder()
                .baseUrl("https://gongdong-ktduj.run.goorm.site/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService.class);

        MessageBody messageBody = new MessageBody(Integer.parseInt(roomId), userId, "");

        apiService.sendUserDetailsToServer(messageBody).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("ChatActivity", "사용자 정보가 서버에 성공적으로 보내졌습니다.");
                } else {
                    Log.d("ChatActivity", "사용자 정보 전송 실패: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("ChatActivity", "API 호출 실패: " + t.getMessage());
            }
        });
    }

    private void sendMessageToServer(String userId, int roomId, String ment) {
        MessageBody messageBody = new MessageBody(roomId, userId, ment);

        apiService.sendMessage(messageBody).enqueue(new Callback<RoomChat>() {
            @Override
            public void onResponse(Call<RoomChat> call, Response<RoomChat> response) {
                if (response.isSuccessful()) {
                    Log.d("ChatActivity", "메세지 보내짐");
                    // Reload messages
                    loadChatMessages();
                } else {
                    Log.d("ChatActivity", "메세지 실패: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<RoomChat> call, Throwable t) {
                Log.d("ChatActivity", "API 못불름: " + t.getMessage());
            }
        });
    }

    private void loadChatMessages() {
        apiService.getMessages(new InitMessage(Integer.parseInt(roomId), userId)).enqueue(new Callback<RoomChat>() {
            @Override
            public void onResponse(Call<RoomChat> call, Response<RoomChat> response) {
                if (response.isSuccessful()) {
                    RoomChat messages = response.body();
                    if (messages != null) {
                        chatMessages.clear();
                        chatMessages.addAll(messages.chatInfo);
                        chatAdapter.notifyDataSetChanged();

                        // 라스트 메세지 스크롤
                        chatRecyclerView.scrollToPosition(chatMessages.size() - 1);
                        // Log the messages
                        for (RoomChat.Chat message : chatMessages) {
                            Log.d("ChatActivity", "Message: " + message.ment);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<RoomChat> call, Throwable t) {
                // Error handling here
            }
        });
    }
}