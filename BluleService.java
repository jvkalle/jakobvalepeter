package ch.ethz.inf.vs.a1.jvkalle.ble;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
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
import static android.support.v4.app.ActivityCompat.startActivityForResult;
import static ch.ethz.inf.vs.a1.jvkalle.ble.MainActivity.REQUEST_ENABLE_BT;


public class BluleService extends Service {


    private final Handler mScanHandler = new Handler();
    private final IBinder mBinder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;

    public class LocalBinder extends Binder {
        public BluleService getService() {
            return BluleService.this;
        }
    }

    public boolean initialize() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();

        } else {
            final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = bluetoothManager.getAdapter();
            if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
    }




    public boolean startScan(final BleCallback callback, final long durationMs,final String[] ServiceUuidFilter) {

            final ScanSettings settings = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .build();


        final List<ScanFilter> filters = getScanFilters(ServiceUuidFilter):
        final BluetoothLeScanner mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        Runnable mStopScanningRunnable = new Runnable() {
            @Override
            public void run() {
                mBluetoothLeScanner.stopScan();
            }
        };
        mScanHandler.postDelayed(mStopScanningRunnable, durationMs);
        mBluetoothLeScanner.startScan(filters, settings, callback);
        return true;
        }

    private List<ScanFilter> getScanFilters(String[] serviceUuidFilter) {
    }

}
}
