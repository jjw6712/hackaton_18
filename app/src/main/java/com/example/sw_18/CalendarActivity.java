package com.example.sw_18;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CalendarActivity extends AppCompatActivity {

    private TextView textViewPromiseTime;
    private TextView textViewPromiseMaxPrice;
    private TextView textViewRoomName;
    private TextView textViewPromiseDate;
    private TextView textViewPromisePlace;
    private String roomId;
    private String userId;
    private MaterialCalendarView calendarView;
    private AlertDialog dialog;  // dialog를 클래스 멤버로 이동
    private double lat;
    private double lng;
    private Button buttonJoinPromise; // 모임 참가 버튼
    private PromiseData selectedPromiseData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.celendar);

        calendarView = findViewById(R.id.calendarView);
        textViewRoomName = findViewById(R.id.textViewRoomName);
        textViewPromiseDate = findViewById(R.id.textViewPromiseDate);
        textViewPromiseTime = findViewById(R.id.textViewPromiseTime);
        textViewPromiseMaxPrice = findViewById(R.id.textViewPromiseMaxPrice);
        textViewPromisePlace = findViewById(R.id.textViewPromisePlace);
        Button buttonSetPromise = findViewById(R.id.buttonSetPromise);

        // roomId를 가져옵니다. 이 예에서는 인텐트로 전달되는 것으로 가정합니다.
        roomId = getIntent().getStringExtra("roomId");
        userId = getIntent().getStringExtra("userId");

        calendarView.setOnDateChangedListener((widget, date, selected) -> {
            String selectedDate = String.format("%d%02d%02d", date.getYear(), date.getMonth() + 1, date.getDay());
            fetchPromiseData(roomId, selectedDate);
        });

        // 모임 참가 버튼 참조
        buttonJoinPromise = findViewById(R.id.buttonJoinPromise);
        buttonJoinPromise.setOnClickListener(v -> {
            if (selectedPromiseData != null) {
                joinPromise(selectedPromiseData.ID, userId);
                Intent intent = new Intent(CalendarActivity.this, JoinMapActivity.class);
                intent.putExtra("promiseId", selectedPromiseData.ID);
                intent.putExtra("userId", userId);
                startActivity(intent);
            } else {
                Toast.makeText(CalendarActivity.this, "참가할 약속을 선택해주세요.", Toast.LENGTH_SHORT).show();
            }
        });

        buttonSetPromise.setOnClickListener(v -> showSetPromiseDialog());

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                this.lat = data.getDoubleExtra("lat", 0);
                this.lng = data.getDoubleExtra("lng", 0);

                // Log the latitude and longitude
                Log.d("LocationData", "경도: " + lat + ", 위도: " + lng);

                // TODO: lat, lng 데이터를 처리합니다.

                // 다이얼로그를 다시 표시합니다.
                dialog.show();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                // 사용자가 위치를 선택하지 않고 지도 액티비티를 종료한 경우
            }
        }
    }

    private void showSetPromiseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.promisepopup, null);

        EditText editPromiseDate = view.findViewById(R.id.editPromiseDate);
        EditText editPromiseTime = view.findViewById(R.id.editPromiseTime);
        EditText editMaxPrice = view.findViewById(R.id.editMaxPrice);
        Button buttonClose = view.findViewById(R.id.buttonClose);
        Button buttonSave = view.findViewById(R.id.buttonSave);
        Button buttonGps = view.findViewById(R.id.select_place);

        builder.setView(view);

        dialog = builder.create();  // 이전의 AlertDialog dialog = builder.create(); 를 dialog = builder.create(); 로 변경

        // 닫기 버튼을 누를 경우 다이얼로그 종료
        buttonClose.setOnClickListener(v -> dialog.dismiss());

        buttonGps.setOnClickListener(v -> {
            Intent intent = new Intent(CalendarActivity.this, MapActivity.class);
            startActivityForResult(intent, 1);  // 1은 임의의 요청 코드입니다.
        });

        // 저장 버튼을 누를 경우 서버에 데이터를 저장
        buttonSave.setOnClickListener(v -> {
            String promiseDate = editPromiseDate.getText().toString();
            String promiseTime = editPromiseTime.getText().toString();
            String maxPrice = editMaxPrice.getText().toString();

            // data map creation
            Map<String, String> promiseData = new HashMap<>();
            promiseData.put("roomId", roomId);
            promiseData.put("promiseDate", promiseDate);
            promiseData.put("promiseTime", promiseTime);
            promiseData.put("promiseMaxPrice", maxPrice);

            // add lat and lng directly to the map
            promiseData.put("lat", String.valueOf(lat));
            promiseData.put("lng", String.valueOf(lng));

            // Retrofit 객체 생성 및 인터페이스 연결
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://gongdong-ktduj.run.goorm.site/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            ApiService apiService = retrofit.create(ApiService.class);

            // 서버에 데이터 전송
            Call<ResponseBody> call = apiService.setPromise(promiseData);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(CalendarActivity.this, "약속이 저장되었습니다.", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        // 현재 선택된 날짜에 대한 새로운 약속 데이터를 다시 불러옵니다.
                        fetchPromiseData(roomId, promiseDate);
                    } else {
                        Toast.makeText(CalendarActivity.this, "서버에 문제가 있습니다.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(CalendarActivity.this, "네트워크 문제가 발생했습니다.", Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }


    private void fetchPromiseData(String roomId, String promiseDate) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://gongdong-ktduj.run.goorm.site/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        // promiseData 맵을 생성하고 roomId 및 promiseDate를 포함합니다.
        Map<String, String> data = new HashMap<>();
        data.put("roomId", roomId);
        data.put("promiseDate", promiseDate);

        // getPromiseDataForDate 메서드를 호출하고 data 맵을 전달합니다.
        Call<List<PromiseData>> call = apiService.getPromiseDataForDate(data);

        call.enqueue(new Callback<List<PromiseData>>() {
            @Override
            public void onResponse(Call<List<PromiseData>> call, Response<List<PromiseData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<PromiseData> promiseDataList = response.body();

                    // promiseDataList 를 사용하여 UI 업데이트
                    // 예시: TextView에 약속 정보 표시
                    HashSet<CalendarDay> datesWithEvents = new HashSet<>();
                    for (PromiseData promiseData : promiseDataList) {
                        String promiseTime = promiseData.promiseTime;
                        String promiseMaxPrice = promiseData.promiseMaxPrice;
                        String roomName = promiseData.roomName;
                        selectedPromiseData = promiseData;
                        selectedPromiseData.ID = promiseData.ID;
                        textViewRoomName.setText("모임 명: " + roomName);
                        textViewPromiseDate.setText("약속 날짜: " + promiseDate);
                        textViewPromiseTime.setText("약속 시간: " + promiseTime);
                        textViewPromiseMaxPrice.setText("최대 비용: " + promiseMaxPrice);

                        if (promiseTime != null) {
                            // 날짜를 CalendarDay 형식으로 변환하고 HashSet에 추가
                            int year = Integer.parseInt(promiseDate.substring(0, 4));
                            int month = Integer.parseInt(promiseDate.substring(4, 6)) - 1; // 월은 0부터 시작
                            int day = Integer.parseInt(promiseDate.substring(6, 8));
                            datesWithEvents.add(CalendarDay.from(year, month, day));
                            buttonJoinPromise.setEnabled(true);
                        }
                        else  buttonJoinPromise.setEnabled(false);

                    }
                    // 빨간점 데코레이터 추가
                    calendarView.addDecorator(new EventDecorator(Color.RED, datesWithEvents));
                } else {
                    Log.d("Response", "응답이 성공적이지 않음");
                }
            }

            @Override
            public void onFailure(Call<List<PromiseData>> call, Throwable t) {
                Log.d("Error", "네트워크 문제 또는 통신 오류로 인한 실패: " + t.getMessage());
            }
        });
    }

    private void joinPromise(int promiseId, String userId) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://gongdong-ktduj.run.goorm.site/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Map<String, String> joinData = new HashMap<>();
        joinData.put("promiseId", String.valueOf(promiseId));
        joinData.put("userId", userId);

        Call<ResponseBody> call = apiService.joinPromise(joinData);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CalendarActivity.this, "약속에 참가하였습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CalendarActivity.this, "서버에 문제가 있습니다.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(CalendarActivity.this, "네트워크 문제가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}