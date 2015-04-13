package com.coolweather.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.coolweather.app.R;
import com.coolweather.app.model.City;
import com.coolweather.app.model.Weather;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_weather_show);

        Intent intent = getIntent();
        String cityName = intent.getStringExtra("cityName");
        String cityCode = intent.getStringExtra("cityCode");
        city.setCityCode(cityCode);
        TextView cityNameTitle = (TextView)findViewById(R.id.city_name);
        cityNameTitle.setText(cityName);

        getAllWeatherInfo();

        WeatherAdapterUtil adapter = new WeatherAdapterUtil(WeatherShowActivity.this,R.layout.weather_item,weatherList);
        ListView listView = (ListView)findViewById(R.id.weather_list);
        listView.setAdapter(adapter);


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
        HttpUtil.sendHttpRequest(WEATHER_QUERY_URL+cityCode, new HttpUtil.HttpCallbackListener() {
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
