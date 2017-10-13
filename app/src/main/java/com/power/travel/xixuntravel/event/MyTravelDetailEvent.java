package com.power.travel.xixuntravel.event;

public class MyTravelDetailEvent {
	
	public static final int REFRESH=1;
	
	int type;
	
	public MyTravelDetailEvent(int type){
		this.type=type;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
