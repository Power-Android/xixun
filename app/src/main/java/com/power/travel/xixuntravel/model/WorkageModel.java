package com.power.travel.xixuntravel.model;

import java.io.Serializable;

/**
 * @author fan
 *
 */
@SuppressWarnings("serial")
public class WorkageModel implements Serializable {

	private String id;
	private String name;//
	private String type;//
	private String val;//
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getVal() {
		return val;
	}
	public void setVal(String val) {
		this.val = val;
	}
	
	
}
