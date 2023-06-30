package com.example.sw_18;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.ChatRoomViewHolder> {

    private Context context;
    private List<ChatRoom> chatRoomList;
    private OnItemClickListener itemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public ChatRoomAdapter(Context context, List<ChatRoom> chatRoomList) {
        this.context = context;
        this.chatRoomList = chatRoomList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }


    @NonNull
    @Override
    public ChatRoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_room_item, parent, false);
        return new ChatRoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatRoomViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ChatRoom chatRoom = chatRoomList.get(position);
        holder.roomName.setText(chatRoom.getRoomName());
        holder.peopleCount.setText(chatRoom.getPeopleCount());
        holder.mPerPrice.setText(chatRoom.getMPerPrice());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatRoomList.size();
    }

    public interface xOnItemClickListener {
    }

    public static class ChatRoomViewHolder extends RecyclerView.ViewHolder {

        ImageView roomImage;
        TextView roomName;
        TextView peopleCount;
        TextView mPerPrice;

        public ChatRoomViewHolder(@NonNull View itemView) {
            super(itemView);
            roomImage = itemView.findViewById(R.id.roomImage);
            roomName = itemView.findViewById(R.id.roomName);
            peopleCount = itemView.findViewById(R.id.peopleCount);
            mPerPrice = itemView.findViewById(R.id.mPerPrice);
        }
    }
}
