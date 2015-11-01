package com.robot;

import com.robot.webloader.URLQueue;
import com.robot.webloader.WebLoader;

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
        URLQueue urlQueue = new URLQueue("URL Queue");
        urlQueue.start();
        int webloader_count = 15;
        ArrayList<WebLoader> webLoaders = new ArrayList<WebLoader>();
        for (int i = 0; i < webloader_count; i ++) {
            WebLoader webLoader = new WebLoader("WebLoader" + i, urlQueue);
            webLoaders.add(webLoader);
            webLoader.start();
        }
        while (!killSignal.has_kill) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void kill() {

    }
}
