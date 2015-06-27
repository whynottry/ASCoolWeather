package com.coolweather.app.view;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.coolweather.app.R;
import com.google.gson.Gson;

/**
 * 
 * ������ģ�ͣ���װ������ص�����
 * 
 * @author Star
 * @see [�����/����]
 */
public class WeatherInfoModule implements Serializable
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 8578087925576751875L;
    
    public String mCityName;
    public String mCityCode;
    public String m_todayDataView;
    public String m_todaySunnyView;
    public int m_todayImageId;
    public String m_todayAirView;
    public String m_todayUltravioletView;
    public String m_todayTempreatureView;
    public String m_todayWindView;
    public String m_todayHumidityView;
    public List<Weather> weatherList = new ArrayList<Weather>();
    boolean flag = false;

    private static final WeatherInfoModule wif = new WeatherInfoModule();
    private final Lock lock = new ReentrantLock();
    private final Condition write = lock.newCondition();
    private final Condition read = lock.newCondition();

    public Lock getLock(){
        return lock;
    }

    public Condition getWriteCond(){
        return write;
    }

    public Condition getReadCond(){
        return read;
    }

    private WeatherInfoModule(){};

    public static WeatherInfoModule getInstance(){
        return wif;
    }

    public void setFlag(boolean flag){
        this.flag = flag;
    }

    public void buildWeatherInfo(ArrayList<String> weatherInfoParams) throws InterruptedException {
        lock.lock();
        //flag为false的时候，代表内容未更新，可以写操作
        try {

            if(flag) {   //flag为true，则不能写
                //Log.i("Thread","input+++++++++++wait");
                //wif.wait();
                write.await();
            }
//            Log.i("Thread","++++input");
            wif.mCityName = getParam(1, weatherInfoParams);
            String temp;
            String[] tempArray;

            temp = getParam(3, weatherInfoParams);
            tempArray = temp.split(" ");

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日    HH:mm:ss     ");
            Date curDate = new Date(System.currentTimeMillis());//获取当前时间
            String str = formatter.format(curDate);
            str += wif.mCityName;
            wif.m_todayDataView = str;

            temp = getParam(7, weatherInfoParams);
            tempArray = temp.split(" ");
            if (tempArray.length >= 2) {
                wif.m_todaySunnyView = tempArray[1];
            } else {
                wif.m_todaySunnyView = "";
            }
            temp = getParam(10, weatherInfoParams);
            wif.m_todayImageId = wif.getResId(temp);
            temp = getParam(5, weatherInfoParams);
            tempArray = temp.split("；");
            if (tempArray.length >= 2) {
                wif.m_todayAirView = tempArray[0];
                wif.m_todayUltravioletView = tempArray[1];
            } else {
                wif.m_todayAirView = "";
                wif.m_todayUltravioletView = "";
            }
            temp = getParam(4, weatherInfoParams);
            tempArray = temp.split("；");
            String[] tempArray2;
            if (tempArray.length >= 3) {
                tempArray2 = tempArray[0].split("：");
                if (tempArray2.length >= 3) {
                    wif.m_todayTempreatureView = tempArray2[2];
                    tempArray2 = tempArray[1].split("：");
                    wif.m_todayWindView = tempArray2[1];
                }
                wif.m_todayHumidityView = tempArray[2];
            }

            wif.weatherList.clear();
            String key = "";
            for (int i = 0; i < 5; i++) {
                Weather weather = new Weather();
                temp = getParam(7 + 5 * i, weatherInfoParams);

                temp = getParam(8 + 5 * i, weatherInfoParams);
                weather.setTemperature(temp);
                temp = getParam(9 + 5 * i, weatherInfoParams);
                weather.setWind(temp);
                wif.weatherList.add(weather);
            }

            flag = true;
            read.signalAll();
        }finally {
            lock.unlock();
        }
    }

    public synchronized void changeFlagFalse(){
        wif.flag = false;
    }

    public boolean getFlag(){
        return flag;
    }
    
    public static String getParam(int index, ArrayList<String> weatherInfoParams)
    {
        String result = "";
        if (weatherInfoParams != null && index >= 0 && index < weatherInfoParams.size())
        {
            result = weatherInfoParams.get(index);
        }
        
        return result;
    }
    
    public String getJSONString()
    {
        return new Gson().toJson(this, WeatherInfoModule.class);
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
}
