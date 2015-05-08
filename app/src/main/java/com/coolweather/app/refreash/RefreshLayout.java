package com.coolweather.app.refreash;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coolweather.app.R;

/**
 * Created by Star on 2015/5/4.
 */
public class RefreshLayout extends LinearLayout {
    private RefreshableView refreshableView;

    public RefreshLayout(Context context) {
        super(context);
    }

    public RefreshLayout(final Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.refreshlayout, this);



    }

    public RefreshLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

}
