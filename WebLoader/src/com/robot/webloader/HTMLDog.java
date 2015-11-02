package com.robot.webloader;

import info.monitorenter.cpdetector.io.*;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by terry on 15/11/1.
 */
public class HTMLDog {
    public String tmp_file_path = "";

    private	final static int timeOut = 3000;
    private final static int retry_count = 3;

    private DefaultHttpClient client = new DefaultHttpClient();
    private static final Pattern p_charset = Pattern.compile("charset\\s?=\\s?([a-zA-Z0-9\\-]+)");
    private static final Pattern p_encoding = Pattern.compile("encoding=\"([a-zA-Z0-9\\-]+)\"");

    public HTMLDog(String a_tmp_file_path) {
        tmp_file_path = a_tmp_file_path;
    }

    public String getContent(String url) {
        int try_count = 0;
        URL target = null;
        try {
            target = new URL(url);
        } catch (MalformedURLException e) {
            return "";
        }

        HttpGet httpGet = null;
        String content = "";
        while ((content.length() == 0) && (try_count < retry_count)) {
            try {
                httpGet = new HttpGet(url);
                httpGet.addHeader("Accept-Language", "zh-cn,zh;q=0.5");
                httpGet.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
                httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
                httpGet.addHeader("Host", target.getHost());
                httpGet.addHeader("Referer", "http://" + target.getHost() + "/");

                HttpConnectionParams.setConnectionTimeout(httpGet.getParams(), timeOut);
                HttpConnectionParams.setSoTimeout(httpGet.getParams(), timeOut);
                HttpResponse httpResponse = client.execute(httpGet);

                int statusCode = httpResponse.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.SC_OK) {
                    HttpEntity entity = httpResponse.getEntity();
                    content = entityToString(entity);
                } else if ((statusCode == HttpStatus.SC_MOVED_TEMPORARILY) ||
                        (statusCode == HttpStatus.SC_MOVED_PERMANENTLY) ||
                        (statusCode == HttpStatus.SC_SEE_OTHER) ||
                        (statusCode == HttpStatus.SC_TEMPORARY_REDIRECT)) {
                    Header header = httpResponse.getLastHeader("location");
                    if(header!=null){
                        httpGet.abort();
                        return getContent(header.getValue());
                    }
                }
                if (statusCode != 200) {
                    System.out.printf("status != %d\n", statusCode);
                }
            } catch(Exception e) {
//                e.printStackTrace();
            } finally {
                if (httpGet != null) httpGet.abort();
                try_count ++;
            }
        }
        return content;
    }

    private String entityToString(HttpEntity entity){

        String charsetName = null;
        try
        {
            int k = 0;
            byte[] bytes = EntityUtils.toByteArray(entity);
            if (charsetName == null){
                Matcher m = p_charset.matcher(new String(bytes));
                if (m.find()) {
                    charsetName = m.group(1).trim();
                }
                k ++;
            }
            if (charsetName == null) {
                Matcher m = p_encoding.matcher(new String(bytes));
                if (m.find()) {
                    charsetName = m.group(1).trim();
                }
                k ++;
            }
            if (charsetName == null) {
                File tmp_file = new File(tmp_file_path);
                if (tmp_file.exists()) {
                    tmp_file.delete();
                }
                FileWriter writer = new FileWriter(tmp_file);
                writer.write(new String(bytes));
                writer.flush();
                writer.close();
                CodepageDetectorProxy detector = CodepageDetectorProxy.getInstance();
                detector.add(JChardetFacade.getInstance());
                detector.add(ASCIIDetector.getInstance());
                detector.add(UnicodeDetector.getInstance());
                detector.add(new ParsingDetector(false));
                detector.add(new ByteOrderMarkDetector());
                java.nio.charset.Charset charset = null;
                try {
                    charset = detector.detectCodepage(tmp_file.toURI().toURL());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                if (charset != null) {
                    charsetName = charset.name();
                }
                k ++;
            }
            if (charsetName == null) {
                System.out.printf("%d charset = null\n", k);
            } else {
                System.out.printf("%d charset = %s\n", k, charsetName);
            }

            if (charsetName == null) return "";
            String content = new String(bytes, charsetName);
            return content;
        }
        catch (Exception e){
            e.printStackTrace();
        } finally {
            try {
                entity.getContent().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }
}
