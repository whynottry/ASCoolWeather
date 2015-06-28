package com.coolweather.app.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Star on 2015/4/9.
 */
public class WeatherUtil {
    private static String SERVICES_HOST = "www.webxml.com.cn";
    private static String WEATHER_SERVICE_URL =
            "http://webservice.webxml.com.cn/WebServices/WeatherWS.asmx/";
    private static String PROVINCE_CODE_URL = WEATHER_SERVICE_URL
            + "getRegionProvince";
    private static String CITY_CODE_URL = WEATHER_SERVICE_URL
            + "getSupportCityString?theRegionCode=";

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

    public static void getCityInfoById(final String addressURL,
                                final CoolWeatherDB coolWeatherDB,
                                final HttpCallbackListener listener)
    {
        new Thread(new Runnable() {
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


