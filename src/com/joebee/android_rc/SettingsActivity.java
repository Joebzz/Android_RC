package com.joebee.android_rc;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.util.Log;


public class SettingsActivity extends PreferenceActivity {
	EditTextPreference device_address_pref;
	CheckBoxPreference tilt_steering_pref;
	AlertDialog alert;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		alert = new AlertDialog.Builder(this).create();
		device_address_pref = (EditTextPreference) getPreferenceScreen().findPreference("device_address_pref");
		tilt_steering_pref = (CheckBoxPreference) getPreferenceScreen().findPreference("tilt_steering_pref");
		tilt_steering_pref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {		
            public boolean onPreferenceChange(Preference preference, Object newValue) {
            	startActivity(new Intent(SettingsActivity.this, Main.class));
            	finish();
				return true;
            }
		});
		
		device_address_pref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {		
            public boolean onPreferenceChange(Preference preference, Object newValue) {
            	Log.d("preference_change", "Address: New Value - " + newValue);
                Boolean rtnval = true;
                if (!checkValue(newValue.toString())) {
                	Log.d("preference_checking", "Invalid input");
                	alert.setTitle("Invalid MAC Address");
                	alert.setMessage("You did not enter a correct MAC Address");
                	alert.setButton("OK", new DialogInterface.OnClickListener() {
                		public void onClick(DialogInterface dialog, int which) {
                			alert.cancel();
                		}
                	});
                	alert.show();
                    rtnval = false;
                }
                return rtnval;
            }
        });
	}
	private boolean checkValue(String val){
		Log.d("checking_address", "Value - " + val);
		if(val.matches("^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$"))
			return true;
		else
			return false;
	}
}
