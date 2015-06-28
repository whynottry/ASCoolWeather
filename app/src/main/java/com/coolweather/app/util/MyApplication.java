package com.coolweather.app.util;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Star on 2015/6/24.
 */
public class MyApplication  extends Application {

    private ArrayList<City> citys_sel_list;
    private ArrayList<String> citysName_sel_list;

    @Override
    public void onCreate() {
        super.onCreate();
        citys_sel_list = new ArrayList<City>();
        citysName_sel_list = new ArrayList<String>();
    }

    public void addCity(City city_sel){
        citys_sel_list.add(city_sel);
    }

    public void delCity(City city){
        for(int i = 0; i < citys_sel_list.size(); i++){
            if(citys_sel_list.get(i).getCityCode().equals(city.getCityCode())){
                citys_sel_list.remove(i);
                break;
            }
        }
    }

    public void delCity(String cityName){
        for(int i = 0; i < citys_sel_list.size(); i++){
            if(cityName.equals(citys_sel_list.get(i).getCityName())){
                citys_sel_list.remove(i);
                break;
            }
        }
    }

    public int findCityIndex(City city){
        String cityCode = city.getCityCode();
        int index = -1;
        for(int i = 0; i < citys_sel_list.size(); i++){
            if(citys_sel_list.get(i).getCityCode().equals(cityCode)){
                index = i;
                break;
            }
        }
        return index;
    }

    public void getCityArray(){
        //从文件中读城市
        //MODE_PRIVATE,只有当前应用程序可操作
        //如果文件中没有，则创建该文件。
        SharedPreferences pref = getSharedPreferences("citys_data",MODE_PRIVATE);
        citys_sel_list.clear();
        String cityTemp;
        String[] tempArray;
        int size = pref.getInt("Status_size", 0);
//        Log.i("Thread", "getCityArray:size = " + size);
        for(int i=0;i<size;i++) {
            cityTemp = pref.getString("Status_" + i, null);
            tempArray = cityTemp.split("_");
            City city = new City();
            city.setCityCode(tempArray[0]);
            city.setCityName(tempArray[1]);
            citys_sel_list.add(city);
        }
    }

    public boolean saveCityArray() {
        SharedPreferences.Editor editor = getSharedPreferences("citys_data",MODE_PRIVATE).edit();

        editor.clear();
        editor.putInt("Status_size",citys_sel_list.size()); /*sKey is an array*/

//        Log.i("Thread", "saveCityArray:" + citys_sel_list.size());
        for(int i=0;i<citys_sel_list.size();i++) {
            //editor.remove("Status_" + i);
            editor.putString("Status_" + i, citys_sel_list.get(i).getCityCode()
                    +"_"+citys_sel_list.get(i).getCityName());
        }

        return editor.commit();
    }

    //从本地文件中读取选中的城市信息，包括城市名及城市ID
    public ArrayList<City> getCitys_sel() {
        return citys_sel_list;
    }

    public ArrayList<String> getCitysName_sel() {

        citysName_sel_list.clear();
        for(int i = 0; i < citys_sel_list.size(); i++){
            citysName_sel_list.add(citys_sel_list.get(i).getCityName());
        }

        return citysName_sel_list;
    }

    public void setCitys_sel(ArrayList<City> citys_sel) {
        this.citys_sel_list = citys_sel;
    }
}
