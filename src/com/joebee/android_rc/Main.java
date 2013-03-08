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
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
//import android.widget.SeekBar;
//import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import at.abraxas.amarino.Amarino;

/**
 * @author Joebee
 *
 */
public class Main extends Activity implements SensorEventListener {
	private CheckBox cbTiltSteer;
	//private SeekBar sbAccelerator;
	private ImageButton left_button;
	private ImageButton right_button;
	private Button forward_button;
	private Button reverse_button;
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
		
		setCbTiltSteer((CheckBox) findViewById(R.id.checkBox1));
		left_button = (ImageButton) findViewById(R.id.leftButton);
		right_button = (ImageButton) findViewById(R.id.rightButton);
		setForward_button((Button) findViewById(R.id.forwardButton));
		setReverse_button((Button) findViewById(R.id.reverseButton));
		setStop_button((Button) findViewById(R.id.stopButton));
		//sbAccelerator = (SeekBar) findViewById(R.id.seekBarAccelerator);

		/*sbAccelerator.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				accInfo.setText("SeekBar value is " + progress);
			}

			public void onStartTrackingTouch(SeekBar seekBar) {  TODO Auto-generated method stub  }

			public void onStopTrackingTouch(SeekBar seekBar) {  TODO Auto-generated method stub  }
		});*/

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

	public void forwardPressed(View v) {
		Amarino.sendDataToArduino(this, deviceAddress, 'f', true);
	}

	public void reversePressed(View v) {
		Amarino.sendDataToArduino(this, deviceAddress, 'b', true);
	}
	
	public void stopPressed(View v) {
		Amarino.sendDataToArduino(this, deviceAddress, 's', true);
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

	public CheckBox getCbTiltSteer() {
		return cbTiltSteer;
	}

	public void setCbTiltSteer(CheckBox cbTiltSteer) {
		this.cbTiltSteer = cbTiltSteer;
	}

	public Button getReverse_button() {
		return reverse_button;
	}

	public void setReverse_button(Button reverse_button) {
		this.reverse_button = reverse_button;
	}

	public Button getStop_button() {
		return stop_button;
	}

	public void setStop_button(Button stop_button) {
		this.stop_button = stop_button;
	}

	public Button getForward_button() {
		return forward_button;
	}

	public void setForward_button(Button forward_button) {
		this.forward_button = forward_button;
	}
}
