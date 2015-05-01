package com.coolweather.app.refreash;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ScrollView;

import com.coolweather.app.R;

/**
 * Created by Star on 2015/4/30.
 */
public class WeatherRefreshView extends ScrollView {

    public WeatherRefreshView(Context context) {
        super(context);
    }

    public WeatherRefreshView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.weather_refresh_view,this);
    }

    public WeatherRefreshView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

}
