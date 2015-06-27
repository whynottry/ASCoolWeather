package com.coolweather.app.view;

public interface ICallback<T>
{
    void onSuccess(T result);
    
    void onFail(String errorMsg);
}
