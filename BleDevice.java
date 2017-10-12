package ch.ethz.inf.vs.a1.jvkalle.ble;
import android.bluetooth.BluetoothGatt;

public class BleDevice {
    private int mConnectionState;
    private BluetoothGatt mBluetoothGatt;

    public BluetoothGatt getBluetoothGatt() {
        return mBluetoothGatt;
    }

    public int getConnectionState(){
        return mConnectionState;
    }


    public BleDevice(final BluetoothGatt bluetoothGatt, int ConnectionState){
        mBluetoothGatt = bluetoothGatt;
        mConnectionState = ConnectionState;
    }

    public void setConnectionState(int connectionState) {
        this.mConnectionState = connectionState;
    }
}
