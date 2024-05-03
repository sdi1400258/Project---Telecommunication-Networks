package com.g3.conversion;

import java.io.*;
import java.util.Random;

public class ConversionController {
    private final static String filename1 = "all_vehicles.xml";
    private final static String filename2 = "vehicle_26.xml";
    private final static String filename3 = "vehicle_27.xml";

    private final static String filename1_out = "all_vehicles.csv";
    private final static String filename2_out = "vehicle_26.csv";
    private final static String filename3_out = "vehicle_27.csv";

    private final Random random = new Random();

    private void convertFile(String filename, String filename_out) throws IOException {
        File file = new File(filename);
        File file_out = new File(filename_out);
        try (FileInputStream fis = new FileInputStream(file);
             FileOutputStream fos = new FileOutputStream(file_out)) {
            byte[] data = new byte[(int) file.length()];//read file byte per byte,initialize a byte array in Java
            fis.read(data);//method of input reading file
            String str = new String(data, "UTF-8");
//            System.out.println(str);

            String[] timesteps = str.split("</timestep>");//ignore </timestep>

            for (String timestep : timesteps) {
                String t = timestep.trim();//avoid spaces
                String[] lines = t.split("\n");

                System.out.println("--------------------------------");

                if (lines.length > 0) {
                    String header = lines[0];

                    if (header.contains("time")) {
                        String[] headerwords = header.split("\"");

                        if (headerwords.length > 1) {
                            String time = headerwords[1];


                            System.out.println(time);


                            for (int i = 1; i < lines.length; i++) {
                                String[] attributes = lines[i].trim().split(" ");
                                String id = "id", lat = "lat", lon ="lon", angle="angle", speed="speed", rssi="rssi", throughput="throughput";

                                System.out.println(attributes[1]);

                                id = attributes[1].split("\"")[1];

                                lat = attributes[2].split("\"")[1];

                                lon = attributes[3].split("\"")[1];

                                angle = attributes[4].split("\"")[1];

                                speed = attributes[6].split("\"")[1];
                                //String text = "12.34"; // example String
                                //double value = Double.parseDouble(text);

                                while (true) {
                                    double q = random.nextGaussian();
                                    rssi = String.valueOf(q * 120 + 60);

                                    if (Float.parseFloat(rssi) < 20 || Float.parseFloat(rssi) > 100) {
                                        continue;
                                    } else {
                                        throughput = String.valueOf(Float.parseFloat(rssi) / 2.0f);
                                        break;
                                    }
                                }

                                String s = String.format("%s,%s,%s,%s,%s,%s,%s,%s\n",time,id, lat, lon, angle, speed, rssi, throughput);
                                fos.write(s.getBytes());

                            }
                        }


                    }
                }
            }
        }
    }

    public void convert() throws IOException {
        convertFile(filename1, filename1_out);
        convertFile(filename2, filename2_out);
        convertFile(filename3, filename3_out);
    }
}
