package com.robot;

import java.util.*;

public class WebLoader {

    public static void main(String[] args) {

        KillSignal killSignal = new KillSignal();

	    while (!killSignal.has_kill) {
            long ms = new Date().getTime();
//            System.out.printf("[%d.%ds] Web Loader\n", ms / 1000, ms % 1000);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
