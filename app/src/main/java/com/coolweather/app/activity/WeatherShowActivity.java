package com.coolweather.app.activity;

//import android.app.ListFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.app.R;
import com.coolweather.app.circlePageIndicator.CirclePageIndicator;
import com.coolweather.app.model.City;
import com.coolweather.app.model.CoolWeatherDB;
import com.coolweather.app.model.Weather;
import com.coolweather.app.refreash.RefreshableHelper;
import com.coolweather.app.refreash.RefreshableView;
import com.coolweather.app.refreash.WeatherFragmentAdapter;
import com.coolweather.app.util.ActivityCollector;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.WeatherAdapterUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class WeatherShowActivity extends BaseSampleActivity {

    WeatherFragmentAdapter mAdapter;
    ViewPager mPager;
    CirclePageIndicator mIndicator;

    private static final String TAG = WeatherShowActivity.class.getSimpleName();



    private List<Weather> weatherList = new ArrayList<Weather>();
    //private TextView city_name_view;
    private TextView today_date_view;
    private TextView today_sunny_view;
    private ImageView today_image_view;
    private TextView today_tempreature_view;
    private TextView today_wind_view;
    private TextView today_air_view;
    private TextView today_ultraviolet_view;
    private TextView today_humidity_view;
    private TextView cityNameTitle;
    static List<String> allWeatherInfo = new ArrayList<String>();
    private City city = new City();
    private List<City> citys;

    static final int MAX_CITY_COUNT = 1;

    private Button home_btn;
    private Button refresh_btn;

    private WeatherAdapterUtil adapter;
    private RefreshableView refreshableView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        ActivityCollector.addActivity(this);
        setContentView(R.layout.activity_weather_show);

        initView();
        citys = new ArrayList<City>();


        SharedPreferences.Editor editor = getSharedPreferences("citys_data",MODE_PRIVATE).edit();
        //

//        editor.clear();
//        editor.commit();

        //从文件中读城市
        //MODE_PRIVATE,只有当前应用程序可操作
        //如果文件中没有，则创建该文件。
        SharedPreferences pref = getSharedPreferences("citys_data",MODE_PRIVATE);
        Set<String> codeResults = new HashSet<String>();
        codeResults = pref.getStringSet("citys_code",new HashSet<String>());
        CoolWeatherDB coolWeatherDB = CoolWeatherDB.getInstance(this);
        for(String s:codeResults){
            City cityTemp = new City();
            cityTemp.setCityCode(s);
            String cityTempName = coolWeatherDB.queryCityName(s);
            cityTemp.setCityName(cityTempName);
            citys.add(cityTemp);
        }

        Intent intent = getIntent();
        String cityName = intent.getStringExtra("cityName");
        String cityCode = intent.getStringExtra("cityCode");
        if(TextUtils.isEmpty(cityCode)){
            //定位信息
            cityCode = "1984";
            cityName = "江宁";
        }
        if(!ifCityExist(cityCode))
        {
            //将该城市存到文件中
            codeResults.add(cityCode);
            editor.clear();
            editor.putStringSet("citys_code",codeResults);
            editor.commit();

            //存到链表里
            city.setCityCode(cityCode);  //江宁的code为1984
            city.setCityName(cityName);
            citys.add(city);
        }

        cityNameTitle.setText(cityName);

        home_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(WeatherShowActivity.this,ChooseAreaActivity.class);
                startActivity(intent1);
            }
        });


        refresh_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent2 = new Intent(WeatherShowActivity.this,PullToRefreshExpandableListActivity.class);
                //startActivity(intent2);
            }
        });

        mAdapter = new WeatherFragmentAdapter(getSupportFragmentManager(),citys);

        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);

        mIndicator = (CirclePageIndicator)findViewById(R.id.indicator);

        mIndicator.setViewPager(mPager);

        //We set this on the indicator, NOT the pager
        mIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                String s = citys.get(position).getCityName();
                cityNameTitle.setText(citys.get(position).getCityName());
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

    }

    public boolean ifCityExist(String cityCode){
        for(City city:citys){
            if(city.getCityCode().equals(cityCode)){
                return true;
            }
        }
        return false;
    }

    public void initView() {
        home_btn = (Button) findViewById(R.id.home_btn);
        cityNameTitle = (TextView) findViewById(R.id.title_text);
        refresh_btn = (Button) findViewById(R.id.refresh_btn);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityCollector.finishAllActivities();
    }
}
