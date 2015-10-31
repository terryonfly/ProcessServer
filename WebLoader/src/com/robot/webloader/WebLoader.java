package com.robot.webloader;

import java.util.ArrayList;

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
        Downloader downloader = new Downloader();
        try {
            while (is_run) {
                String target_url = urlQueue.get_one_url();
                if (target_url.length() > 0) {
                    System.out.printf("URL : %s\n", target_url);
                    downloader.getHtml(target_url);
                    // TODO : Save It !
                } else {
                    System.out.println("No urls in DB");
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
