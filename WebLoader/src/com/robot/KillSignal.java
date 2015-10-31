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

    public boolean has_kill = false;

    public KillSignal() {
        Signal.handle(new Signal("TERM"), this);
        Signal.handle(new Signal("INT"), this);
    }

    public void start() {
        new Thread(this).start();
    }

    public void add_signal() {
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
                    has_kill = true;
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
        System.out.printf("OK, I will kill myself!\n");
        /* You can do somethings to the end */
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("Now, I had killed myself!\n");
        System.exit(0);
    }
}
