package com.g3.myapp.markeroptions;

public class MqttMarker {
    public double real_lat;
    public double real_lon;
    public double real_rssi;
    public double real_throughput;

    public double pred_lat;
    public double pred_lon;
    public double pred_rssi;
    public double pred_throughput;

    public MqttMarker(double real_lat, double real_lon, double real_rssi, double real_throughput, double pred_lat, double pred_lon, double pred_rssi, double pred_throughput) {
        this.real_lat = real_lat;
        this.real_lon = real_lon;
        this.real_rssi = real_rssi;
        this.real_throughput = real_throughput;
        this.pred_lat = pred_lat;
        this.pred_lon = pred_lon;
        this.pred_rssi = pred_rssi;
        this.pred_throughput = pred_throughput;
    }
}
