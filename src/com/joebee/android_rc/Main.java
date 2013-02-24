package com.joebee.android_rc;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import at.abraxas.amarino.Amarino;

public class Main extends Activity implements SensorEventListener {
	private CheckBox cbTiltSteer;
	private SeekBar sbAccelerator;
	private ImageButton left_button;
	private ImageButton right_button;
	String deviceAddress = "00:12:10:17:02:39";
	TextView yViewO = null;
	TextView accInfo = null;

	private float yLimit = 4;
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		yViewO = (TextView) findViewById(R.id.yboxo);
		accInfo = (TextView) findViewById(R.id.acc_info);
		
		cbTiltSteer = (CheckBox) this.findViewById(R.id.checkBox1);
		left_button = (ImageButton) findViewById(R.id.leftButton);
		right_button = (ImageButton) findViewById(R.id.rightButton);
		sbAccelerator = (SeekBar) findViewById(R.id.seekBarAccelerator);

		sbAccelerator.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				accInfo.setText("SeekBar value is " + progress);
			}

			public void onStartTrackingTouch(SeekBar seekBar) { /* TODO Auto-generated method stub */ }

			public void onStopTrackingTouch(SeekBar seekBar) { /* TODO Auto-generated method stub */ }
		});

		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mAccelerometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mSensorManager.registerListener(this, mAccelerometer,
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	protected void onResume() {
		super.onResume();
		mSensorManager.registerListener(this, mAccelerometer,
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	protected void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
		Amarino.connect(this, deviceAddress);
	}

	@Override
	protected void onStop() {
		super.onStop();
		Amarino.disconnect(this, deviceAddress);
	}

	public void rightButtonPressed(View v) {
		Amarino.sendDataToArduino(this, deviceAddress, 'r', true);
	}

	public void leftButtonPressed(View v) {
		Amarino.sendDataToArduino(this, deviceAddress, 'l', true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	public void onSensorChanged(SensorEvent event) {
		Log.d("tag", "onSensorChanged: " + event.values[1]);
		float y = event.values[1];

		yViewO.setText("Orientation Y: " + y);
		if (y > yLimit || y < -yLimit) {
			if (y > yLimit)
				rightButtonPressed(right_button);

			else if (y < -yLimit)
				leftButtonPressed(left_button);
		}
	}
}
