package com.coolweather.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.app.R;
import com.coolweather.app.util.Constants;
import com.coolweather.app.util.MyApplication;
import com.coolweather.app.view.City;
import com.coolweather.app.view.SlideCutListView;

import java.util.ArrayList;

public class DelCityActivity extends Activity  implements SlideCutListView.RemoveListener {
    private SlideCutListView slideCutListView ;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> citysName_sel_list;
    private TextView del_finish_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_del_city);
        init();
    }

    private void init(){
        citysName_sel_list =  ((MyApplication)getApplication()).getCitysName_sel();
        del_finish_text = (TextView)findViewById(R.id.del_city_return_text);
        slideCutListView = (SlideCutListView) findViewById(R.id.slideCutListView);
        slideCutListView.setRemoveListener(this);
        adapter = new ArrayAdapter<String>(this,R.layout.del_city_list_item,R.id.list_item,citysName_sel_list);
        slideCutListView.setAdapter(adapter);
        slideCutListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                //Toast.makeText(DelCityActivity.this, citys_sel_list.get(position), Toast.LENGTH_SHORT).show();
            }
        });
        del_finish_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DelCityActivity.this, WeatherShowActivity.class);
                int i = Constants.DEL_CITY;
                intent.putExtra("citySelType", i);
                startActivity(intent);
                finish();
            }
        });
    }

    //滑动删除之后的回调方法
    @Override
    public void removeItem(SlideCutListView.RemoveDirection direction, int position) {
        String cityName = adapter.getItem(position);
        adapter.remove(cityName);
        ((MyApplication)getApplication()).delCity(cityName);
        switch (direction) {
            case RIGHT:
                Toast.makeText(this, "向右删除  " + position, Toast.LENGTH_SHORT).show();
                break;
            case LEFT:
                Toast.makeText(this, "向左删除  "+ position, Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(DelCityActivity.this, WeatherShowActivity.class);
        int i = Constants.DEL_CITY;
        intent.putExtra("citySelType", i);
        startActivity(intent);
        finish();
    }
}
