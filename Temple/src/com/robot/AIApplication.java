package com.robot;

import java.util.*;

public class AIApplication {

    KillSignal killSignal = null;

    public static void main(String[] args) {
        AIApplication aiApplication = new AIApplication();
        aiApplication.run();
    }

    public AIApplication() {
        killSignal = new KillSignal(this);
    }

    public void run() {
        while (!killSignal.has_kill) {
            long ms = new Date().getTime();
//            System.out.printf("[%d.%ds] Web Loader\n", ms / 1000, ms % 1000);
            for (int i = 0; i < 1000000; i ++) {
                ms *= ms;
            }
            try {
                Thread.sleep(1 * 100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void kill() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
