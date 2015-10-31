package com.robot.webloader;

import java.io.*;

/**
 * Created by terry on 15/10/22.
 */
public class URLCache {

    String cache_file = "url_cache.txt";
    BufferedReader reader;
    FileWriter writer;

    public URLCache() {
        try {
            File file = new File(cache_file);
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            reader = new BufferedReader(new FileReader(cache_file));
            writer = new FileWriter(cache_file, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String read_out() {
        String out = null;
        try {
            out = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out;
    }

    public void write_in(String in) {
        try {
            writer.write(in + "\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
