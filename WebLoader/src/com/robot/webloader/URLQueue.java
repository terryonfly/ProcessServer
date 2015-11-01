package com.robot.webloader;

import com.robot.database.Connector;
import java.util.*;

/**
 * Created by terry on 15/10/20.
 */
public class URLQueue implements Runnable {

    Thread thread;
    String thread_name;
    boolean is_run;
    Connector db;
    public ArrayList<URLModel> target_urls;
    int done_count = 0;

    public URLQueue(String a_thread_name) {
        thread_name = a_thread_name;
        is_run = false;
        db = new Connector();
        db.connect();
        target_urls = new ArrayList<URLModel>();
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
        while (is_run) {
            check_target_urls();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean check_target_urls() {
        boolean need_fill = false;
        if (target_urls.size() < 500) {
            need_fill = true;
        }
        if (need_fill) {
            ArrayList<URLModel> fill_urls = null;
            synchronized (db) {
                fill_urls = db.get_urls(1000);
            }
            synchronized (target_urls) {
                for (int i = 0; i < fill_urls.size(); i ++) {
                    target_urls.add(fill_urls.get(i));
                }
            }
        }
        return need_fill;
    }

    public URLModel get_one_url() {
        URLModel url = null;
        synchronized (target_urls) {
            if (target_urls.size() > 0) {
                url = target_urls.get(0);
                target_urls.remove(0);
                done_count ++;
            }
        }
        return url;
    }

    public void feedback_loading_result(URLModel a_url, boolean a_finished) {
        if (a_finished) {
            synchronized (db) {
                db.set_url_getted(a_url.url_id, 2);
            }
        }
    }

    @Override
    protected void finalize() throws Throwable {
        stop();
        super.finalize();
    }
}
