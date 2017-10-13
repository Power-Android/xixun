package com.power.travel.xixuntravel.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class RecommentModel implements Serializable {

	private String id;
	private String title;
//	private String route;//
	private String thumb;//
	private String onclick;//
	private String description;
	private String addtime;
	private String zan;
	private String zanIf;
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
//	public String getRoute() {
//		return route;
//	}
//	public void setRoute(String route) {
//		this.route = route;
//	}
	public String getThumb() {
		return thumb;
	}
	public void setThumb(String thumb) {
		this.thumb = thumb;
	}
	public String getOnclick() {
		return onclick;
	}
	public void setOnclick(String onclick) {
		this.onclick = onclick;
	}
	public String getZan() {
		return zan;
	}
	public void setZan(String zan) {
		this.zan = zan;
	}
	public String getZanIf() {
		return zanIf;
	}
	public void setZanIf(String zanIf) {
		this.zanIf = zanIf;
	}
	
	
}
