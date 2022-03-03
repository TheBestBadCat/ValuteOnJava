package com.stanislavkorneev.valute;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MAIN";
    private final String LIST_KEY = "LIST_KEY";
    private ArrayList<String> textObjects = new ArrayList<>();

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // сохранение состояния при изменении конфигурации
        if (savedInstanceState == null) {
            new AsyncLoader().execute();
        } else {
            textObjects = savedInstanceState.getStringArrayList(LIST_KEY);
            recyclerView.setAdapter(new RecViewAdapter(getListOfValute(textObjects)));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(LIST_KEY, textObjects);
    }

    public class AsyncLoader extends AsyncTask<String, Void, String> {
        // получение данных в фоновом потоке
        @Override
        protected String doInBackground(String... strings) {
            try {
                // получение данных с сайта в текстовом формате
                URL sourceUrl = new URL("https://www.cbr-xml-daily.ru/daily_json.js");
                BufferedReader readerFromUrl = new BufferedReader(new InputStreamReader(sourceUrl.openStream()));
                StringBuilder buffer = new StringBuilder();
                String inputLine;
                while ((inputLine = readerFromUrl.readLine()) != null) {
                    buffer.append(inputLine);
                }
                readerFromUrl.close();

                String json = buffer.toString();

                // разделение текста на "объекты" для последующей сериализации
                ArrayList<Integer> startIndexes = new ArrayList<>();
                ArrayList<Integer> endIndexes = new ArrayList<>();
                for (int i = 0; i < json.length(); i++) {
                    char c = json.charAt(i);
                    if (c == '{') {
                        startIndexes.add(i);
                    }
                    if (c == '}') {
                        endIndexes.add(i);
                    }
                }

                // добавление отформатированных текстовых данных в список
                for (int i = 2; i < startIndexes.size(); i++) {
                    textObjects.add(json.substring(startIndexes.get(i), endIndexes.get(i - 2) + 1));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "doInBackground");
            return null;
        }

        // передаем данные в пользовательский интерфейс
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            recyclerView.setAdapter(new RecViewAdapter(getListOfValute(textObjects)));
        }
    }

    // получаем список объектов класса Valute
    public List<Valute> getListOfValute(List<String> items) {
        List<Valute> resultList = new ArrayList<>();
        Gson gson = new Gson();
        for (String item : items) {
            Valute valute = gson.fromJson(item, Valute.class);
            resultList.add(valute);
        }
        return resultList;
    }

}