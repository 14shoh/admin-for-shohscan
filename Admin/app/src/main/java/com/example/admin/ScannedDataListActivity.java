package com.example.admin;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ScannedDataListActivity extends AppCompatActivity {

    private DatabaseReference dataRef;
    private ListView listView;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanned_data_list);

        // Инициализация Firebase
        FirebaseApp.initializeApp(this);

        // Получение ссылки на базу данных "scanned_data"
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        dataRef = database.getReference("scanned_data");

        listView = findViewById(R.id.scannedDataListView);
        ArrayList<ScannedData> scannedDataList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        listView.setAdapter(adapter);

        fetchAndDisplayData();
    }

    private void fetchAndDisplayData() {
        dataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("Firebase", "Метод onDataChange вызван");
                adapter.clear();

                // Создайте карту для хранения сумм по датам
                Map<String, Double> dailySumMap = new HashMap<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ScannedData data = snapshot.getValue(ScannedData.class);

                    if (data != null) {
                        String dateTime = data.getDateTime();

                        // Разделите дату и время (предположим, что они разделены пробелом)
                        String[] parts = dateTime.split(" "); 

                        if (parts.length >= 1) {
                            String date = parts[0];

                            double value = data.getValue();

                            // Добавьте значение к сумме для данной даты
                            if (dailySumMap.containsKey(date)) {
                                Double currentSum = dailySumMap.get(date);
                                if (currentSum != null) {
                                    dailySumMap.put(date, currentSum + value);
                                } else {
                                    dailySumMap.put(date, value);
                                }
                            } else {
                                dailySumMap.put(date, value);
                            }
                        }
                    }
                }

                // Обновите интерфейс с полученными данными
                for (Map.Entry<String, Double> entry : dailySumMap.entrySet()) {
                    adapter.add(entry.getKey() + ": " + entry.getValue());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Обработка ошибок, если не удалось получить данные
                Log.e("Firebase", "Ошибка при извлечении данных: " + databaseError.getMessage());
            }
        });
    }
}
