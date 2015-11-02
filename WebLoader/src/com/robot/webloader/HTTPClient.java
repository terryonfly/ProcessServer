package com.robot.webloader;

import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;

/**
 * Created by terry on 15/11/2.
 */
public class HTTPClient {

    DefaultHttpClient client;

    private HTTPClient() {
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
        schemeRegistry.register(new Scheme("https", 443, SSLSocketFactory.getSocketFactory()));
        PoolingClientConnectionManager cm = new PoolingClientConnectionManager(schemeRegistry);
        cm.setMaxTotal(200);
        cm.setDefaultMaxPerRoute(2);
        client = new DefaultHttpClient(cm);
        HttpClientParams.setCookiePolicy(client.getParams(), CookiePolicy.IGNORE_COOKIES);
    }

    private static final HTTPClient httpClient = new HTTPClient();

    //静态工厂方法
    public static HTTPClient getInstance() {
        return httpClient;
    }
}
