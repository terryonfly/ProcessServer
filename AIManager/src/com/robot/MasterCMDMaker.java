package com.robot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by terry on 15/10/30.
 */
public class MasterCMDMaker {

    public enum RES_ID {
        RES_ID_PROCESSES_LIST,
    }

    private static final MasterCMDMaker masterCMDMaker = new MasterCMDMaker();

    //静态工厂方法
    public static MasterCMDMaker getInstance() {
        return masterCMDMaker;
    }

    public int res_id_int(RES_ID a_res_id) {
        for (int i = 0; i < RES_ID.values().length; i ++) {
            if (a_res_id == RES_ID.values()[i]) {
                return i;
            }
        }
        return -1;
    }

    public String make_processes_list(HashMap<String, Integer> processes, int die_processes_count, int unormal_die_processes_count) {
        JSONObject cmd = new JSONObject();
        try {
            cmd.put("res_id", res_id_int(RES_ID.RES_ID_PROCESSES_LIST));
            JSONObject cmd_values = new JSONObject();
            JSONArray processes_list = new JSONArray();
            Iterator iter = processes.entrySet().iterator();
            while(iter.hasNext()) {
                Map.Entry entry = (Map.Entry)iter.next();
                String run_path = (String)entry.getKey();
                Integer run_count = (Integer)entry.getValue();
                JSONObject process = new JSONObject();
                process.put("run_path", run_path);
                process.put("run_count", run_count);
                processes_list.put(process);
            }
            cmd_values.put("processes_list", processes_list);
            cmd_values.put("die_count", die_processes_count);
            cmd_values.put("unormal_die_count", unormal_die_processes_count);
            cmd.put("res_values", cmd_values);
            return cmd.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }
}
