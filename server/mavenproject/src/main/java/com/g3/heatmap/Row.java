package com.g3.heatmap;

public class Row {
    public final double lat;
    public final double lon;
    public final double rssi;
    public final double throughput;
    public final int i;
    public final int j;

    public Row(double lat, double lon, double rssi, double throughput, int i, int j) {
        this.lat = lat;
        this.lon = lon;
        this.rssi = rssi;
        this.throughput = throughput;
        this.i = i;
        this.j = j;
    }
}
