/*This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.joebee.android_rc;

import java.util.Scanner;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import at.abraxas.amarino.Amarino;
import at.abraxas.amarino.AmarinoIntent;

/**
 * @author Joebee
 * 
 */
public class Main extends Activity implements SensorEventListener {
	private SeekBar sb_accelerator;
	private ImageButton left_button;
	private ImageButton right_button;
	private ImageButton stop_button;
	private ArduinoReceiver arduino_receiver = new ArduinoReceiver();

	private String sensor_results = "";
	private AlertDialog sensor_alert;

	private String device_address = "00:12:10:17:02:39";

	public boolean set_tilt_steering;
	private float yLimit = 4;
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);

		device_address = prefs.getString("device_address_pref", "");
		set_tilt_steering = prefs.getBoolean("tilt_steering_pref", false);

		left_button = (ImageButton) findViewById(R.id.leftButton);
		right_button = (ImageButton) findViewById(R.id.rightButton);
		
		if (!set_tilt_steering) {
			// turn on the left & right buttons if they are not shown
			if (!left_button.isShown())
				left_button.setVisibility(View.VISIBLE);
			if (!right_button.isShown())
				right_button.setVisibility(View.VISIBLE);

			// set the left button onclick actions and change the color of the
			// button to green when pressed
			left_button.setOnTouchListener(new OnTouchListener() {
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						left_button.setImageResource(R.drawable.left_green);
						leftButtonPressed();
					} else if (event.getAction() == MotionEvent.ACTION_UP) {
						left_button.setImageResource(R.drawable.left);
						ResetSteering();
					}
					return false;
				}
			});

			// set the right button onclick actions and change the color of the
			// button to green when pressed
			right_button.setOnTouchListener(new OnTouchListener() {
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						right_button.setImageResource(R.drawable.right_green);
						rightButtonPressed();
					} else if (event.getAction() == MotionEvent.ACTION_UP) {
						right_button.setImageResource(R.drawable.right);
						ResetSteering();
					}
					return false;
				}
			});
		}
		// else if the tilt steering is selected turn off the image buttons for
		// left & right
		else {
			left_button.setVisibility(View.GONE);
			right_button.setVisibility(View.GONE);
		}
		stop_button = (ImageButton) findViewById(R.id.stopButton);

		sb_accelerator = (SeekBar) findViewById(R.id.seekBarAccelerator);
		stop_button.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					stop_button.setImageResource(R.drawable.stop_green);
					stopPressed();
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					stop_button.setImageResource(R.drawable.stop);
				}
				return false;
			}
		});

		sb_accelerator.setProgress(30);
		sb_accelerator
				.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {
						acceleratorEvent(progress);
					}

					public void onStartTrackingTouch(SeekBar seekBar) {
					}

					public void onStopTrackingTouch(SeekBar seekBar) {
					}
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

	// Functions to connect and disconnect from arduino
	private void Connect() {
		Amarino.connect(this, device_address);
		// in order to receive broadcasted intents we need to register our
		// receiver
		getApplicationContext().registerReceiver(arduino_receiver,
				new IntentFilter(AmarinoIntent.ACTION_RECEIVED));

	}

	private void DisConnect() {
		stopPressed();
		Amarino.disconnect(this, device_address);
		// do never forget to unregister a registered receiver
		getApplicationContext().unregisterReceiver(arduino_receiver);
	}

	protected void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
		Connect();
	}

	@Override
	protected void onStop() {
		super.onStop();
		DisConnect();
	}

	public void acceleratorEvent(int seekBarValue) {
		int outputToArduino = seekBarValue - 30;
		Log.d("accelerator",
				"onAcceleratorChanged: " + Math.abs(outputToArduino));
		if (seekBarValue > 30)
			Amarino.sendDataToArduino(this, device_address, 'f',
					outputToArduino);
		else if (seekBarValue < 30)
			Amarino.sendDataToArduino(this, device_address, 'b',
					Math.abs(outputToArduino));
		else
			stopPressed();
	}

	public void rightButtonPressed() {
		Amarino.sendDataToArduino(this, device_address, 'r', true);
	}

	public void leftButtonPressed() {
		Amarino.sendDataToArduino(this, device_address, 'l', true);
	}

	public void stopPressed() {
		sb_accelerator.setProgress(30);
		Amarino.sendDataToArduino(this, device_address, 's', true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d("option_selected", "" + item.getTitle());
		if (item.getItemId() == R.id.refreshConnectionButton) {
			DisConnect();
			Connect();
			return true;
		} else if (item.getItemId() == R.id.menu_settings) {
			startActivity(new Intent(getBaseContext(), SettingsActivity.class));
			return true;
		} else if (item.getItemId() == R.id.checkSensors) {
			checkSensorEvent();
			return true;
		}
		return false;
	}

	private void CheckSensors() {
		AlertDialog.Builder AB = new AlertDialog.Builder(this);
		
		AB.setTitle("Sensors");

		// set dialog message
		AB.setMessage(sensor_results);
		AB.setCancelable(true);
		AB.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				sensor_alert.cancel();
			}
		});
		AB.setNeutralButton("Refresh", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				checkSensorEvent();
			}
		});
		sensor_alert = AB.create();
		// show it
		sensor_alert.show();
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	public void checkSensorEvent() {
		Amarino.sendDataToArduino(this, device_address, 'c', true);
	}

	public void onSensorChanged(SensorEvent event) {
		if (set_tilt_steering) {
			Log.d("tilt_changed", "onSensorChanged: " + event.values[1]);

			float y = event.values[1];

			if (y > yLimit)
				Amarino.sendDataToArduino(this, device_address, 'r', true);

			else if (y < -yLimit)
				Amarino.sendDataToArduino(this, device_address, 'l', true);
			else
				ResetSteering();
		}
	}

	public void ResetSteering() {
		Amarino.sendDataToArduino(this, device_address, 'v', true);
	}

	/**
	 * ArduinoReceiver is responsible for catching broadcasted Amarino events.
	 * 
	 * It extracts data from the intent and updates the android app
	 */
	public class ArduinoReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			int[] var = new int[10];
			String data;
			Log.d("arduino_recveiver", "Recieved");
			// the type of data which is added to the intent
			final int dataType = intent.getIntExtra(
					AmarinoIntent.EXTRA_DATA_TYPE, -1);

			// we only expect String data though, but it is better to check if
			// really string was sent
			// later Amarino will support differnt data types, so far data comes
			// always as string and
			// you have to parse the data to the type you have sent from
			// Arduino, like it is shown below
			if (dataType == AmarinoIntent.STRING_EXTRA) {
				Scanner s = null;
				data = intent.getStringExtra(AmarinoIntent.EXTRA_DATA);
				Log.d("dataRecievedFromArduino", data);
				try {
					s = new Scanner(data);
					s.useDelimiter(",\\s*");

					int i = 0;
					while (s.hasNext()) {
						if (s.hasNextInt()) {
							var[i] = s.nextInt();

						} else {
							s.next();
						}
						i++;
					}
				} finally {
					s.close();
					sensor_results = "Left: " + var[0] + " - Right: " + var[1];
					CheckSensors();
				}
			}
		}
	}
}