package com.robot.webloader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
                URLModel target_url = urlQueue.get_one_url();
                if (target_url != null && target_url.url.length() > 0) {
//                    System.out.printf("URL : %s\n", target_url.url);
                    String data = downloader.getHtml(target_url.url);
                    // Save it
                    String file_name = target_url.url_id + ".html";
                    save_web_data(data, "data/", file_name);
                } else {
                    System.out.println("No urls in DB");
                    Thread.sleep(1000);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void save_web_data(String a_data, String a_path, String a_name) {
        String save_file_name = a_path + a_name;
        File file = new File(save_file_name);
        if (file.exists()) {
//            file.delete();
            return;
        }
        FileWriter writer = null;
        try {
            file.createNewFile();
            writer = new FileWriter(save_file_name, true);
            writer.write(a_data);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void finalize() throws Throwable {
        stop();
        super.finalize();
    }
}
