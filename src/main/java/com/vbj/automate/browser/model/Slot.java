package com.vbj.automate.browser.model;

public class Slot {
	public Slot(String location, String day, String available) {
		this.location = location;
		this.available = available;
		this.day = day;
	}

	public String location;
	public String available;
	public String day;
	
	@Override
	public String toString() {
		return "location : " + location + " slots : " + available + " day : " + day;
	}
}
