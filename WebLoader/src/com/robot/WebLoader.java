package com.robot;

public class WebLoader {

    public static void main(String[] args) {

        KillSignal killSignal = new KillSignal();

	    while (true) {
            System.out.printf("Web Loader\n");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
