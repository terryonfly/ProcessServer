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
        int webloader_count = 10;
        ArrayList<WebLoader> webLoaders = new ArrayList<WebLoader>();
        while (!killSignal.has_kill) {
            if (urlQueue.target_urls.size() == 0 && webLoaders.size() != 0) {
                for (int i = 0; i < webLoaders.size(); i ++) {
                    webLoaders.get(i).stop();
                    webLoaders.remove(i);
                    i --;
                }
            }
            if (urlQueue.target_urls.size() > 500 & webLoaders.size() == 0) {
                for (int i = 0; i < webloader_count; i ++) {
                    WebLoader webLoader = new WebLoader("WebLoader" + i, urlQueue);
                    webLoaders.add(webLoader);
                    webLoader.start();
                }
            }
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void kill() {

    }
}
