package com.coolweather.app.refreash;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.coolweather.app.R;
import com.coolweather.app.view.BusinessManager;
import com.coolweather.app.view.City;
import com.coolweather.app.view.ICallback;
import com.coolweather.app.view.Weather;
import com.coolweather.app.view.WeatherInfoModule;
import com.coolweather.app.util.WeatherAdapterUtil;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
* Created by Star on 2015/5/10.
*/
public class MyWeatherFragment extends Fragment {

    private static String WEATHER_SERVICE_URL =
            "http://webservice.webxml.com.cn/WebServices/WeatherWS.asmx/";
    private static String WEATHER_QUERY_URL = WEATHER_SERVICE_URL
            + "getWeather?theUserID=&theCityCode=";

    int pageNum;
    private City show_city;
    private String city_code;
    private View view;
    private RefreshableView refreshableView;
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
    private TextView cityNameTitle;
    private WeatherAdapterUtil adapter;
    List<String> allWeatherInfo = new ArrayList<String>();
//    private int pageId;

    WeatherInfoModule mCurrentWIM = WeatherInfoModule.getInstance();
    static MyWeatherFragment newInstance(int num, City city) {
        MyWeatherFragment f = new MyWeatherFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        args.putString("cityCode",city.getCityCode());
        f.setArguments(args);
//        f.pageId = num;
//        Log.i("Thread","newInstance:num = "+num+",====cityCode = "+ city.getCityCode());
       // city_code = city.getCityCode();

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pageNum = getArguments() != null ? getArguments().getInt("num") : 1;
        city_code = getArguments() != null ? getArguments().getString("cityCode") : "";
//        Log.i("Thread","OnCreate:PageNum:"+pageNum);
    }

    public void setFragmentContext() throws InterruptedException {

        mCurrentWIM.getLock().lock();
        try {
            if (!mCurrentWIM.getFlag()) {
//                Log.i("Thread", "output---wait");
                mCurrentWIM.getReadCond().await();
            }
            Log.i("Thread", "output");
            today_date_view.setText(mCurrentWIM.m_todayDataView);
            today_sunny_view.setText(mCurrentWIM.m_todaySunnyView);
            today_image_view.setImageResource(mCurrentWIM.m_todayImageId);
            today_air_view.setText(mCurrentWIM.m_todayAirView);
            today_ultraviolet_view.setText(mCurrentWIM.m_todayUltravioletView);
            today_tempreature_view.setText(mCurrentWIM.m_todayTempreatureView);
            today_wind_view.setText(mCurrentWIM.m_todayWindView);
            today_humidity_view.setText(mCurrentWIM.m_todayHumidityView);

            adapter = new WeatherAdapterUtil(getActivity(), R.layout.weather_item, mCurrentWIM.weatherList);

            ListView listView = (ListView) view.findViewById(R.id.weather_list);
            listView.setAdapter(adapter);
            setListViewHeightBasedOnChildren(listView);

            mCurrentWIM.setFlag(false);
            mCurrentWIM.getWriteCond().signalAll();
        }finally {
            mCurrentWIM.getLock().unlock();
        }

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.refreshlayout,container,false);
//        TextView temp = (TextView) view.findViewById(R.id.home_btn);
        initRefreshPageView();
        mCurrentWIM = WeatherInfoModule.getInstance();
//        if(mCurrentWIM.getFlag())
//        {
//            try {
//                setFragmentContext();
//            }  catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//        else
//        {
            getAllWeatherInfoByServer(city_code);
//        }

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
//                        Log.i("Thread","正在刷新...");
//                        Log.i("Thread","Refreash:pageNum = "+pageNum+"");
//                        Log.i("Thread","Refreash:city_code = "+city_code+"");
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

   
    public static final String SP_FILE_NAME = "app_data_weahter" + "_info";
    public void saveSelCityInfo(String cityCode,WeatherInfoModule wim){
        SharedPreferences.Editor editor;
        editor = (getActivity()).getSharedPreferences(SP_FILE_NAME,(getActivity()).MODE_PRIVATE).edit();
        
        editor.putString(cityCode, wim.getJSONString());
        editor.commit();
    }

    public WeatherInfoModule readAllWeatherInfoFromFileCache(String cityCode){
        SharedPreferences pref = (getActivity()).getSharedPreferences(SP_FILE_NAME,(getActivity()).MODE_PRIVATE);
        String weatherInfoString = pref.getString(cityCode,"");
        WeatherInfoModule wim = new Gson().fromJson(weatherInfoString, WeatherInfoModule.class);
        
        return wim;
    }

    public void getAllWeatherInfoByServer(final String cityCode){
        String url = WEATHER_QUERY_URL+cityCode;
//        Log.i("Thread",cityCode+"");
        new BusinessManager(getActivity()).getWeather(url, cityCode, new ICallback<WeatherInfoModule>()
        {
            
            @Override
            public void onSuccess(WeatherInfoModule result)
            {
                //saveSelCityInfo(cityCode, result);
                mCurrentWIM = result;
                //初始化界面将取到的数据设置到界面上
                try {
                    setFragmentContext();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            
            @Override
            public void onFail(String errorMsg)
            {
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