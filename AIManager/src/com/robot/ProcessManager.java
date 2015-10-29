package com.robot;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by terry on 15/10/29.
 */
public class ProcessManager {

    ArrayList<ProcessWatchDog> processWatchDogs;

    public ProcessManager() {
        processWatchDogs = new ArrayList<ProcessWatchDog>();
    }

    private static final ProcessManager processManager = new ProcessManager();

    //静态工厂方法
    public static ProcessManager getInstance() {
        return processManager;
    }

    public void add_process(String a_run_path) {
        if (processWatchDogs.size() >= 300) return;
        ProcessWatchDog processWatchDog = new ProcessWatchDog(a_run_path, "./run.sh");
        synchronized (processWatchDogs) {
            processWatchDogs.add(processWatchDog);
        }
    }

    public void remoce_all_process() {
        synchronized (processWatchDogs) {
            for (int i = 0; i < processWatchDogs.size(); i ++) {
                processWatchDogs.get(i).stop();
                processWatchDogs.remove(i);
                i--;
            }
        }
    }

    public void set_process_count(String a_run_path, int a_process_count) {
        int process_count = get_process_count(a_run_path);
        if (process_count > a_process_count) {
            int remove_count = process_count - a_process_count;
            synchronized (processWatchDogs) {
                for (int i = 0; i < processWatchDogs.size(); i++) {
                    if (processWatchDogs.get(i).if_run_path_match(a_run_path)) {
                        processWatchDogs.get(i).stop();
                        processWatchDogs.remove(i);
                        i--;
                        remove_count--;
                        if (remove_count <= 0) break;
                    }
                }
            }
        } else if (process_count < a_process_count) {
            int add_count = a_process_count - process_count;
            for (int i = 0; i < add_count; i ++) {
                add_process(a_run_path);
            }
        }
    }

    public int get_process_count(String a_run_path) {
        int process_count = 0;
        synchronized (processWatchDogs) {
            for (int i = 0; i < processWatchDogs.size(); i ++) {
                if (processWatchDogs.get(i).if_run_path_match(a_run_path))
                    process_count ++;
            }
        }
        return process_count;
    }
}
