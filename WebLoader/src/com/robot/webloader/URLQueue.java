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
    Connector db_get_url;
    ArrayList<String> uncommit_urls;
    ArrayList<String> target_urls;
    String seed_url = "http://www.baidu.com";
    URLCache urlCache;

    public URLQueue(String a_thread_name) {
        thread_name = a_thread_name;
        is_run = false;
        db = new Connector();
        db.connect();
        db_get_url = new Connector();
        db_get_url.connect();
        uncommit_urls = new ArrayList<String>();
        target_urls = new ArrayList<String>();
        urlCache = new URLCache();
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
            if (uncommit_urls.size() < 1000000) check_target_urls();
            if (commit_url()) {
//                RuntimeInfo.getInstance().update_uncommit_url_count(-1);
                //System.out.println(thread_name + " : [ OK uncommit url : " + uncommit_urls.size() + " ]");
            } else {
                //RuntimeInfo.getInstance().update_uncommit_url_count(uncommit_urls.size());
                //System.out.println(thread_name + " : [ Empty uncommit url ]");
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
        //synchronized (target_urls) {
        if (target_urls.size() < 500) {
            need_fill = true;
        }
        //}
        if (need_fill) {
            ArrayList<String> fill_urls = db.get_urls(1000);
            synchronized (target_urls) {
                for (int i = 0; i < fill_urls.size(); i ++) {
                    target_urls.add(fill_urls.get(i));
                }
            }
        }
        return need_fill;
    }

    public boolean commit_url() {
        String url_to_commit = null;
        boolean res = false;
        synchronized (uncommit_urls) {
            if (uncommit_urls.size() < 100) {
                while (uncommit_urls.size() < 1000) {
                    String url = urlCache.read_out();
                    if (url == null) break;
                    if (!uncommit_urls.contains(url)) {
                        uncommit_urls.add(url);
//                        RuntimeInfo.getInstance().update_uncommit_url_count(1);
                    }
                }
            }
            if (uncommit_urls.size() > 0) {
                url_to_commit = uncommit_urls.get(0);
                uncommit_urls.remove(0);
                res = true;
            }
            if (uncommit_urls.size() > 1000000) {
                while (uncommit_urls.size() > 500000) {
                    String url = target_urls.get(0);
                    target_urls.remove(0);
                    urlCache.write_in(url);
//                    RuntimeInfo.getInstance().update_uncommit_url_count(-1);
                }
            }
        }
        if (res) {
            db.add_url(url_to_commit);
        }
        return res;
    }

    public void add_urls(ArrayList<String> a_urls) {
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
//        RuntimeInfo.getInstance().update_uncommit_url_count(added_url_count);
    }

    public String get_one_url() {
        String url = "";
        synchronized (target_urls) {
            if (target_urls.size() > 0) {
                url = target_urls.get(0);
                target_urls.remove(0);
//                RuntimeInfo.getInstance().update_running_page_count(1);
            }
        }
        if (url.length() == 0) {
            url = seed_url;
            seed_url = "";
        }
        return url;
    }
}
