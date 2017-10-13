package com.power.travel.xixuntravel.event;

public class CalendarEvent {
	
	public static final int REFRESH=1;
	
	int type;
	
	public CalendarEvent(int type){
		this.type=type;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
