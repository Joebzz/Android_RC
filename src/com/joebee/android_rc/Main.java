package com.joebee.android_rc;

import java.util.Set;

import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;
import at.abraxas.amarino.Amarino;

public class Main extends Activity {	
	private Switch tilt_switch;
	private ImageButton left_button;
	private ImageButton right_button;
	String deviceAddress;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		connectBluetooth();
		
		this.setTilt_switch((Switch) this.findViewById(R.id.switch1));
		this.setLeft_button((ImageButton) this.findViewById(R.id.leftButton));
		this.setRight_button((ImageButton) this.findViewById(R.id.rightButton));
	}
	/*@Override
	protected void onStart() {
		super.onStart();
		Amarino.connect(this, deviceAddress);
	}

	@Override
	protected void onStop() {
		super.onStop();
		Amarino.disconnect(this, deviceAddress);
	}*/
	
	public void connectBluetooth(){
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
				.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			// Device does not support Bluetooth
		}
		if (mBluetoothAdapter.isEnabled()) {
			Toast.makeText(this, "Bluetooth Enabled", Toast.LENGTH_SHORT);
		}
		else{
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, 1);
		}

		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter
				.getBondedDevices();
		// If there are paired devices
		if (pairedDevices.size() > 0) {			
			// Loop through paired devices
			for (BluetoothDevice device : pairedDevices) {
				// Add the name and address to an array adapter to show in a
				// ListView
				deviceAddress = device.getAddress();
				String deviceStatus = device.getName() + "\n" + deviceAddress;
				Toast.makeText(this, deviceStatus, Toast.LENGTH_LONG).show();
			}
		}
		
	}
	
	public void rightButtonPressed(View v){
		Toast.makeText(this, "Right Pressed" , Toast.LENGTH_LONG).show();
		//Amarino.sendDataToArduino(this, deviceAddress, 'r', true);
	}
	
	public void leftButtonPressed(View v){
		Toast.makeText(this, "Left Pressed" , Toast.LENGTH_LONG).show();
		//Amarino.sendDataToArduino(this, deviceAddress, 'l', true);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public Switch getTilt_switch() {
		return tilt_switch;
	}

	public void setTilt_switch(Switch tilt_switch) {
		this.tilt_switch = tilt_switch;
	}

	public ImageButton getLeft_button() {
		return left_button;
	}

	public void setLeft_button(ImageButton left_button) {
		this.left_button = left_button;
	}

	public ImageButton getRight_button() {
		return right_button;
	}

	public void setRight_button(ImageButton right_button) {
		this.right_button = right_button;
	}
}
