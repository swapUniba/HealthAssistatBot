package com.triage.rest.models.messages;

import java.util.ArrayList;

public class ReplayMarkup {
	private ArrayList<ArrayList<String>> keyboard;
	private boolean one_time_keyboard;
	private boolean resize_keyboard;

	public ReplayMarkup(ArrayList<ArrayList<String>> keyboard, boolean otk) {
		super();
		this.keyboard = keyboard;
		this.one_time_keyboard = otk;
		this.resize_keyboard = true;
	}

	public ArrayList<ArrayList<String>> getKeyboard() {
		return keyboard;
	}

	public void setKeyboard(ArrayList<ArrayList<String>> keyboard) {
		this.keyboard = keyboard;
	}

	public boolean isOne_time_keyboard() {
		return one_time_keyboard;
	}

	public void setOne_time_keyboard(boolean one_time_keyboard) {
		this.one_time_keyboard = one_time_keyboard;
	}
	
	public boolean isResize_keyboard() {
		return resize_keyboard;
	}

	public void setResize_keyboard(boolean resize_keyboard) {
		this.resize_keyboard = resize_keyboard;
	}

	@Override
	public String toString() {
		return "ReplayMarkup [keyboard=" + keyboard + ", one_time_keyboard=" + one_time_keyboard + "]";
	}
}
