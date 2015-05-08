package com.coolweather.app.refreash;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ScrollView;

import com.coolweather.app.R;

/**
 * Created by Star on 2015/4/30.
 */
public class RefreshLayoutDetail extends ScrollView {

    public RefreshLayoutDetail(Context context) {
        super(context);
    }

    public RefreshLayoutDetail(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.refresh_layout_detail,this);
    }

    public RefreshLayoutDetail(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

}
