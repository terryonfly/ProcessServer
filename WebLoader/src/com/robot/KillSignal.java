package com.robot;

import sun.misc.Signal;
import sun.misc.SignalHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by terry on 15/10/30.
 */
public class KillSignal implements Runnable, SignalHandler {

    public KillSignal() {
        new Thread(this).start();
        Signal.handle(new Signal("TERM"), this);
        Signal.handle(new Signal("INT"), this);
    }

    @Override
    public void run() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            try {
                String readedString = reader.readLine();
                if (readedString.equals("Please kill yourself, thank you!")) {
                    kill();
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void handle(Signal signal) {
        kill();
    }

    public void kill() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("kill signal is recevied.");
        System.exit(0);
    }
}
