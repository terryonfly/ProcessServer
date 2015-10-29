package com.robot;

import java.util.*;

public class WebLoader {

    public static void main(String[] args) {

        KillSignal killSignal = new KillSignal();

	    while (true) {
            long ms = new Date().getTime();
            System.out.printf("[%d.%ds] Web Loader\n", ms / 1000, ms % 1000);
            try {
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
