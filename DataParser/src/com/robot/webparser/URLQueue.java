package com.robot.webparser;

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
    ArrayList<String> uncommit_urls;
    public ArrayList<URLModel> unparsed_urls;

    public URLQueue(String a_thread_name) {
        thread_name = a_thread_name;
        is_run = false;
        db = new Connector();
        db.connect();
        uncommit_urls = new ArrayList<String>();
        unparsed_urls = new ArrayList<URLModel>();
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
            if (commit_url()) {

            } else {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean check_target_urls() {
        boolean need_fill = false;
        if (unparsed_urls.size() < 500) {
            need_fill = true;
        }
        if (need_fill) {
            ArrayList<URLModel> fill_urls = null;
            synchronized (db) {
                fill_urls = db.get_unparsed_urls(1000);
            }
            synchronized (unparsed_urls) {
                for (int i = 0; i < fill_urls.size(); i ++) {
                    unparsed_urls.add(fill_urls.get(i));
                }
            }
        }
        return need_fill;
    }

    public URLModel get_one_unparsed_url() {
        URLModel url = null;
        synchronized (unparsed_urls) {
            if (unparsed_urls.size() > 0) {
                url = unparsed_urls.get(0);
                unparsed_urls.remove(0);
            }
        }
        return url;
    }

    public void feedback_parsing_result(URLModel a_url, boolean a_finished) {
        synchronized (db) {
            db.set_url_status(a_url.url_id, (a_finished) ? 6 : 5);
        }
    }

    public boolean commit_url() {
        String url_to_commit = null;
        boolean res = false;
        synchronized (uncommit_urls) {
            if (uncommit_urls.size() > 0) {
                url_to_commit = uncommit_urls.get(0);
                uncommit_urls.remove(0);
                res = true;
            }
        }
        if (res) {
            db.add_url(url_to_commit);
        }
        return res;
    }

    public void add_urls(ArrayList<String> a_urls) {
        System.out.printf("add %d new urls, uncommit %d urls\n", a_urls.size(), uncommit_urls.size());
        if (a_urls.size() == 0) return;
        int added_url_count = 0;
        synchronized (uncommit_urls) {
            for (int i = 0; i < a_urls.size(); i ++) {
                if (!uncommit_urls.contains(a_urls.get(i))) {
                    uncommit_urls.add(a_urls.get(i));
                    added_url_count ++;
                }
            }
        }
    }
}
