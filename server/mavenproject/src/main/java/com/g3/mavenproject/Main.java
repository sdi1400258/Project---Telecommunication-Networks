/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.g3.mavenproject;

import com.g3.conversion.ConversionController;
import com.g3.database.DatabaseController;
import com.g3.mqtt.MqttController;
import com.g3.ui.GuiController;

import javax.swing.*;
import java.io.IOException;
import java.util.Scanner;

/**
 *
 }
 */
public class Main {
    public static void main(String[] args) {
//        DatabaseController ctr = new DatabaseController();
//
////        mqttctr.publishToAndroid1("hello to A1");
//
//        System.out.print("press ennter to exit :");
//        new Scanner(System.in).nextLine();


        GuiController gui = null;
        try {
            MqttController mqttctr = new MqttController();

            mqttctr.connect();

            mqttctr.subscribeToAndroid1();
            mqttctr.subscribeToAndroid2();

            gui = new GuiController();
            gui.display();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        ctr.removeAll();

//        ctr.insert(1, "Car 26", 10,20,30,40,50,60,70,80);
//        ctr.insert(1, "Car 27", 10,20,30,40,50,60,70,80);

//        ctr.remove(8);


//        ctr.displayAll();

//        ctr.disconnect();

    }
}