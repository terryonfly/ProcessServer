package com.robot.webloader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
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
        try {
            while (is_run) {
                URLModel target_url = urlQueue.get_one_url();
                if (target_url != null && target_url.url.length() > 0) {
//                    System.out.printf("URL : %s\n", target_url.url);
                    URL httpurl = null;
                    try {
                        httpurl = new URL(target_url.url);
                    } catch (MalformedURLException e) {
                        System.err.printf("target_url is not a good url!\n");
                        continue;
                    }
                    // Save it
                    String file_name = target_url.url_id + ".html";
                    File file = new File("data/" + file_name);
                    if (file.exists()) {
                        continue;
                    }
                    try {
                        FileUtils.copyURLToFile(httpurl, file);
                    } catch (IOException e) {
                        System.err.printf("url load err\n");
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
