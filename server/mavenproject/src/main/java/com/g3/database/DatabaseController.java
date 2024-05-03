package com.g3.database;

import java.sql.*;

public class DatabaseController {
    private static final String SQL_INSERT = "INSERT INTO car(timestep,device_id,real_lat,real_long,predicted_lat,predicted_long,real_rssi,real_throughput,predicted_rssi,predicted_throughput) VALUES (?,?,?,?,?,?,?,?,?,?)";
    private static final String SQL_DELETE_ALL = "DELETE FROM car";
    private static final String SQL_DELETE_BY_ID = "DELETE FROM car where device_id = ?";
    private static final String SQL_SELECT_ALL = "SELECT * FROM car order by timestep asc, device_id asc";
    private static final String SQL_VIEW26 = "SELECT avg(error_rssi) as error_rssi, avg(error_thr) as error_thr, avg(error_distance) as error_distance FROM view26";
    private static final String SQL_VIEW27 = "SELECT avg(error_rssi) as error_rssi, avg(error_thr) as error_thr, avg(error_distance) as error_distance FROM view27";
    private final String DB_URL = "jdbc:mysql://localhost/signalbell";
    private final String USER = "root";
    private final String PASS = "root";

    private Connection conn = null;

    public void connect() {
        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("DB OK ");
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void disconnect() {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }


    public void insert(long timestep, String device_id, double real_lat, double real_long, double predicted_lat, double predicted_long, double real_rssi, double real_throughput, double predicted_rssi, double predicted_throughput) {
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(SQL_INSERT);

            preparedStatement.setLong(1, timestep);
            preparedStatement.setString(2, device_id);
            preparedStatement.setDouble(3, real_lat);
            preparedStatement.setDouble(4, real_long);
            preparedStatement.setDouble(5, predicted_lat);
            preparedStatement.setDouble(6, predicted_long);
            preparedStatement.setDouble(7, real_rssi);
            preparedStatement.setDouble(8, real_throughput);
            preparedStatement.setDouble(9, predicted_rssi);
            preparedStatement.setDouble(10, predicted_throughput);
            preparedStatement.executeUpdate();
            preparedStatement.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void removeAll() {
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(SQL_DELETE_ALL);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void remove(long id) {
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(SQL_DELETE_BY_ID);
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void displayAll() {
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(SQL_SELECT_ALL);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                long id = resultSet.getLong("id");
                long timestep = resultSet.getLong("timestep");
                String device_id = resultSet.getString("device_id");
                double real_lat = resultSet.getDouble("real_lat");
                double real_long = resultSet.getDouble("real_long");
                double predicted_lat = resultSet.getDouble("predicted_lat");
                double predicted_long = resultSet.getDouble("predicted_long");
                double real_rssi = resultSet.getDouble("real_rssi");
                double real_throughput = resultSet.getDouble("real_throughput");
                double predicted_rssi = resultSet.getDouble("predicted_rssi");
                double predicted_throughput = resultSet.getDouble("predicted_throughput");

                System.out.printf("%6d %3d %5s %7s %7f %7f %7f %5.2f %5.2f %5.2f %5.2f \n", id, timestep, device_id, real_lat, real_long, predicted_lat, predicted_long, real_rssi, real_throughput, predicted_rssi, predicted_throughput);
            }


            resultSet.close();
            preparedStatement.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void stats(String car) {
        String SQL;

        if (car.equals("car26")) {
            SQL = SQL_VIEW26;
        } else {
            SQL = SQL_VIEW27;
        }

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(SQL);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                double error_rssi = resultSet.getDouble("error_rssi");
                double error_thr = resultSet.getDouble("error_thr");
                double error_distance = resultSet.getDouble("error_distance");

                System.out.println("error_rssi: " + error_rssi);
                System.out.println("error_thr: " + error_thr);
                System.out.println("error_distance: " + error_distance);
            }

            resultSet.close();
            preparedStatement.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
