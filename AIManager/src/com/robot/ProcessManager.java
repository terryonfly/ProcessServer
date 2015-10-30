package com.robot;

import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by terry on 15/10/29.
 */
public class ProcessManager implements Runnable {

    private boolean is_running = true;

    public class ProcessWatchDogList {

        public ArrayList<ProcessWatchDog> processWatchDogs = new ArrayList<ProcessWatchDog>();

        public int plan_process_count = 0;

        public int check_need_sync_count() {
            return (plan_process_count - processWatchDogs.size());
        }
    }

    HashMap<String, ProcessWatchDogList> processWatchDogs;

    private static final ProcessManager processManager = new ProcessManager();

    //静态工厂方法
    public static ProcessManager getInstance() {
        return processManager;
    }

    public ProcessManager() {
        processWatchDogs = new HashMap<String, ProcessWatchDogList>();
        new Thread(this).start();
    }

    public void stop() {
        is_running = false;
    }

    @Override
    public void run() {
        while (is_running) {
            check_process_plans();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void check_process_plans() {
        synchronized (processWatchDogs) {
            int add_count_each_time = 1;
            int del_count_each_time = 1;
            Iterator iter = processWatchDogs.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                String run_path = (String) entry.getKey();
                ProcessWatchDogList processWatchDogList = (ProcessWatchDogList) entry.getValue();
                int need_sync_count = processWatchDogList.check_need_sync_count();
                while (add_count_each_time > 0 && need_sync_count > 0) {
                    add_process(run_path);
                    add_count_each_time --;
                }
                while (del_count_each_time > 0 && need_sync_count < 0) {
                    del_process(run_path);
                    del_count_each_time --;
                }
                if (add_count_each_time == 0 && del_count_each_time == 0)
                    break;
            }
        }
    }

    public void add_process(String a_run_path) {
        ProcessWatchDog processWatchDog = new ProcessWatchDog(a_run_path, "./run.sh");
        synchronized (processWatchDogs) {
            ProcessWatchDogList processWatchDogList = processWatchDogs.get(a_run_path);
            processWatchDogList.processWatchDogs.add(processWatchDog);
        }
        System.out.printf("current processes count : %d\n", get_all_processes_count());
    }

    public void del_process(String a_run_path) {
        synchronized (processWatchDogs) {
            ProcessWatchDogList processWatchDogList = processWatchDogs.get(a_run_path);
            if (processWatchDogList.processWatchDogs.size() > 0) {
                ProcessWatchDog processWatchDog_to_del = processWatchDogList.processWatchDogs.get(0);
                processWatchDog_to_del.stop();
                processWatchDogList.processWatchDogs.remove(0);
            }
        }
        System.out.printf("current processes count : %d\n", get_all_processes_count());
    }

    public void remoce_all_process() {
        synchronized (processWatchDogs) {
            Iterator iter = processWatchDogs.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                ProcessWatchDogList processWatchDogList = (ProcessWatchDogList) entry.getValue();
                processWatchDogList.plan_process_count = 0;
            }
        }
    }

    public void set_process_count(String a_run_path, int a_process_count) {
        synchronized (processWatchDogs) {
            ProcessWatchDogList processWatchDogList = processWatchDogs.get(a_run_path);
            processWatchDogList.plan_process_count = a_process_count;
        }
    }

    public int get_all_processes_count() {
        int all_processes_count = 0;
        synchronized (processWatchDogs) {
            Iterator iter = processWatchDogs.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                ProcessWatchDogList processWatchDogList = (ProcessWatchDogList)entry.getValue();
                all_processes_count = processWatchDogList.processWatchDogs.size();
            }
        }
        return all_processes_count;
    }

    @Override
    protected void finalize() throws Throwable {
        stop();
        super.finalize();
    }
}
