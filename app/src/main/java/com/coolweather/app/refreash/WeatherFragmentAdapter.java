package com.coolweather.app.refreash;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.coolweather.app.model.City;

import java.util.List;

/**
 * Created by Star on 2015/5/11.
 */
public class WeatherFragmentAdapter  extends FragmentStatePagerAdapter {
    private List<City> m_city_list;

    public int getCityNum() {
        return cityNum;
    }

    public void setCityNum(int cityNum) {
        this.cityNum = cityNum;
    }

    private int cityNum;

    public WeatherFragmentAdapter(FragmentManager fm, List<City> citys) {
        super(fm);
        m_city_list = citys;
        cityNum = citys.size();
    }

    @Override
    public Fragment getItem(int position) {

        return WeatherFragment.newInstance(position, m_city_list.get(position)) ;
    }

    @Override
    public int getCount() {
         return cityNum;
    }
}
