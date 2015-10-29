package com.robot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.HashMap;

/**
 * Created by terry on 15/10/30.
 */
public class MasterCMDParser {

    RES_ID last_res = null;

    public enum RES_ID {
        RES_ID_PROCESSES_LIST,
    }

    public void parse_base(String a_res) {
        try {
            JSONTokener jsonTokener = new JSONTokener(a_res);
            JSONObject res = (JSONObject)jsonTokener.nextValue();
            int res_id = res.getInt("res_id");
            JSONObject res_values = res.getJSONObject("res_values");
            parse_res(res_id, res_values);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void parse_res(int res_id, JSONObject res_values) {
        if (res_id < 0 || res_id >= RES_ID.values().length) return;
        RES_ID res = RES_ID.values()[res_id];
        try {
            switch (res) {
                case RES_ID_PROCESSES_LIST:
                    JSONArray processes_list = res_values.getJSONArray("processes_list");
                    int total_process_count = 0;
                    for (int i = 0; i < processes_list.length(); i ++) {
                        JSONObject process = (JSONObject)processes_list.get(i);
                        String run_path = process.getString("run_path");
                        int run_count = process.getInt("run_count");
                        System.out.printf("%30s [%d]\n", run_path, run_count);
                        total_process_count += run_count;
                    }
                    System.out.printf("total process count : %d\n", total_process_count);
                    break;
            }
            last_res = res;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
