package com.coolweather.app.refreash;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.coolweather.app.R;
import com.coolweather.app.model.City;
import com.coolweather.app.model.Weather;
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
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
* Created by Star on 2015/5/10.
*/
public class WeatherFragment extends Fragment {

    private static String WEATHER_SERVICE_URL =
            "http://webservice.webxml.com.cn/WebServices/WeatherWS.asmx/";
    private static String WEATHER_QUERY_URL = WEATHER_SERVICE_URL
            + "getWeather?theUserID=&theCityCode=";

    int pageNum;
    private City show_city;
    private static String city_code;
    private View view;
    private RefreshableView refreshableView;
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
    private WeatherAdapterUtil adapter;
    static List<String> allWeatherInfo = new ArrayList<String>();

    static WeatherFragment newInstance(int num, City city) {
        WeatherFragment f = new WeatherFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);

        city_code = city.getCityCode();

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pageNum = getArguments() != null ? getArguments().getInt("num") : 1;
//            if ((savedInstanceState != null) && savedInstanceState.containsKey(KEY_CONTENT)) {
//                mContent = savedInstanceState.getString(KEY_CONTENT);
//            }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        allWeatherInfo.clear();
        view = inflater.inflate(R.layout.refreshlayout,container,false);
        initRefreshPageView();
        //getAllWeatherInfo();
        readAllWeatherInfo();

        refreshableView.setRefreshableHelper(new RefreshableHelper() {

            @Override
            public View onInitRefreshHeaderView() {
                return LayoutInflater.from(getActivity()).inflate(R.layout.refresh_head, null);
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
                        tv.setVisibility(View.GONE);
                        //tv.setBackgroundColor(0xff);
                        break;
                    case RefreshableView.STATE_REFRESH_NOT_ARRIVED:
                        tv.setText("往下拉可以刷新");
                        tv.setVisibility(View.VISIBLE);
                        break;
                    case RefreshableView.STATE_REFRESH_ARRIVED:
                        tv.setText("放手可以刷新");
                        tv.setVisibility(View.VISIBLE);
                        break;
                    case RefreshableView.STATE_REFRESHING:
                        tv.setText("正在刷新");
                        tv.setVisibility(View.VISIBLE);
                        getAllWeatherInfoByServer(city_code);
                        refreshableView.onCompleteRefresh();
                        break;
                }
            }
        });

        return view;
    }

    public void initRefreshPageView(){

        refreshableView = (RefreshableView) view.findViewById(R.id.main_refresh_view);
        today_humidity_view = (TextView)view.findViewById(R.id.today_humidity);
        //city_name_view = (TextView)view.findViewById(R.id.city_name);
        today_date_view = (TextView)view.findViewById(R.id.today_date);
        today_sunny_view = (TextView)view.findViewById(R.id.today_sunny);
        today_image_view = (ImageView)view.findViewById(R.id.today_image);
        today_tempreature_view = (TextView)view.findViewById(R.id.today_temperature);
        today_wind_view = (TextView)view.findViewById(R.id.today_wind);
        today_air_view = (TextView)view.findViewById(R.id.today_air);
        today_ultraviolet_view = (TextView)view.findViewById(R.id.today_ultraviolet);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//            setListAdapter(new ArrayAdapter<String>(getActivity(),
//                    android.R.layout.simple_list_item_1, Cheeses.sCheeseStrings));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putString(KEY_CONTENT, mContent);
    }

    class RefreshAsyncTask extends AsyncTask<String, Integer, List<String>> {
        //List<String> allWeatherInfo;

        public RefreshAsyncTask() {
//            allWeatherInfo = new ArrayList<String>();
        }

        protected List<String> doInBackground(String... urls) {

            HttpURLConnection connection = null;

            try {
                URL url = new URL(urls[0]);
                connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(8000);
                connection.setReadTimeout(8000);
                InputStream in = connection.getInputStream();
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
                        document = documentB.parse(in);
                        NodeList nodeList = document.getElementsByTagName("string");
                        int len = nodeList.getLength();
                        if(len <= 1){
                            Log.d("WXDebug", "++++++++++++++++++++");
                            Log.d("WXDebug","24小时访问量超过限制");
                        }
//                        allWeatherInfo.clear();
                        for(int i = 0; i < len; i++){
                            Node n = nodeList.item(i);
                            String weather = "";
                            if(n.hasChildNodes()) {
                                weather = n.getFirstChild().getNodeValue();
                            }
                            allWeatherInfo.add(weather);
                        }
                        //写文件
                        saveSelCityInfo(urls[1]);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (SAXException e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {
                //回调onError()方法
                //listener.onErr                                                                                                                        or(e);
                e.printStackTrace();
            } finally {
                if(connection != null){
                    connection.disconnect();
                }
            }
            return allWeatherInfo;
        }

        protected void onProgressUpdate(Integer... progress) {
            //setProgressPercent(progress[0]);
        }

        protected void onPostExecute(List<String> allWeatherInfo) {
            //getAllWeatherInfo();
            readAllWeatherInfo();
        }
    }

    public void saveSelCityInfo(String cityCode){
        SharedPreferences.Editor editor;
        String fileName = cityCode + "_info";
        editor = (getActivity()).getSharedPreferences(fileName,(getActivity()).MODE_PRIVATE).edit();

        String temp;
        String[] tempArray;

        //initRefreshPageView();
        //editor.putString("city_name_view",allWeatherInfo.get(1));
        //city_name_view.setText(allWeatherInfo.get(1));
        temp = allWeatherInfo.get(3);
        tempArray = temp.split(" ");

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日    HH:mm:ss     ");
        Date curDate    =   new Date(System.currentTimeMillis());//获取当前时间
        String    str    =    formatter.format(curDate);
        str += allWeatherInfo.get(1);
        editor.putString("today_date_view",str);
        temp = allWeatherInfo.get(7);
        tempArray = temp.split(" ");
        if(tempArray.length >= 2) {
            editor.putString("today_sunny_view",tempArray[1]);
        }else {
            editor.putString("today_sunny_view","");
        }
        temp = allWeatherInfo.get(10);
        int resId = getResId(temp);
        //today_image_view.setImageResource(resId);
        editor.putInt("today_image_view", resId);
        temp = allWeatherInfo.get(5);
        tempArray = temp.split("；");
        if(tempArray.length >= 2) {
//            today_air_view.setText(tempArray[0]);
//            today_ultraviolet_view.setText(tempArray[1]);
            editor.putString("today_air_view",tempArray[0]);
            editor.putString("today_ultraviolet_view",tempArray[1]);
        }else{
            editor.putString("today_air_view","");
            editor.putString("today_ultraviolet_view","");
//            today_air_view.setText("");
//            today_ultraviolet_view.setText("");
        }
        temp = allWeatherInfo.get(4);
        tempArray = temp.split("；");
        String[] tempArray2;
        if(tempArray.length>=3) {
            tempArray2 = tempArray[0].split("：");
            if(tempArray2.length >=3) {
                //today_tempreature_view.setText(tempArray2[2]);
                editor.putString("today_tempreature_view",tempArray2[2]);
                tempArray2 = tempArray[1].split("：");
                //today_wind_view.setText(tempArray2[1]);
                editor.putString("today_wind_view",tempArray2[1]);
            }
            //today_humidity_view.setText(tempArray[2]);
            editor.putString("today_humidity_view",tempArray[2]);
        }

        weatherList.clear();
        String key = "";
        for(int i = 0; i < 5; i++){
            Weather weather = new Weather();
            temp = allWeatherInfo.get(7+5*i);

            tempArray = temp.split(" ");
            //weather.setDate(tempArray[0]);
            //weather.setSunny(tempArray[1]);
            key = "weatherList_data_"+i;
            editor.putString(key,tempArray[0]);
            key = "weatherList_sunny_"+i;
            editor.putString(key,tempArray[1]);
            key = "weatherList_temperature_"+i;
            editor.putString(key,allWeatherInfo.get(8+5*i));
            key = "weatherList_windy_"+i;
            editor.putString(key,allWeatherInfo.get(9+5*i));
//            weather.setTemperature(allWeatherInfo.get(8+5*i));
//            weather.setWind(allWeatherInfo.get(9+5*i));
//            weatherList.add(weather);
        }

        editor.commit();
    }

    public void readAllWeatherInfo(){
        String fileName = city_code + "_info";
        SharedPreferences pref = (getActivity()).getSharedPreferences(fileName,(getActivity()).MODE_PRIVATE);
        String today_date_view_str = pref.getString("today_date_view","");
        if(TextUtils.isEmpty(today_date_view_str)){
            getAllWeatherInfoByServer(city_code);
        }else{
            today_date_view.setText(today_date_view_str);
        }

    }

    public synchronized void getAllWeatherInfo(){
        if(allWeatherInfo.size() > 0){
            String temp;
            String[] tempArray;

            initRefreshPageView();
            //city_name_view.setText(allWeatherInfo.get(1));
            temp = allWeatherInfo.get(3);
            tempArray = temp.split(" ");

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日    HH:mm:ss     ");
            Date curDate    =   new Date(System.currentTimeMillis());//获取当前时间
            String    str    =    formatter.format(curDate);
            str += allWeatherInfo.get(1);
            today_date_view.setText(str);
            temp = allWeatherInfo.get(7);
            tempArray = temp.split(" ");
            if(tempArray.length >= 2) {
                today_sunny_view.setText(tempArray[1]);
            }else {
                today_sunny_view.setText("");
            }
            temp = allWeatherInfo.get(10);
            int resId = getResId(temp);
            today_image_view.setImageResource(resId);
            temp = allWeatherInfo.get(5);
            tempArray = temp.split("；");
            if(tempArray.length >= 2) {
                today_air_view.setText(tempArray[0]);
                today_ultraviolet_view.setText(tempArray[1]);
            }else{
                today_air_view.setText("");
                today_ultraviolet_view.setText("");
            }
            temp = allWeatherInfo.get(4);
            tempArray = temp.split("；");
            String[] tempArray2;
            if(tempArray.length>=3) {
                tempArray2 = tempArray[0].split("：");
                if(tempArray2.length >=3) {
                    today_tempreature_view.setText(tempArray2[2]);
                    tempArray2 = tempArray[1].split("：");
                    today_wind_view.setText(tempArray2[1]);
                }
                today_humidity_view.setText(tempArray[2]);

            }

            weatherList.clear();
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

            adapter = new WeatherAdapterUtil(getActivity(),R.layout.weather_item,weatherList);
            ListView listView = (ListView)view.findViewById(R.id.weather_list);
            listView.setAdapter(adapter);
            setListViewHeightBasedOnChildren(listView);

        }else{
            //getAllWeatherInfoByServer(city.getCityCode());
            getAllWeatherInfoByServer(city_code);
        }
    }

    public void getAllWeatherInfoByServer(String cityCode){
        String url = WEATHER_QUERY_URL+cityCode;
        new RefreshAsyncTask().execute(url,cityCode);
    }

    public int getResId(String weatherName){
        if(weatherName.equals("0.gif")){
            return R.drawable.a_0;
        }
        if(weatherName.equals("1.gif")){
            return R.drawable.a_1;
        }
        if(weatherName.equals("2.gif")){
            return R.drawable.a_2;
        }
        if(weatherName.equals("3.gif")){
            return R.drawable.a_3;
        }
        if(weatherName.equals("4.gif")){
            return R.drawable.a_4;
        }
        if(weatherName.equals("5.gif")){
            return R.drawable.a_5;
        }
        if(weatherName.equals("6.gif")){
            return R.drawable.a_6;
        }
        if(weatherName.equals("7.gif")){
            return R.drawable.a_7;
        }
        if(weatherName.equals("8.gif")){
            return R.drawable.a_8;
        }
        if(weatherName.equals("9.gif")){
            return R.drawable.a_9;
        }
        if(weatherName.equals("10.gif")){
            return R.drawable.a_10;
        }
        if(weatherName.equals("11.gif")){
            return R.drawable.a_11;
        }
        if(weatherName.equals("12.gif")){
            return R.drawable.a_12;
        }
        if(weatherName.equals("13.gif")){
            return R.drawable.a_13;
        }
        if(weatherName.equals("14.gif")){
            return R.drawable.a_14;
        }
        if(weatherName.equals("15.gif")){
            return R.drawable.a_15;
        }
        if(weatherName.equals("16.gif")){
            return R.drawable.a_16;
        }
        if(weatherName.equals("17.gif")){
            return R.drawable.a_17;
        }
        if(weatherName.equals("18.gif")){
            return R.drawable.a_18;
        }
        if(weatherName.equals("19.gif")){
            return R.drawable.a_19;
        }
        if(weatherName.equals("20.gif")){
            return R.drawable.a_20;
        }
        if(weatherName.equals("21.gif")){
            return R.drawable.a_21;
        }
        if(weatherName.equals("22.gif")){
            return R.drawable.a_22;
        }
        if(weatherName.equals("23.gif")){
            return R.drawable.a_23;
        }
        if(weatherName.equals("24.gif")){
            return R.drawable.a_24;
        }
        if(weatherName.equals("25.gif")){
            return R.drawable.a_25;
        }
        if(weatherName.equals("26.gif")){
            return R.drawable.a_26;
        }
        if(weatherName.equals("27.gif")){
            return R.drawable.a_27;
        }
        if(weatherName.equals("28.gif")){
            return R.drawable.a_28;
        }
        if(weatherName.equals("29.gif")){
            return R.drawable.a_29;
        }
        if(weatherName.equals("30.gif")){
            return R.drawable.a_30;
        }
        if(weatherName.equals("31.gif")){
            return R.drawable.a_31;
        }
        return R.drawable.a_nothing;
    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        //ListAdapter listAdapter = listView.getAdapter();
        if (adapter == null) {
            return;
        }

        int totalHeight = 0;
        // listAdapter.getCount()返回数据项的数目
        int len = adapter.getCount();
        for (int i = 0; i < len; i++) {
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
}