package com.g3.heatmap;

import java.io.*;
import java.util.HashSet;

public class HeatmapController {
    private final static String filename1 = "all_vehicles.csv";

    public Heatmap calculate() throws IOException {
        File file = new File(filename1);

        HashSet<Row> rows = new HashSet<>();

        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] data = new byte[(int) file.length()];
            fis.read(data);

            String s = new String(data);

            String[] lines = s.split("\n");

            for (String line : lines ) {
                String [] words = line.split(",");
                //use for conversion controller
                double lon = Double.parseDouble(words[2]);
                double lat = Double.parseDouble(words[3]);

                int i = 0, j = 0;

                if (lat > 37.9668800 && lat <= 37.9673150) {
                    i = 4;
                    System.out.println("i=4");
                } else if (lat > 37.9673150 && lat <= 37.9677500) {
                    i = 3;
                    System.out.println("i=3");
                } else if (lat > 37.9677500 && lat <= 37.9681850) {
                    i = 2;
                    System.out.println("i=2");
                } else if (lat > 37.9681850 && lat <= 37.9686200) {
                    i = 1;
                    System.out.println("i=1");
                } else {
                    System.out.println("latitude out of map");
                    continue;
                }

                if (lon > 23.7647600 && lon < 23.7658230) {
                    j = 1;
                    System.out.println("j=1");
                } else if (lon > 23.7658230 && lon <= 23.7668860) {
                    j = 2;
                    System.out.println("j=2");
                } else if (lon > 23.7668860 && lon <= 23.7679490) {
                    j = 3;
                    System.out.println("j=3");
                } else if (lon > 23.7679490 && lon <= 23.7690120) {
                    j = 4;
                    System.out.println("j=4");
                } else if (lon > 23.7690120 && lon <= 23.7700750) {
                    j = 5;
                    System.out.println("j=5");
                } else if (lon > 23.7700750 && lon <= 23.7711380) {
                    j = 6;
                    System.out.println("j=6");
                } else if (lon > 23.7711380 && lon <= 23.7722010) {
                    j = 7;
                    System.out.println("j=7");
                } else if (lon > 23.7722010 && lon <= 23.7732640) {
                    j = 8;
                    System.out.println("j=8");
                } else if (lon > 23.7732640 && lon <= 23.7743270) {
                    j = 9;
                    System.out.println("j=9");
                } else if (lon > 23.7743270 && lon <= 23.7753900) {
                    j = 10;
                    System.out.println("j=10");
                } else {
                    System.out.println("longtitude out of map");
                    continue;
                }

                i--;
                j--;

                System.out.println("i=" + i + ", "  + j);

                int n = words.length;
                double rssi = Double.parseDouble(words[n-2]);
                double throughput = Double.parseDouble(words[n-1]);
                Row row = new Row(lat, lon, rssi,throughput ,i, j);

                rows.add(row);
            }
        }

        Heatmap heatmap = new Heatmap();

        for (int i=0;i<4;i++) {
            for (int j=0;j<10;j++) {
                double sum_rssi = 0;
                double sum_throughput = 0;
                int n_rssi = 0;
                int n_throughput = 0;

                for (Row r : rows) {
                    if (r.i == i && r.j == j) {
                        sum_rssi += r.rssi;
                        sum_throughput += r.throughput;
                        n_rssi++;
                        n_throughput++;
                    }
                }

                if (n_rssi > 0) {
                    sum_rssi = sum_rssi / n_rssi;
                }
                if (n_throughput > 0) {
                    sum_throughput = sum_throughput / n_throughput;
                }
                    //normalisation-gauss
                if (sum_rssi != 0) {
                    heatmap.rssi_values[i][j] = (sum_rssi - 20) / 80;
                }
                if (sum_throughput != 0) {
                    heatmap.throughput_values[i][j] = (sum_throughput - 10) / 40;
                }
            }
        }

        return heatmap;
    }
}
