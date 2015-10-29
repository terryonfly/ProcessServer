package com.robot;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by terry on 15/10/29.
 */
public class ProcessWatchDog implements Runnable {

    private boolean is_running = true;

    private String run_path;

    private String run_cmd;

    public ProcessWatchDog(String a_run_path, String a_run_cmd) {
        run_path = a_run_path;
        run_cmd = a_run_cmd;
        new Thread(this).start();
    }

    public void stop() {
        is_running = false;
    }

    @Override
    public void run() {
        try {
            String s;
            Process process;
            File f = new File(run_path);
            process = Runtime.getRuntime().exec(run_cmd, null, f);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while(is_running) {
                if ((s=bufferedReader.readLine()) != null) {
                    System.out.println(s);
                }
            }
            OutputStream outputStream = process.getOutputStream ();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
            writer.write("Please kill yourself, thank you!\n");
            writer.flush();
            process.waitFor();
            process.destroy();
            System.out.printf("Delete thread\n");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
