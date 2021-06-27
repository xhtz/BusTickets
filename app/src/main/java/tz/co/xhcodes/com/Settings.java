package tz.co.xhcodes.com;

import android.bluetooth.BluetoothAdapter;

public class Settings {
	
	public static void startBluetoth()
	{
		//starting bluetooth
	 	try {
        	//checking if bluetooth is open before going further
        	BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        	
        	if (!mBluetoothAdapter.isEnabled()) {
        		mBluetoothAdapter.enable();
            }
	 	} catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	public static void stopBluetoth()
	{
		//starting bluetooth
	 	try {
        	//checking if bluetooth is open before going further
        	BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        	
        	if (mBluetoothAdapter.isEnabled()) {
        		mBluetoothAdapter.disable();
            }
	 	} catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
