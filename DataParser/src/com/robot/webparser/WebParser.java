package com.robot.webparser;

import com.robot.database.Connector;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by terry on 15/11/2.
 */
public class WebParser implements Runnable {

    Thread thread;
    String thread_name;
    boolean is_run;
    Connector db;
    URLQueue urlQueue;

    public WebParser(String a_thread_name, URLQueue a_urlQueue) {
        thread_name = a_thread_name;
        is_run = false;
//        db = new Connector();
//        db.connect();
        //urlQueue = new URLQueue("URLQueue");
        //urlQueue.start();
        urlQueue = a_urlQueue;
    }

    public void start() {
        if (is_run) return;
        thread = new Thread(this);
        is_run = true;
        thread.start();
    }

    public void stop() {
        if (!is_run) return;
        is_run = false;
        thread = null;
    }

    @Override
    public void run() {
        try {
            DataAnalyzer dataAnalyzer = new DataAnalyzer();
            while (is_run) {
                URLModel unparsed_url = urlQueue.get_one_unparsed_url();
                if (unparsed_url != null && unparsed_url.url.length() > 0) {
                    String inputHTML = "";
                    File file = new File("../WebLoader/data/" + unparsed_url.url_id + ".data");
                    if (!file.exists()) {
                        System.out.printf("%s not found\n", file.getPath());
                        continue;
                    }
                    try {
                        BufferedReader reader = new BufferedReader(new FileReader(file));
                        String s;
                        while ((s = reader.readLine()) != null) {
                            inputHTML += s;
                            inputHTML += "\n";
                        }
                        reader.close();
                    } catch (FileNotFoundException e) {
                        urlQueue.feedback_parsing_result(unparsed_url, false);
                        continue;
                    } catch (IOException e) {
                        urlQueue.feedback_parsing_result(unparsed_url, false);
                        continue;
                    }
                    if (inputHTML == null || inputHTML.length() == 0) {
                        urlQueue.feedback_parsing_result(unparsed_url, false);
                        continue;
                    }
                    dataAnalyzer.set_resource(inputHTML);
                    // Content Data
                    ArrayList<String> content_datas = dataAnalyzer.getContentDatas();
                    File data_file = new File("data/" + unparsed_url.url_id + ".content");
                    try {
                        BufferedWriter writer = new BufferedWriter(new FileWriter(data_file));
                        for (int i = 0; i < content_datas.size(); i ++) {
                            writer.write(content_datas.get(i));
                            writer.write("\n");
                            writer.flush();
                        }
                        writer.close();
                    } catch (IOException e) {
                        urlQueue.feedback_parsing_result(unparsed_url, false);
                        continue;
                    }
                    // Links
                    urlQueue.add_urls(dataAnalyzer.getLinks());
                    urlQueue.feedback_parsing_result(unparsed_url, true);
                    // Move to done_data
                    File done_file = new File("done_page/" + unparsed_url.url_id + ".data");
                    file.renameTo(done_file);
                } else {
                    Thread.sleep(1000);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        stop();
        super.finalize();
    }
}
