package com.robot.webloader;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import info.monitorenter.cpdetector.io.*;
import org.apache.commons.io.FileUtils;

/**
 * Created by terry on 15/10/31.
 */
public class WebLoader implements Runnable {

    Thread thread;
    String thread_name;
    boolean is_run;
    URLQueue urlQueue;

    public WebLoader(String a_thread_name, URLQueue a_urlQueue) {
        thread_name = a_thread_name;
        is_run = false;
        urlQueue = a_urlQueue;
    }

    public void start() {
        if (is_run) return;
        thread = new Thread(this);
        is_run = true;
        thread.start();
    }

    public void stop() {
        if (!is_run) return;
        is_run = false;
        thread = null;
    }

    @Override
    public void run() {
        String name = ManagementFactory.getRuntimeMXBean().getName();
        String pid = name.split("@")[0];
        HTMLDog htmlDog = new HTMLDog("tmp/" + thread_name + "@" + pid + ".tmp.data");
        try {
            while (is_run) {
                URLModel target_url = urlQueue.get_one_url();
                if (target_url != null && target_url.url.length() > 0) {
//                    System.out.printf("URL : %s\n", target_url.url);
                    String content = htmlDog.getContent(target_url.url);
                    if (content == null || content.length() == 0) {
                        continue;
                    }
                    String file_name = target_url.url_id + ".data";
                    File file = new File("data/" + file_name);
                    if (file.exists()) {
                        continue;
                    }
                    try {
                        FileWriter writer = new FileWriter(file);
                        writer.write(content);
                        writer.flush();
                        writer.close();
                    } catch (IOException e) {
                        continue;
                    }
                } else {
//                    System.out.println("No urls in DB");
                    Thread.sleep(1000);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        stop();
        super.finalize();
    }
}
