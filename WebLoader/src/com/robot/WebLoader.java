package com.robot;

import java.util.*;

public class WebLoader {

    public static void main(String[] args) {

        KillSignal killSignal = new KillSignal();
        killSignal.add_signal();
        killSignal.start();

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
}
