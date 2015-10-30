package com.robot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by terry on 15/10/29.
 */
public class MasterCMDParser {

    MasterTalker masterTalkerCallback;

    public enum CMD_ID {
        CMD_ID_SET_PROCESSES_COUNT,
        CMD_ID_REMOVE_ALL_PROCESSES,
        CMD_ID_LIST_PROCESSES,
        CMD_ID_SHOW_ALL_PROCESSES_OUTPUT,
    }

    public MasterCMDParser(MasterTalker a_masterTalker) {
        masterTalkerCallback = a_masterTalker;
    }

    public void parse_base(String a_cmd) {
        try {
            JSONTokener jsonTokener = new JSONTokener(a_cmd);
            JSONObject cmd = (JSONObject)jsonTokener.nextValue();
            int cmd_id = cmd.getInt("cmd_id");
            JSONObject cmd_values = cmd.getJSONObject("cmd_values");
            parse_cmd(cmd_id, cmd_values);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void parse_cmd(int cmd_id, JSONObject cmd_values) {
        if (cmd_id < 0 || cmd_id >= CMD_ID.values().length) return;
        CMD_ID cmd = CMD_ID.values()[cmd_id];
        try {
            switch (cmd) {
                case CMD_ID_SET_PROCESSES_COUNT:
                    String run_path = cmd_values.getString("run_path");
                    int run_count = cmd_values.getInt("run_count");
                    ProcessManager.getInstance().set_process_count(run_path, run_count);
                    break;
                case CMD_ID_REMOVE_ALL_PROCESSES:
                    ProcessManager.getInstance().remoce_all_process();
                    break;
                case CMD_ID_LIST_PROCESSES:
                    HashMap<String, Integer> processes = new HashMap<String, Integer>();

                    Iterator iter = ProcessManager.getInstance().processWatchDogs.entrySet().iterator();
                    while (iter.hasNext()) {
                        Map.Entry entry = (Map.Entry) iter.next();
                        String single_run_path = (String) entry.getKey();
                        ProcessManager.ProcessWatchDogList processWatchDogList = (ProcessManager.ProcessWatchDogList) entry.getValue();
                        int single_run_count = processWatchDogList.processWatchDogs.size();
                        processes.put(single_run_path, single_run_count);
                    }
                    int die_count = ProcessManager.getInstance().die_processes.size();
                    int unormal_die_count = ProcessManager.getInstance().unormal_die_processes_count;
                    String res_string = MasterCMDMaker.getInstance().make_processes_list(processes, die_count, unormal_die_count);
                    masterTalkerCallback.send_msg(res_string);
                    break;
                case CMD_ID_SHOW_ALL_PROCESSES_OUTPUT:
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
