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

    public HashMap<String, ProcessWatchDogList> processWatchDogs;

    public ArrayList<ProcessWatchDog> die_processes;

    public int unormal_die_processes_count = 0;

    private static final ProcessManager processManager = new ProcessManager();

    //静态工厂方法
    public static ProcessManager getInstance() {
        return processManager;
    }

    public ProcessManager() {
        processWatchDogs = new HashMap<String, ProcessWatchDogList>();
        die_processes = new ArrayList<ProcessWatchDog>();
        new Thread(this).start();
    }

    public void stop() {
        is_running = false;
    }

    @Override
    public void run() {
        while (is_running) {
            check_process_plans();
            check_die_processes();
            try {
                Thread.sleep(1000);
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

    public void check_die_processes() {
        synchronized (die_processes) {
            for (int i = 0; i < die_processes.size(); i ++) {
                if (die_processes.get(i).has_exit_process) {
                    die_processes.remove(i);
                    break;
                }
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
        ProcessWatchDog processWatchDog_to_del = null;
        synchronized (processWatchDogs) {
            ProcessWatchDogList processWatchDogList = processWatchDogs.get(a_run_path);
            if (processWatchDogList.processWatchDogs.size() > 0) {
                processWatchDog_to_del = processWatchDogList.processWatchDogs.get(0);
                processWatchDogList.processWatchDogs.remove(0);
                processWatchDog_to_del.destroy_process();
                int retry_times = 3;
                boolean is_normal_del = false;
                for (int i = 0; i < retry_times; i ++) {
                    if (processWatchDog_to_del.confirm_exit_process) {
                        is_normal_del = true;
                        break;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (!is_normal_del) {
                    unormal_die_processes_count ++;
                    processWatchDog_to_del.force_destroy_process();
                    processWatchDog_to_del = null;
                }
            }
        }
        synchronized (die_processes) {
            if (processWatchDog_to_del != null) {
                die_processes.add(processWatchDog_to_del);
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
            if (processWatchDogs.containsKey(a_run_path)) {
                ProcessWatchDogList processWatchDogList = processWatchDogs.get(a_run_path);
                processWatchDogList.plan_process_count = a_process_count;
            } else {
                ProcessWatchDogList processWatchDogList = new ProcessWatchDogList();
                processWatchDogList.plan_process_count = a_process_count;
                processWatchDogs.put(a_run_path, processWatchDogList);
            }
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
