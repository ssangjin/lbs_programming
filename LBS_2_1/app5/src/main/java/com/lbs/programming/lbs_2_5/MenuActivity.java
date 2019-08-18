package com.lbs.programming.lbs_2_5;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "주문하기", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        setTitle("실내 음식점에 입장하신것을 환영합니다.");

        ArrayList<String> list = new ArrayList<>();
        list.add("App 사용 쿠폰 : 3000원");
        list.add("순메밀 회냉면 : 9000원");
        list.add("치즈 가락 떡볶이 : 8900원");
        list.add("2인세트 : 17500원");
        list.add("3인 세트 : 29000원");


        if (list != null)
        {
            ListView mListView = (ListView) findViewById(R.id.listView);
            ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, list);
            mListView.setAdapter(adapter);
        }

    }

}
