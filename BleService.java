package ch.ethz.inf.vs.a1.jvkalle.ble;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;



import java.util.ArrayList;

import java.util.List;
import static android.content.ContentValues.TAG;

import static ch.ethz.inf.vs.a1.jvkalle.ble.MainActivity.REQUEST_ENABLE_BT;


public class BleService extends Service {


    private final Handler mScanHandler = new Handler();
    private final IBinder mBinder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;

    public class LocalBinder extends Binder {
        public BleService getService() {
            return BleService.this;
        }
    }

    public void initialize() {
        mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
    }




    public boolean startScan(final ScanCallback callback, final long durationMs,
                             final String[] ServiceUuid, final String[] DeviceName) {

            final ScanSettings settings = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .build();

        final List<ScanFilter> filters = getScanFilters(ServiceUuid,DeviceName);
        final BluetoothLeScanner mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        Runnable mStopScanningRunnable = new Runnable() {
            @Override
            public void run() {mBluetoothLeScanner.stopScan(callback);
            }
        };
        mScanHandler.postDelayed(mStopScanningRunnable, durationMs);
        final List<ScanFilter> filterList = getScanFilters(DeviceName,ServiceUuid)
        mBluetoothLeScanner.startScan(filterList, settings, callback);

        return true;
        }

    private List<ScanFilter> getScanFilters(final String[] DeviceName, final String[] ServiceUuid) {
        final List<ScanFilter> filters = new ArrayList<>();
        if (DeviceName != null) {
            for (String deviceName : DeviceName) {
                final ScanFilter scanFilter = new ScanFilter.Builder()
                        .setDeviceName(deviceName)
                        .build();
                filters.add(scanFilter);
            }
        }
        if (ServiceUuid != null) {
            for (String uuidSString : ServiceUuid) {
                final ScanFilter scanFilter = new ScanFilter.Builder()
                        .setServiceUuid(ParcelUuid.fromString(uuidSString))
                        .build();
                filters.add(scanFilter);
            }
        }
        return filters;
    }
}

