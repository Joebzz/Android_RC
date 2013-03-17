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

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import at.abraxas.amarino.Amarino;

/**
 * @author Joebee
 *
 */
public class Main extends Activity implements SensorEventListener {
	private CheckBox cbTiltSteer;
	private SeekBar sbAccelerator;
	private ImageButton left_button;
	private ImageButton right_button;
	private Button stop_button;
	
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
		
		cbTiltSteer = (CheckBox) findViewById(R.id.tiltSteerCheckBox);
		left_button = (ImageButton) findViewById(R.id.leftButton);
		right_button = (ImageButton) findViewById(R.id.rightButton);
		stop_button = (Button) findViewById(R.id.stopButton);
		
		sbAccelerator = (SeekBar) findViewById(R.id.seekBarAccelerator);
		
		left_button.setOnTouchListener(new OnTouchListener() {
		    public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN) {
					leftButtonPressed(v);
		        } else if (event.getAction() == MotionEvent.ACTION_UP) {
		            ResetSteering();
		        }
				return false;
			}
		});
		
		right_button.setOnTouchListener(new OnTouchListener() {
		  	public boolean onTouch(View v, MotionEvent event) {
		  		if(event.getAction() == MotionEvent.ACTION_DOWN) {
		  			rightButtonPressed(v);
		        } else if (event.getAction() == MotionEvent.ACTION_UP) {
		            ResetSteering();
		        }
		  		
				return false;
			}
		});
		
		stop_button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				stopPressed();
			}
		});
		
		
		sbAccelerator.setProgress(30);
		sbAccelerator.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				accInfo.setText("SeekBar value is " + progress);
				acceleratorEvent(progress);
			}

			public void onStartTrackingTouch(SeekBar seekBar) {   }

			public void onStopTrackingTouch(SeekBar seekBar) {  }
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
		Amarino.connect(this, deviceAddress);
	}
	private void DisConnect() {
		Amarino.disconnect(this, deviceAddress);
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
		Log.d("tag", "onAcceleratorChanged: " +  Math.abs(seekBarValue -30));
		if(seekBarValue > 30)
			Amarino.sendDataToArduino(this, deviceAddress, 'f',  seekBarValue-30);
		else if(seekBarValue < 30)
			Amarino.sendDataToArduino(this, deviceAddress, 'b',  Math.abs(seekBarValue-30));
		else
			stopPressed();
	}
	
	public void rightButtonPressed(View v) {
		Amarino.sendDataToArduino(this, deviceAddress, 'r', true);
	}
	
	public void leftButtonPressed(View v) {
		Amarino.sendDataToArduino(this, deviceAddress, 'l', true);
	}
	
	public void stopPressed() {
		sbAccelerator.setProgress(30);
		Amarino.sendDataToArduino(this, deviceAddress, 's', true);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.refreshConnectionButton) {
			DisConnect();
			Connect();
			return true;
		}
		
		return false;		
	}
	public void onAccuracyChanged(Sensor sensor, int accuracy) {}

	public void onSensorChanged(SensorEvent event) {
		if(cbTiltSteer.isChecked()){
			Log.d("tag", "onSensorChanged: " + event.values[1]);
		
			float y = event.values[1];
			
			yViewO.setText("Orientation Y: " + y);
		
			if (y > yLimit)
				Amarino.sendDataToArduino(this, deviceAddress, 'r', true);

			else if (y < -yLimit)
				Amarino.sendDataToArduino(this, deviceAddress, 'l', true);
			else
				ResetSteering();
		}
	}
	
	public void ResetSteering(){
		Amarino.sendDataToArduino(this, deviceAddress, 'v', true);
	}
}