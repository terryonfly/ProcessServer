package com.robot;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by terry on 15/10/29.
 */
public class MasterCMDMaker {

    public enum CMD_ID {
        CMD_ID_SET_PROCESSES_COUNT,
        CMD_ID_REMOVE_ALL_PROCESSES,
        CMD_ID_LIST_PROCESSES,
        CMD_ID_SHOW_ALL_PROCESSES_OUTPUT,
    }

    public int cmd_id_int(CMD_ID a_cmd_id) {
        for (int i = 0; i < CMD_ID.values().length; i ++) {
            if (a_cmd_id == CMD_ID.values()[i]) {
                return i;
            }
        }
        return -1;
    }

    public String make_set_processes_count(String run_path, int run_count) {
        JSONObject cmd = new JSONObject();
        try {
            cmd.put("cmd_id", cmd_id_int(CMD_ID.CMD_ID_SET_PROCESSES_COUNT));
            JSONObject cmd_values = new JSONObject();
            cmd_values.put("run_path", run_path);
            cmd_values.put("run_count", run_count);
            cmd.put("cmd_values", cmd_values);
            return cmd.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String make_remove_all_processes() {
        JSONObject cmd = new JSONObject();
        try {
            cmd.put("cmd_id", cmd_id_int(CMD_ID.CMD_ID_REMOVE_ALL_PROCESSES));
            JSONObject cmd_values = new JSONObject();
            cmd.put("cmd_values", cmd_values);
            return cmd.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String make_list_processes() {
        JSONObject cmd = new JSONObject();
        try {
            cmd.put("cmd_id", cmd_id_int(CMD_ID.CMD_ID_LIST_PROCESSES));
            JSONObject cmd_values = new JSONObject();
            cmd.put("cmd_values", cmd_values);
            return cmd.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String make_show_all_processes_output() {
        JSONObject cmd = new JSONObject();
        try {
            cmd.put("cmd_id", cmd_id_int(CMD_ID.CMD_ID_SHOW_ALL_PROCESSES_OUTPUT));
            JSONObject cmd_values = new JSONObject();
            cmd.put("cmd_values", cmd_values);
            return cmd.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }
}
