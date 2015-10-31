package com.robot;

import java.io.*;

/**
 * Created by terry on 15/10/29.
 */
public class ProcessWatchDog implements Runnable {

    private boolean is_running = true;

    private String run_path;

    private String run_cmd;

    public Process process = null;

    public boolean confirm_exit_process = false;

    public boolean has_exit_process = false;

    Thread thread = null;

    BufferedWriter writer = null;

    BufferedReader reader = null;

    public ProcessWatchDog(String a_run_path, String a_run_cmd) {
        run_path = a_run_path;
        run_cmd = a_run_cmd;
        thread = new Thread(this);
        thread.start();
    }

    public void stop() {
        is_running = false;
    }

    @Override
    public void run() {
        try {
            String s;
            confirm_exit_process = false;
            has_exit_process = false;
            File f = new File(run_path);
            process = Runtime.getRuntime().exec(run_cmd, null, f);
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
            while(is_running) {
                if ((s = reader.readLine()) != null) {
                    if (!confirm_exit_process) {
                        if (s.equals("OK, I will kill myself!")) {
//                            System.out.printf("Process start run to end\n");
                            confirm_exit_process = true;
                        }
                    } else {
                        if (s.equals("Now, I had killed myself!")) {
//                            System.out.printf("Thread run to end by normal\n");
                            reader.close();
                            is_running = false;
                        }
                    }
                    if (!confirm_exit_process) {
                        System.out.printf("s = %s\n", s);
                    }
                }
            }
            reader.close();
            writer.close();
            try {
                process.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            process.destroy();
            process = null;
            has_exit_process = true;
//            System.out.printf("Delete thread\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void destroy_process() {
        if (writer != null) {
            try {
                writer.write("Please kill yourself, thank you!\n");
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void force_destroy_process() {
        System.err.printf("Now force destory process because the process did not confirm me he is going to kill himself.\n");
        System.err.printf("The process path is %s\n", run_path);
        if (process != null) {
            process.destroy();
            process = null;
            is_running = false;
        }
        thread.interrupt();
    }

    public String get_run_path() {
        return run_path;
    }

    public boolean if_run_path_match(String a_run_path){
        return (run_path.equals(a_run_path));
    }

    @Override
    protected void finalize() throws Throwable {
        stop();
        super.finalize();
    }
}
