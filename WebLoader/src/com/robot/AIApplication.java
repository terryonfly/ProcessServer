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
            
        }
    }

    public void kill() {

    }
}
