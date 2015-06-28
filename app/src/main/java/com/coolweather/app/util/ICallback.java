package com.coolweather.app.util;

public interface ICallback<T>
{
    void onSuccess(T result);
    
    void onFail(String errorMsg);
}
