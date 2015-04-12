package com.coolweather.app.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.coolweather.app.R;
import com.coolweather.app.model.Weather;

import java.util.List;

/**
 * Created by Star on 2015/4/12.
 */
public class WeatherAdapterUtil extends ArrayAdapter<Weather>{
    private int resourceId;

    public WeatherAdapterUtil(Context context, int textViewResourceId,
                              List<Weather> objects){
        super(context,textViewResourceId,objects);
        resourceId = textViewResourceId;
    }

    //此方法在每个子项被滚动到屏幕内的时候会被调用
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Weather weather = getItem(position);  //获得当前项的weather实例
        View view;
        ViewHolder viewHolder;

        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId,null);
            viewHolder = new ViewHolder();
            viewHolder.weather_date = (TextView)view.findViewById(R.id.item_date);
            viewHolder.weather_sunny = (TextView)view.findViewById(R.id.item_sunny);
            viewHolder.weather_temperature = (TextView)view.findViewById(R.id.item_temperature);
            viewHolder.weather_wind = (TextView)view.findViewById(R.id.item_wind);

            view.setTag(viewHolder);
        }else {
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }
        viewHolder.weather_date.setText(weather.getDate());
        viewHolder.weather_sunny.setText(weather.getSunny());
        viewHolder.weather_temperature.setText(weather.getTemperature());
        viewHolder.weather_wind.setText(weather.getWind());

        return view;
    }

    class ViewHolder{
        TextView weather_date;
        TextView weather_sunny;
        TextView weather_temperature;
        TextView weather_wind;
    }
}

