package com.coolweather.app.model;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class BusinessManager
{
    //����
    private Context mContext;
    
    public BusinessManager(Context context)
    {
        this.mContext = context;
    }
    
    public void getWeather(String url, String cityCode, ICallback<WeatherInfoModule> callback)
    {
        new RefreshAsyncTask(callback).execute(url, cityCode);
    }
    
    class RefreshAsyncTask extends AsyncTask<String, Integer, WeatherInfoModule>
    {
        public ICallback<WeatherInfoModule> mCallback;
        
        public boolean isSucceed = false;
        
        public RefreshAsyncTask(ICallback<WeatherInfoModule> callback)
        {
            this.mCallback = callback;
        }
        
        protected WeatherInfoModule doInBackground(String... params)
        {
            HttpURLConnection connection = null;
            ArrayList<String> allWeatherInfo = new ArrayList<String>();
            WeatherInfoModule wim = WeatherInfoModule.getInstance();

            try
            {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(8000);
                connection.setReadTimeout(8000);
                InputStream in = connection.getInputStream();
                Document document = null;
                
                // ���󹤳���
                DocumentBuilderFactory documentBF = DocumentBuilderFactory.newInstance();
                documentBF.setNamespaceAware(true);
                // DOM����������
                DocumentBuilder documentB = null;
                try
                {
                    documentB = documentBF.newDocumentBuilder();
                }
                catch (ParserConfigurationException e)
                {
                    e.printStackTrace();
                }
                try
                {
                    document = documentB.parse(in);
                    NodeList nodeList = document.getElementsByTagName("string");
                    int len = nodeList.getLength();
                    if (len <= 1)
                    {
                        Log.d("WXDebug", "++++++++++++++++++++");
                        Log.d("WXDebug", "24Сʱ��������������");
                    }
                    // allWeatherInfo.clear();
                    for (int i = 0; i < len; i++)
                    {
                        Node n = nodeList.item(i);
                        String weather = "";
                        if (n.hasChildNodes())
                        {
                            weather = n.getFirstChild().getNodeValue();
                        }
                        allWeatherInfo.add(weather);
                    }
                    wim.buildWeatherInfo(allWeatherInfo);
                    isSucceed = true;
                }
                catch (Exception e)
                {
                    Log.e("Thread","connection.getInputStream error");
                    e.printStackTrace();
                    isSucceed = false;
                }
                
            }
            catch (IOException e)
            {
                e.printStackTrace();
                isSucceed = false;
            }
            finally
            {
                if (connection != null)
                {
                    connection.disconnect();
                }
            }
            return wim;
        }
        
        protected void onProgressUpdate(Integer... progress)
        {
            // setProgressPercent(progress[0]);
        }
        
        protected void onPostExecute(WeatherInfoModule wim)
        {

            if (mCallback != null)
            {
                if (isSucceed)
                {
                    mCallback.onSuccess(wim);
                }
                else
                {
                    mCallback.onFail("some error message");
                }
            }
            
        }
    }
    
}