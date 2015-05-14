package com.coolweather.app.model;

public interface ICallback<T>
{
    void onSuccess(T result);
    
    void onFail(String errorMsg);
}
