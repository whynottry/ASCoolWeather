package com.coolweather.app.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.coolweather.app.R;
import com.coolweather.app.model.City;
import com.coolweather.app.model.CoolWeatherDB;
import com.coolweather.app.model.Province;
import com.coolweather.app.util.HttpUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.util.ArrayList;
import java.util.List;

public class ChooseAreaActivity extends Activity {

    private ListView listView;
    private ArrayAdapter<String> adapter;
    private CoolWeatherDB coolWeatherDB;
    private List<String> dataList = new ArrayList<String>();
    private ProgressDialog progressDialog;
    private List<Province> provinceList;
    private List<City> cityList;

    private static final int LEVEL_PROVINCE = 0;
    private static final int LEVEL_CITY = 1;
    private int currenLevel = 0;

    private static String WEATHER_SERVICE_URL =
            "http://webservice.webxml.com.cn/WebServices/WeatherWS.asmx/";
    private static String PROVINCE_CODE_URL = WEATHER_SERVICE_URL
            + "getRegionProvince";
    private static String CITY_CODE_URL = WEATHER_SERVICE_URL
            + "getSupportCityString?theRegionCode=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_area);

        listView = (ListView)findViewById(R.id.list_view);

        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);

        coolWeatherDB = CoolWeatherDB.getInstance(this);

        queryProvinces();  //加载省数据

        //ListViewItem项点击相应
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                if(currenLevel == LEVEL_PROVINCE){
                    Province selProvince = provinceList.get(index);
                    //从web服务器查询城市的信息，并存到数据库中
                    queryFromServer(selProvince.getProvincecode(),LEVEL_CITY);
                }
                if(currenLevel == LEVEL_CITY){
                    City selCity = cityList.get(index);
                    Intent intent = new Intent(ChooseAreaActivity.this, WeatherShowActivity.class);
                    intent.putExtra("cityCode",selCity.getCityCode());
                    startActivity(intent);
                }
            }
        });
    }

    void queryProvinces(){
        provinceList = coolWeatherDB.loadProvinces();
        //数据库中已经有了省的信息
        if(provinceList.size() > 0){
            int len = provinceList.size();
            dataList.clear();
            for(int i = 0; i < len; i++){
                dataList.add(provinceList.get(i).getProvincename());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currenLevel = LEVEL_PROVINCE;
            Log.d("ChooseAreaActivity","queryProvinces from the db");
        }else{
            //从web服务器中读取，并存到数据库中
            queryFromServer(null,LEVEL_PROVINCE);
        }
    }

    void queryCities(String code){
        cityList = coolWeatherDB.loadCities(code);
        //数据库中已经有了省的信息
        if(cityList.size() > 0){
            int len = cityList.size();
            dataList.clear();
            for(int i = 0; i < len; i++){
                dataList.add(cityList.get(i).getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currenLevel = LEVEL_CITY;
        }else{
            //从web服务器中读取，并存到数据库中
            queryFromServer(null,LEVEL_CITY);
        }
    }

    void queryFromServer(final String code, final int level){
        showProgressDlg();
        if(level == LEVEL_PROVINCE){
            HttpUtil.sendHttpRequest(PROVINCE_CODE_URL,new HttpUtil.HttpCallbackListener() {
                @Override
                public void onFinish(Document document) {
                    NodeList nodeList = document.getElementsByTagName("string");
                    int len = nodeList.getLength();
                    for(int i = 0; i < len; i++){
                        Node n = nodeList.item(i);
                        String result = n.getFirstChild().getNodeValue();
                        String[] address = result.split(",");
                        Province province = new Province();
                        province.setProvincename(address[0]);
                        province.setProvincecode(address[1]);
                        coolWeatherDB.saveProvince(province);
                    }
                    Log.d("ChooseAreaActivity", "save provinces to table done.");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDlg();

                            queryProvinces();
                        }
                    });
                }

                @Override
                public void onError(Exception e) {
                    closeProgressDlg();
                    Toast.makeText(ChooseAreaActivity.this,"加载失败",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }else if(level == LEVEL_CITY){
            String addressUrl = CITY_CODE_URL + code;
            HttpUtil.sendHttpRequest(addressUrl,new HttpUtil.HttpCallbackListener() {
                @Override
                public void onFinish(Document document) {
                    NodeList nl = document.getElementsByTagName("string");    //具体webService相关
                    int len = nl.getLength();
                    for(int i = 0; i < len; i++){
                        Node n = nl.item(i);
                        String result = n.getFirstChild().getNodeValue();
                        String[] address = result.split(",");

                        City city = new City();
                        city.setCityName(address[0]);
                        city.setCityCode(address[1]);
                        city.setProvinceId(code);
                        coolWeatherDB.saveCity(city);
                    }
                    Log.d("ChooseAreaActivity", "save cities to table done.");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDlg();
                            queryCities(code);
                        }
                    });
                }

                @Override
                public void onError(Exception e) {
                    closeProgressDlg();
                    Toast.makeText(ChooseAreaActivity.this,"加载失败",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    /**
     * 显示进度对话框
     */
    private void showProgressDlg(){
        if(progressDialog == null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * 关闭进度对话框
     */
    private void closeProgressDlg(){
        if(progressDialog != null){
            progressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        if(currenLevel == LEVEL_CITY){
            queryProvinces();
        }else if(currenLevel == LEVEL_PROVINCE){
            finish();
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
