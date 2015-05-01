package com.coolweather.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.coolweather.app.R;
import com.coolweather.app.model.City;
import com.coolweather.app.model.Weather;
import com.coolweather.app.refreash.RefreshableHelper;
import com.coolweather.app.refreash.RefreshableView;
import com.coolweather.app.util.ActivityCollector;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.WeatherAdapterUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class WeatherShowActivity extends Activity {

    private static final String TAG = WeatherShowActivity.class.getSimpleName();

    private static String WEATHER_SERVICE_URL =
            "http://webservice.webxml.com.cn/WebServices/WeatherWS.asmx/";
    private static String WEATHER_QUERY_URL = WEATHER_SERVICE_URL
            + "getWeather?theUserID=&theCityCode=";

    private List<Weather> weatherList = new ArrayList<Weather>();
    private TextView city_name_view;
    private TextView today_date_view;
    private TextView today_sunny_view;
    private ImageView today_image_view;
    private TextView today_tempreature_view;
    private TextView today_wind_view;
    private TextView today_air_view;
    private TextView today_ultraviolet_view;
    private TextView today_humidity_view;
    List<String> allWeatherInfo = new ArrayList<String>();
    private City city = new City();

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

        Intent intent = getIntent();
        String cityName = intent.getStringExtra("cityName");
        String cityCode = intent.getStringExtra("cityCode");
        if(TextUtils.isEmpty(cityCode)){
            cityCode = "1984";
            cityName = "江宁";
        }
        city.setCityCode(cityCode);  //江宁的code为1984
        TextView cityNameTitle = (TextView)findViewById(R.id.title_text);
        cityNameTitle.setText(cityName);

        home_btn = (Button)findViewById(R.id.home_btn);
        home_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(WeatherShowActivity.this,ChooseAreaActivity.class);
                startActivity(intent1);
            }
        });

        refresh_btn = (Button)findViewById(R.id.refresh_btn);
        refresh_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent2 = new Intent(WeatherShowActivity.this,PullToRefreshExpandableListActivity.class);
                //startActivity(intent2);
            }
        });

        getAllWeatherInfo();

        adapter = new WeatherAdapterUtil(WeatherShowActivity.this,R.layout.weather_item,weatherList);
        ListView listView = (ListView)findViewById(R.id.weather_list);
        listView.setAdapter(adapter);
        setListViewHeightBasedOnChildren(listView);

        refreshableView = (RefreshableView) findViewById(R.id.main_refresh_view);
        refreshableView.setRefreshableHelper(new RefreshableHelper() {

            @Override
            public View onInitRefreshHeaderView() {
                return LayoutInflater.from(WeatherShowActivity.this).inflate(R.layout.refresh_head, null);
            }

            @Override
            public boolean onInitRefreshHeight(int originRefreshHeight) {
                refreshableView.setRefreshNormalHeight(refreshableView.getOriginRefreshHeight() / 3);
                refreshableView.setRefreshingHeight(refreshableView.getOriginRefreshHeight());
                refreshableView.setRefreshArrivedStateHeight(refreshableView.getOriginRefreshHeight());
                return false;
            }

            @Override
            public void onRefreshStateChanged(View refreshView, int refreshState) {
                TextView tv = (TextView) refreshView.findViewById(R.id.refresh_head_tv);
                switch (refreshState) {
                    case RefreshableView.STATE_REFRESH_NORMAL:
                        tv.setText("");
                        //tv.setBackgroundColor(0xff);
                        break;
                    case RefreshableView.STATE_REFRESH_NOT_ARRIVED:
                        tv.setText("往下拉可以刷新");
                        break;
                    case RefreshableView.STATE_REFRESH_ARRIVED:
                        tv.setText("放手可以刷新");
                        break;
                    case RefreshableView.STATE_REFRESHING:
                        tv.setText("正在刷新");
                        new Thread(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            Thread.sleep(1000l);
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    refreshableView.onCompleteRefresh();
                                                }
                                            });
                                        } catch (InterruptedException e) {
                                            Log.e(TAG, "_", e);
                                        }
                                    }
                                }
                        ).start();
                        break;

                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityCollector.finishAllActivities();
    }

    public void getAllWeatherInfo(){
        if(allWeatherInfo.size() > 0){
            String temp;
            String[] tempArray;
            city_name_view = (TextView)findViewById(R.id.city_name);
            today_date_view = (TextView)findViewById(R.id.today_date);
            today_sunny_view = (TextView)findViewById(R.id.today_sunny);
            today_image_view = (ImageView)findViewById(R.id.today_image);
            today_tempreature_view = (TextView)findViewById(R.id.today_temperature);
            today_wind_view = (TextView)findViewById(R.id.today_wind);
            today_air_view = (TextView)findViewById(R.id.today_air);
            today_ultraviolet_view = (TextView)findViewById(R.id.today_ultraviolet);
            today_humidity_view = (TextView)findViewById(R.id.today_humidity);

            city_name_view.setText(allWeatherInfo.get(1));
            temp = allWeatherInfo.get(3);
            tempArray = temp.split(" ");
            today_date_view.setText(tempArray[0]);
            temp = allWeatherInfo.get(7);
            tempArray = temp.split(" ");
            today_sunny_view.setText(tempArray[1]);
            temp = allWeatherInfo.get(5);
            tempArray = temp.split("；");
            today_air_view.setText(tempArray[0]);
            today_ultraviolet_view.setText(tempArray[1]);
            temp = allWeatherInfo.get(4);
            tempArray = temp.split("；");
            String[] tempArray2;
            tempArray2 = tempArray[0].split("：");
            today_tempreature_view.setText(tempArray2[2]);
            tempArray2 = tempArray[1].split("：");
            today_wind_view.setText(tempArray2[1]);
            today_humidity_view.setText(tempArray[2]);
            today_image_view.setImageResource(R.drawable.a_11);

            for(int i = 0; i < 5; i++){
                Weather weather = new Weather();
                temp = allWeatherInfo.get(7+5*i);
                tempArray = temp.split(" ");
                weather.setDate(tempArray[0]);
                weather.setSunny(tempArray[1]);
                weather.setTemperature(allWeatherInfo.get(8+5*i));
                weather.setWind(allWeatherInfo.get(9+5*i));
                weatherList.add(weather);
            }

        }else{
            getAllWeatherInfoByServer(city.getCityCode());
        }
    }

    public void getAllWeatherInfoByServer(String cityCode){
        String url = WEATHER_QUERY_URL+cityCode;
        HttpUtil.sendHttpRequest(url, new HttpUtil.HttpCallbackListener() {
            @Override
            public void onFinish(Document document) {
                NodeList nodeList = document.getElementsByTagName("string");
                int len = nodeList.getLength();
                for(int i = 0; i < len; i++){
                    Node n = nodeList.item(i);
                    String weather = n.getFirstChild().getNodeValue();
                    allWeatherInfo.add(weather);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getAllWeatherInfo();
                    }
                });
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        //ListAdapter listAdapter = listView.getAdapter();
        if (adapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = adapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = adapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight+ (listView.getDividerHeight() * (adapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_weather_show, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
