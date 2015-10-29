package com.robot;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by terry on 15/10/29.
 */
public class ManagerConnector implements Runnable {

    public static final String ip_addr = "localhost";

    public static final int port = 10001;

    private Socket socket;

    private boolean is_running = true;

    private DataOutputStream outputStream = null;

    public MasterCMDParser masterCMDParser;

    public ManagerConnector() {
        new Thread(this).start();
        masterCMDParser = new MasterCMDParser();
        for (int i = 0; i < 10; i ++) {
            if (socket != null) break;
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        is_running = false;
    }

    @Override
    public void run() {
        while (is_running) {
            try {
                System.out.printf("Try connect to server\n");
                Socket a_socket = new Socket(ip_addr, port);
                socket = a_socket;
                System.out.printf("Connect to server successed\n");
                break;
            } catch (IOException e) {
                System.out.printf("Connect to server failed\n");
            }
        }
        if (socket != null) {
            DataInputStream inputStream = null;
            try {
                inputStream = new DataInputStream(socket.getInputStream());
                outputStream = new DataOutputStream(socket.getOutputStream());
                while (!socket.isClosed() && is_running) {
                    if (inputStream.available() > 0) {
                        String inputString = inputStream.readUTF();
//                        System.out.printf(inputString);
                        masterCMDParser.parse_base(inputString);
                    }
                }
            } catch (IOException e) {
//                e.printStackTrace();
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
            if (socket != null) {
                try {
                    socket.close();
                    System.out.printf("Server disconnected\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                socket = null;
            }
        }
        is_running = false;
    }

    public void send_msg(String msg) {
        String outputString = msg;
        if (outputStream != null) {
            try {
                outputStream.writeUTF(outputString);
                System.out.printf("Ctrl msg has been sended\n");
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
