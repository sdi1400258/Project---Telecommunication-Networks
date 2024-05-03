package com.g3.myapp.internetoptions;

public class ApplicationOptions {
    public static String EDGE_IP = "192.168.43.189";
    public static int EDGE_PORT = 1883;
    public static int TIME = 500;
    public static String file = "vehicle_26.csv";

    public static String getEdgeIp() {
        return EDGE_IP;
    }

    public static void setEdgeIp(String edgeIp) {
        ApplicationOptions.EDGE_IP = edgeIp;
    }

    public static int getEdgePort() {
        return EDGE_PORT;
    }

    public static void setEdgePort(int edgePort) {
        ApplicationOptions.EDGE_PORT = edgePort;
    }

    public static void setPORT(String PORT) {
        ApplicationOptions.EDGE_PORT = Integer.parseInt(PORT);
    }

    public static int getTIME() {
        return TIME;
    }

    public static void setTIME(int TIME) {
        ApplicationOptions.TIME = TIME;
    }

    public static void setTIME(String TIME) {
        ApplicationOptions.TIME = Integer.parseInt(TIME);
    }

    public static String getFile() {
        return file;
    }

    public static int getRole() {
        if (file.contains("26")) {
            return 26;
        } else {
            return 27;
        }
    }

    public static void setFile(String file) {
        ApplicationOptions.file = file;
    }


    public static String[] getOptions() {
        String[] opt = new String[4];

        opt[0] = EDGE_IP;
        opt[1] = "" + EDGE_PORT;
        opt[2] = "" + TIME;
        opt[3] = file;

        return opt;
    }

    public static void setOptions(String[] opt) {
        EDGE_IP = opt[0];
        EDGE_PORT = Integer.parseInt(opt[1]);
        TIME = Integer.parseInt(opt[2]);
        file = opt[3];
    }
}
