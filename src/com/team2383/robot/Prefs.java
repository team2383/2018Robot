package com.team2383.robot;

import java.util.Vector;

import edu.wpi.first.wpilibj.Preferences;


public class Prefs {
	private Preferences wpiPrefs;
	
	public Prefs() {
		wpiPrefs = Preferences.getInstance();
	}
	
	public boolean exists(String key) {
		return wpiPrefs.containsKey(key);
	}
	
	public double getDouble(String key, double backup) {
		if(!exists(key)) {
			wpiPrefs.putDouble(key, backup);
		}
		return wpiPrefs.getDouble(key, backup);
	}
	
	public float getFloat(String key, float backup) {
		if(!exists(key)) {
			wpiPrefs.putFloat(key, backup);
		}
		return wpiPrefs.getFloat(key, backup);
	}
	
	public int getInt(String key, int backup) {
		if(!exists(key)) {
			wpiPrefs.putInt(key, backup);
		}
		return wpiPrefs.getInt(key, backup);
	}

	public long getLong(String key, long backup) {
		if(!exists(key)) {
			wpiPrefs.putLong(key, backup);
		}
		return wpiPrefs.getLong(key, backup);
	}
	
	public String getString(String key, String backup) {
		if(!exists(key)) {
			wpiPrefs.putString(key, backup);
		}
		return wpiPrefs.getString(key, backup);
	}
	
	public boolean getBoolean(String key, boolean backup) {
		if(!exists(key)) {
			wpiPrefs.putBoolean(key, backup);
		}
		return wpiPrefs.getBoolean(key, backup);
	}
	
	public Vector<String> getKeys() {
		return wpiPrefs.getKeys();
	}
}