package com.robot;

public class AIMaster {

    public static void main(String[] args) {
        ManagerConnector managerConnector = new ManagerConnector();
        MasterCMDMaker masterCMDMaker = new MasterCMDMaker();

        if (args.length > 0) {
            String arg_cmd = args[0];
            if (arg_cmd.equals("test")) {
                if (args.length == 2) {
                    String test_mode = args[1];
                    if (test_mode.equals("+-process")) {
                        String run_path = "/home/terry/ProcessServer/WebLoader";
                        int run_count = 0;
                        while (true) {
                            if (run_count == 0) run_count = 300;
                            else run_count = 0;
                            String cmd_string = masterCMDMaker.make_set_processes_count(run_path, run_count);
                            managerConnector.send_msg(cmd_string);
                            System.out.printf("count = %d\n", run_count);
                            try {
                                if (run_count == 0) Thread.sleep(420 * 1000);
                                else Thread.sleep(600 * 1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
            if (arg_cmd.equals("set")) {
                if (args.length == 3) {
                    String run_path = args[1];
                    int run_count = Integer.parseInt(args[2]);
                    String cmd_string = masterCMDMaker.make_set_processes_count(run_path, run_count);
                    managerConnector.send_msg(cmd_string);
                }
            }
            if (arg_cmd.equals("clear")) {
                String cmd_string = masterCMDMaker.make_remove_all_processes();
                managerConnector.send_msg(cmd_string);
            }
            if (arg_cmd.equals("list")) {
                String cmd_string = masterCMDMaker.make_list_processes();
                managerConnector.send_msg(cmd_string);
                while (true) {
                    if (managerConnector.masterCMDParser.last_res == MasterCMDParser.RES_ID.RES_ID_PROCESSES_LIST) break;
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (arg_cmd.equals("output")) {
                String cmd_string = masterCMDMaker.make_show_all_processes_output();
                managerConnector.send_msg(cmd_string);
                while (true);
            }
        }

        managerConnector.stop();
    }
}
