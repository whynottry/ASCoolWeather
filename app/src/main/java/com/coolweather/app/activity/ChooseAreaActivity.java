package com.coolweather.app.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.coolweather.app.R;
import com.coolweather.app.model.CoolWeatherDB;
import com.coolweather.app.model.Province;
import com.coolweather.app.util.WeatherUtil;

import java.util.ArrayList;
import java.util.List;

public class ChooseAreaActivity extends Activity {

    private ListView listView;
    private ArrayAdapter<String> adapter;
    private CoolWeatherDB coolWeatherDB;
    private List<String> dataList = new ArrayList<String>();
    private List<Province> provinceList;

    private static String WEATHER_SERVICE_URL =
            "http://webservice.webxml.com.cn/WebServices/WeatherWS.asmx/";
    private static String PROVINCE_CODE_URL = WEATHER_SERVICE_URL
            + "getRegionProvince";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_area);

        listView = (ListView)findViewById(R.id.list_view);

        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);

        coolWeatherDB = CoolWeatherDB.getInstance(this);

        WeatherUtil weatherUtil = new WeatherUtil();

        //provinceList = coolWeatherDB.loadProvinces();

        //weatherUtil.getAllProvinceInfo(PROVINCE_CODE_URL,coolWeatherDB);  //省数据存库
        provinceList = coolWeatherDB.loadProvinces();

        if(provinceList.size() > 0){
            dataList.clear();
            for(Province province:provinceList){
                dataList.add(province.getProvincename());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_choose_area, menu);
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
