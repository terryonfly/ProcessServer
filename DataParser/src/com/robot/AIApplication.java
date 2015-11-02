package com.robot;

import com.robot.webparser.URLQueue;
import com.robot.webparser.WebParser;

import java.io.File;
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
        URLQueue urlQueue = new URLQueue("URLQueue");
        urlQueue.start();
        WebParser webParser = new WebParser("WebParser", urlQueue);
        webParser.start();
        while (!killSignal.has_kill) {
            try {
                Thread.sleep(1 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void kill() {

    }
}
