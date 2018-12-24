/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.bluetoothadvertisements;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * Scans for Bluetooth Low Energy Advertisements matching a filter and displays them to the user.
 */
public class ScannerFragment extends ListFragment implements
        ServiceConnection, OnBeaconEventListener, CompoundButton.OnCheckedChangeListener {

    private static final String TAG = ScannerFragment.class.getSimpleName();

    /**
     * Stops scanning after 5 seconds.
     */
    private static final long SCAN_PERIOD = 5000;

    private static final long SWITCH_ENABLE_INTERVAL = 5000;

    private ScanResultAdapter mAdapter;

    private Handler mHandler;

    private ScannerService mService;

    private Switch mSwitch;

    private boolean mBound = false;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            Log.d(TAG, "Connected to scanner service");
            mService = ((ScannerService.LocalBinder) service).getService();
            mService.startLocationBeaconScanning();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBound = false;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
        Log.d(TAG,"#### Scanner Fragment On created");

        // Use getActivity().getApplicationContext() instead of just getActivity() because this
        // object lives in a fragment and needs to be kept separate from the Activity lifecycle.
        //
        // We could get a LayoutInflater from the ApplicationContext but it messes with the
        // default theme, so generate it from getActivity() and pass it in separately.
        mAdapter = new ScanResultAdapter(getActivity().getApplicationContext(),
                LayoutInflater.from(getActivity()));
        mHandler = new Handler();

    }
    @Override
    public void onStart(){
        super.onStart();
        Log.d(TAG,"#### Scanner Fragment On started");
        // スキャンを実行するサービスとバインドする.
        Intent intent = new Intent(getActivity(),ScannerService.class);
        // TODO: onAttatch()で自分がセットされたActivityの参照を取るようにしよう(getActivty()そんなに使わないで)
        getActivity().bindService(intent,mConnection,Context.BIND_AUTO_CREATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = super.onCreateView(inflater, container, savedInstanceState);
        setListAdapter(mAdapter);
        setHasOptionsMenu(true);
        return view;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.scanner_menu, menu);

        mSwitch = (Switch)menu.findItem(R.id.scanning_switch).getActionView();
        mSwitch .setOnCheckedChangeListener(this);
        mSwitch.setChecked(true);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        boolean on = isChecked;
        mSwitch.setEnabled(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwitch.setEnabled(true);
            }
        },SWITCH_ENABLE_INTERVAL);

        if (on) {
            mService.startLocationBeaconScanning();
        } else {
            mService.stopLocationBeaconScanning();
        }

        //スイッチが押された時に呼ばれる
        if( isChecked ){
            Toast.makeText(getActivity(), "Switch is checked", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getActivity(), "Switch is unchecked", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume(){
        super.onResume();

    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getListView().setDivider(null);
        getListView().setDividerHeight(0);

        setEmptyText(getString(R.string.empty_list));

        // Trigger refresh on app's 1st load
//        startScanning();
    }



    /* Handle connection events to the discovery service */
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.d(TAG, "Connected from scanner service");
        mService.startLocationBeaconScanning();
        mService.setBeaconEventListener(this); // ここでonBeaconIdentifier()とonBeaconTelemetry()の実装を渡す.
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.d(TAG, "Disconnected from scanner service");
        mService.stopLocationBeaconScanning();
        mService = null;
    }

    /**
     * ScannerServiceが発生させたイベントをこのコールバックでハンドリングする(UIスレッド)
     * */
    @Override
    public void onBeaconIdentifier(String deviceAddress, int rssi, String instanceId) {
        Log.d(TAG,"on BeaconIdentifier called");
    }

    /**
     * ビーコン発見時の挙動(ListAdapterで描画する)
     * */
    @Override
    public void onLocationBeaconReceived(ScanResult result) {
//        final long now = System.currentTimeMillis();
//        for (LocationBeacon item : mAdapter.getAdapterItems()) {
//            if (instanceId.equals(item.id)) {
//                //Already have this one, make sure device info is up to date
//                item.update(deviceAddress, rssi, now);
//                mAdapter.notifyDataSetChanged();
//                return;
//            }
//        }
//
//        //New beacon, add it
//        LocationBeacon beacon =
//                new LocationBeacon(deviceAddress, rssi, instanceId, now);
        Log.d(TAG,"on LocationBeacon Received... ");
        mAdapter.add(result);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBeaconTelemetry(String deviceAddress, float battery, float temperature) {
        Log.d(TAG,"on BeaconTelemetry called");
    }

//    /**
//     * Start scanning for BLE Advertisements (& set it up to stop after a set period of time).
//     */
//    public void startScanning() {
//        if (mScanCallback == null) {
//            Log.d(TAG, "Starting Scanning");
//
//            // Will stop the scanning after a set time.
//            mHandler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    stopScanning();
//                }
//            }, SCAN_PERIOD);
//
//            // Kick off a new scan.
//            mScanCallback = new SampleScanCallback();
//            mBluetoothLeScanner.startScan(buildScanFilters(), buildScanSettings(), mScanCallback);
//
//            String toastText = getString(R.string.scan_start_toast) + " "
//                    + TimeUnit.SECONDS.convert(SCAN_PERIOD, TimeUnit.MILLISECONDS) + " "
//                    + getString(R.string.seconds);
//            Toast.makeText(getActivity(), toastText, Toast.LENGTH_LONG).show();
//        } else {
//            Toast.makeText(getActivity(), R.string.already_scanning, Toast.LENGTH_SHORT);
//        }
//    }

//    /**
//     * Stop scanning for BLE Advertisements.
//     */
//    public void stopScanning() {
//        Log.d(TAG, "Stopping Scanning");
//
//        // Stop the scan, wipe the callback.
//        mBluetoothLeScanner.stopScan(mScanCallback);
//        mScanCallback = null;
//
//        // Even if no new results, update 'last seen' times.
//        mAdapter.notifyDataSetChanged();
//    }

//    /**
//     * Return a List of {@link ScanFilter} objects to filter by Service UUID.
//     */
//    private List<ScanFilter> buildScanFilters() {
//        List<ScanFilter> scanFilters = new ArrayList<>();
//
//        ScanFilter.Builder builder = new ScanFilter.Builder();
//        // Comment out the below line to see all BLE devices around you
//        builder.setServiceUuid(Constants.Service_UUID);
//        scanFilters.add(builder.build());
//
//        return scanFilters;
//    }
//
//    /**
//     * Return a {@link ScanSettings} object set to use low power (to preserve battery life).
//     */
//    private ScanSettings buildScanSettings() {
//        ScanSettings.Builder builder = new ScanSettings.Builder();
//        builder.setScanMode(ScanSettings.SCAN_MODE_LOW_POWER);
//        return builder.build();
//    }

//    /**
//     * Custom ScanCallback object - adds to adapter on success, displays error on failure.
//     */
//    private class SampleScanCallback extends ScanCallback {
//
//        @Override
//        public void onBatchScanResults(List<ScanResult> results) {
//            super.onBatchScanResults(results);
//
//            for (ScanResult result : results) {
//                mAdapter.add(result);
//            }
//            mAdapter.notifyDataSetChanged();
//        }
//
//        @Override
//        public void onScanResult(int callbackType, ScanResult result) {
//            super.onScanResult(callbackType, result);
//
//            mAdapter.add(result);
//            mAdapter.notifyDataSetChanged();
//        }
//
//        @Override
//        public void onScanFailed(int errorCode) {
//            super.onScanFailed(errorCode);
//            Toast.makeText(getActivity(), "Scan failed with error: " + errorCode, Toast.LENGTH_LONG)
//                    .show();
//        }
//    }
}
