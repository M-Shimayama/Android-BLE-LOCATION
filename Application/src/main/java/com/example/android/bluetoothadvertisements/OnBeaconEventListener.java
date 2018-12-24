package com.example.android.bluetoothadvertisements;

import android.bluetooth.le.ScanResult;

public interface OnBeaconEventListener {
    /**
     * ビーコンを発見した時のイベントリスナ
     * @param deviceAddress
     * @param rssi
     * @param instanceId
     */
    void onBeaconIdentifier(String deviceAddress, int rssi, String instanceId);
    void onLocationBeaconReceived(ScanResult result);
    void onBeaconTelemetry(String deviceAddress, float battery, float temperature);
}
