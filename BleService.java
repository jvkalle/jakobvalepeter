package ch.ethz.inf.vs.a1.jvkalle.ble;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.ParcelUuid;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static android.bluetooth.BluetoothProfile.STATE_CONNECTED;
import static android.bluetooth.BluetoothProfile.STATE_DISCONNECTED;


public class BleService extends Service {
    private final Handler mScanHandler = new Handler();
    private final IBinder mBinder = new LocalBinder();
    private ConcurrentHashMap<String, BleDevice> mDevices;
    private final BluetoothGattCallback mGattCallback = new BleCallback();

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
        mBluetoothLeScanner.startScan(filters, settings, callback);
        return true;
        }





    public boolean connect(final String deviceAddress) {
        if (mBluetoothAdapter == null) {
            return false;
        }
        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(deviceAddress);
        if (device == null) {
            return false;
        }
        final BluetoothGatt mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        mDevices.put(deviceAddress, new BleDevice(mBluetoothGatt, BluetoothProfile.STATE_CONNECTING));
        return true;
    }

    class BleCallback extends BluetoothGattCallback {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            final String deviceAddress = gatt.getDevice().getAddress();
            final BleDevice mBleDevice = mDevices.get(deviceAddress);
            if (mBleDevice == null) return;

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                mBleDevice.setConnectionState(newState);

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                mBleDevice.setConnectionState(newState);
                mBleDevice.getBluetoothGatt().close();
                mDevices.remove(deviceAddress);
            }
        }
    }




            public void disconnect(final String deviceAddress) {
        final BleDevice bleDevice = mDevices.get(deviceAddress);
        bleDevice.getBluetoothGatt().disconnect();
    }




        List<BluetoothDevice> devices = mBluetoothManager.getConnectedDevices(BluetoothProfile.GATT);




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

