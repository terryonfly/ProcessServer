package com.robot;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by terry on 15/10/28.
 */
public class ManagerServer implements Runnable {

    public static final int port = 10001;

    ArrayList<MasterTalker> masterTalkers;

    private boolean is_running = true;

    public ManagerServer() {
        masterTalkers = new ArrayList<MasterTalker>();
        new Thread(this).start();
    }

    public void stop() {
        is_running = false;
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (is_running) {
                Socket client = serverSocket.accept();
                System.out.printf("New master comes\n");
                MasterTalker masterTalker = new MasterTalker(client);
                synchronized (masterTalkers) {
                    masterTalkers.add(masterTalker);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clear_closed_clients() {
        synchronized (masterTalkers) {
            for (int i = 0; i < masterTalkers.size(); i ++) {
                if (!masterTalkers.get(i).check_is_running()) {
                    masterTalkers.remove(i);
                    i --;
                }
            }
        }
    }

    public int get_clients_count() {
        clear_closed_clients();
        return masterTalkers.size();
    }

    public boolean check_is_running() {
        return is_running;
    }

    @Override
    protected void finalize() throws Throwable {
        stop();
        super.finalize();
    }
}
