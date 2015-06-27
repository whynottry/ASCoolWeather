package com.coolweather.app.activity;

//import android.app.ListFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.coolweather.app.R;
import com.coolweather.app.circlePageIndicator.CirclePageIndicator;
import com.coolweather.app.view.City;
import com.coolweather.app.refreash.WeatherFragmentAdapter;
import com.coolweather.app.util.ActivityCollector;
import com.coolweather.app.util.Constants;
import com.coolweather.app.util.MyApplication;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class WeatherShowActivity extends BaseSampleActivity {

    WeatherFragmentAdapter mAdapter;
    ViewPager mPager;
    CirclePageIndicator mIndicator;

    private static final String TAG = WeatherShowActivity.class.getSimpleName();

    private TextView cityNameTitle;
    private City city = new City();
    private ArrayList<City> citys_sel_list;

    private Button add_city_btn;
    private Button del_city_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        ActivityCollector.addActivity(this);
        setContentView(R.layout.activity_weather_show);

        initView();
        ((MyApplication)getApplication()).getCityArray();

        //加入定位的城市
        City locationCity = getLocationCity();
        if(((MyApplication)getApplication()).findCityIndex(locationCity) == Constants.CONNOT_FIND_CITY){
            //定位到的城市在列表中不存在
            ((MyApplication)getApplication()).addCity(locationCity);
        }

        Intent intent = getIntent();
        int citySelType = intent.getIntExtra("citySelType", 0);
        if(citySelType == Constants.ADD_CITY){
            City city = new City();
            city.setCityName(intent.getStringExtra("cityName"));
            city.setCityCode(intent.getStringExtra("cityCode"));
            int cityIndex = ((MyApplication)getApplication()).findCityIndex(city);
            if(cityIndex == Constants.CONNOT_FIND_CITY)
            {
                ((MyApplication)getApplication()).addCity(city);
            }
        }

        citys_sel_list =  ((MyApplication)getApplication()).getCitys_sel();

        ((MyApplication)getApplication()).saveCityArray();

        add_city_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(WeatherShowActivity.this,ChooseAreaActivity.class);
                startActivity(intent1);
            }
        });

        del_city_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(WeatherShowActivity.this,DelCityActivity.class);
                startActivity(intent2);
            }
        });

        mAdapter = new WeatherFragmentAdapter(getSupportFragmentManager(),citys_sel_list);

        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);

        mIndicator = (CirclePageIndicator)findViewById(R.id.indicator);

        mIndicator.setViewPager(mPager);

        //We set this on the indicator, NOT the pager
        mIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                String s = citys_sel_list.get(position).getCityName();
                cityNameTitle.setText(citys_sel_list.get(position).getCityName());
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

    }

    public void initView() {
        add_city_btn = (Button) findViewById(R.id.home_btn);
        cityNameTitle = (TextView) findViewById(R.id.title_text);
        del_city_btn = (Button) findViewById(R.id.refresh_btn);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityCollector.finishAllActivities();
    }

    public City getLocationCity(){
        City locationCity = new City();
        locationCity.setCityName("江宁");
        locationCity.setCityCode("1984");
        return locationCity;

    }
}
