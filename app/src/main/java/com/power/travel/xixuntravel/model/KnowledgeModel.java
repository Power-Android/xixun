package com.power.travel.xixuntravel.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class KnowledgeModel implements Serializable {

	private String id;
	private String title;
	private String description;
	private String addtime;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getAddtime() {
		return addtime;
	}
	public void setAddtime(String addtime) {
		this.addtime = addtime;
	}
	
	
}
