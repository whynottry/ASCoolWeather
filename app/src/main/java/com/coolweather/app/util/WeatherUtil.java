package com.coolweather.app.util;

import android.app.Application;
import android.content.Context;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.coolweather.app.model.CoolWeatherDB;
import com.coolweather.app.model.Province;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by Star on 2015/4/9.
 */
public class WeatherUtil {
    private static String SERVICES_HOST = "www.webxml.com.cn";
    private static String WEATHER_SERVICE_URL =
            "http://webservice.webxml.com.cn/WebServices/WeatherWS.asmx/";
    private static String PROVINCE_CODE_URL = WEATHER_SERVICE_URL
            + "getRegionProvince";
    private static String WEATHER_QUERY_URL = WEATHER_SERVICE_URL
            + "getWeather?theUserID=&theCityCode=";


    /**
     * 解析URL，获得输入对象
     */
    public static void getAllProvinceInfo(final String addressURL,
                                          final CoolWeatherDB coolweatherDB,
                                          final HttpCallbackListener listener){
        new Thread(new Runnable(){
            @Override
            public void run() {
                HttpURLConnection connection = null;

                try {
                    URL url = new URL(addressURL);
                    connection = (HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream inputStream = connection.getInputStream();

                    if(inputStream != null){
                        listener.onFinish(inputStream);
                    }


                    Document document = null;

                    //抽象工厂类
                    DocumentBuilderFactory documentBF = DocumentBuilderFactory.newInstance();
                    documentBF.setNamespaceAware(true);
                    //DOM解析器对象
                    DocumentBuilder documentB = null;
                    try {
                        documentB = documentBF.newDocumentBuilder();
                    } catch (ParserConfigurationException e) {
                        e.printStackTrace();
                    }
                    try {
                        document = documentB.parse(inputStream);
                    } catch (SAXException e) {
                        e.printStackTrace();
                    }
                    NodeList nodeList = document.getElementsByTagName("string");
                    int len = nodeList.getLength();
                    for(int i = 0; i < len; i++){
                        Node n = nodeList.item(i);
                        String result = n.getFirstChild().getNodeValue();
                        String[] address = result.split(",");
                        Province province = new Province();
                        province.setProvincename(address[0]);
                        province.setProvincecode(address[1]);

                        coolweatherDB.saveProvince(province);


                    }

                    Log.d("test","save db done");
                } catch (IOException e) {
                    //回调onError()方法
                    listener.onError(e);
                    e.printStackTrace();
                } finally {
                    if(connection != null){
                        connection.disconnect();
                    }
                }

            }
        }).start();
    }

    public interface HttpCallbackListener{
        void onFinish(InputStream inputStream);
        void onError(Exception e);
    }

    /**
    * 获得所有省信息，并存到数据库中
    */
//    public static void getAllProvinceInfo(CoolWeatherDB coolweatherDB){
//        //List<Province> provinces = new ArrayList<Province>();
//        Document document;
//
//        //抽象工厂类
//        DocumentBuilderFactory documentBF = DocumentBuilderFactory.newInstance();
//        documentBF.setNamespaceAware(true);
//
//        try {
//            //DOM解析器对象
//            DocumentBuilder documentB = documentBF.newDocumentBuilder();
//            InputStream inputStream = getSoapInputStream(PROVINCE_CODE_URL);
//            document = documentB.parse(inputStream);
//            NodeList nodeList = document.getElementsByTagName("string");
//            int len = nodeList.getLength();
//            for(int i = 0; i < len; i++){
//                Node n = nodeList.item(i);
//                String result = n.getFirstChild().getNodeValue();
//                String[] address = result.split(",");
//                Province province = new Province();
//                province.setProvincename(address[0]);
//                province.setProvincecode(address[1]);
//
//                coolweatherDB.saveProvince(province);
//            }
//        } catch (SAXException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (ParserConfigurationException e) {
//            e.printStackTrace();
//        }
//
//        //return provinces;
//    }
}


