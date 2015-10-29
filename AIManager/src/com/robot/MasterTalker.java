package com.robot;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import org.json.*;

/**
 * Created by terry on 15/10/29.
 */
public class MasterTalker implements Runnable {

    private Socket masterClient;

    private boolean is_running = true;

    private DataOutputStream outputStream = null;

    public MasterCMDParser masterCMDParser;

    public MasterTalker(Socket a_masterClient) {
        masterClient = a_masterClient;
        masterCMDParser = new MasterCMDParser(this);
        new Thread(this).start();
    }

    public void stop() {
        is_running = false;
    }

    @Override
    public void run() {
        DataInputStream inputStream = null;
        try {
            inputStream = new DataInputStream(masterClient.getInputStream());
            outputStream = new DataOutputStream(masterClient.getOutputStream());
            while (!masterClient.isClosed() && is_running) {
                String inputString = inputStream.readUTF();
//                System.out.printf(inputString);
                masterCMDParser.parse_base(inputString);
            }
        } catch (IOException e) {
//            e.printStackTrace();
        }
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            outputStream = null;
        }
        if (masterClient != null) {
            try {
                masterClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            masterClient = null;
        }
        is_running = false;
        System.out.printf("One master leaved\n");
    }

    public void send_msg(String msg) {
        String outputString = msg;
        if (outputStream != null) {
            try {
                outputStream.writeUTF(outputString);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean check_is_running() {
        return is_running;
    }

    @Override
    protected void finalize() throws Throwable {
        if (outputStream != null)
            outputStream.close();
        stop();
        super.finalize();
    }
}
