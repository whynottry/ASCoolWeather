package com.coolweather.app.activity;

import android.app.Activity;
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
import com.coolweather.app.util.WeatherUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class ChooseAreaActivity extends Activity {

    private ListView listView;
    private ArrayAdapter<String> adapter;
    private CoolWeatherDB coolWeatherDB;
    private List<String> dataList = new ArrayList<String>();
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

        final WeatherUtil weatherUtil = new WeatherUtil();

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
                            queryProvinces();
                        }
                    });
                }

                @Override
                public void onError(Exception e) {

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
                            queryCities(code);
                        }
                    });
                }

                @Override
                public void onError(Exception e) {

                }
            });
        }

    }

        //省数据存库
        /*weatherUtil.getAllProvinceInfo(PROVINCE_CODE_URL,coolWeatherDB,
                new WeatherUtil.HttpCallbackListener() {
                    @Override
                    public void onFinish(InputStream inputStream) {
                        Document document = null;

                        //抽象工厂类
                        DocumentBuilderFactory documentBF = DocumentBuilderFactory.newInstance();
                        documentBF.setNamespaceAware(true);
                        //DOM解析器对象
                        DocumentBuilder documentB = null;
                        try {
                            documentB = documentBF.newDocumentBuilder();
                        } catch (ParserConfigurationException e) {
                            e.printStackTrace();
                        }
                        try {
                            try {
                                document = documentB.parse(inputStream);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } catch (SAXException e) {
                            e.printStackTrace();
                        }
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

                        Log.d("test", "save db done");
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
        provinceList = coolWeatherDB.loadProvinces();

        if(provinceList.size() > 0){
            dataList.clear();
            for(Province province:provinceList){
                dataList.add(province.getProvincename());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            Log.d("test", "provinceList size > 0");
        }else{
            Log.d("test", "provinceList size = 0");
        }


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                final Province province = provinceList.get(index);
                Toast.makeText(ChooseAreaActivity.this,province.getProvincename(),Toast.LENGTH_LONG).show();

                String cityUrl = CITY_CODE_URL + province.getProvincecode();
                weatherUtil.getAllProvinceInfo(cityUrl,coolWeatherDB,
                        new WeatherUtil.HttpCallbackListener() {
                            @Override
                            public void onFinish(InputStream inputStream) {
                                Document doc = null;
                                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                                dbf.setNamespaceAware(true);
                                int cityCode = 0;
                                DocumentBuilder db = null;
                                try {
                                    db = dbf.newDocumentBuilder();
                                } catch (ParserConfigurationException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    doc = db.parse(inputStream);
                                } catch (SAXException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                NodeList nl = doc.getElementsByTagName("string");    //具体webService相关
                                int len = nl.getLength();
                                //dataList.clear();
                                for(int i = 0; i < len; i++){
                                    Node n = nl.item(i);
                                    String result = n.getFirstChild().getNodeValue();
                                    String[] address = result.split(",");

                                    City city = new City();
                                    city.setCityName(address[0]);
                                    city.setCityCode(address[1]);
                                    city.setProvinceId(province.getProvincecode());

                                    //dataList.add(address[0]);

                                    coolWeatherDB.saveCity(city);
                                }

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        dataList.clear();
                                        List<City> cities = coolWeatherDB.loadCities(province.getProvincecode());
                                        int len = cities.size();
                                        for(int i = 0; i < len; i++){
                                            dataList.add(cities.get(i).getCityName());
                                        }
                                        adapter.notifyDataSetChanged();
                                        listView.setSelection(0);
                                    }
                                });
                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        });
            }
        });

    }*/


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
