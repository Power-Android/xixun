package com.power.travel.xixuntravel.model;

import java.io.Serializable;

/**
 *
 * @author fan
 *
 */
@SuppressWarnings("serial")
public class CarModel implements Serializable {

	private String id;
	private String upid;
	private String name;
	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUpid() {
		return upid;
	}
	public void setUpid(String upid) {
		this.upid = upid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
}
