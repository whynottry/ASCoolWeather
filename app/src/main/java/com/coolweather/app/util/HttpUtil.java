package com.coolweather.app.util;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by Star on 2015/4/9.
 */
public class HttpUtil {

    public static void sendHttpRequest(final String address,
                                       final HttpCallbackListener listener){
        new Thread(new Runnable(){
            @Override
            public void run() {
                HttpURLConnection connection = null;

                try {
                    URL url = new URL(address);
                    connection = (HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream in = connection.getInputStream();
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
                        try {
                            document = documentB.parse(in);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } catch (SAXException e) {
                        e.printStackTrace();
                    }

                    if(listener != null){
                        //回调onFinish()方法
                        listener.onFinish(document);
                    }
                } catch (IOException e) {
                    //回调onError()方法
                    //listener.onError(e);
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
        void onFinish(Document document);
        void onError(Exception e);
    }
}
