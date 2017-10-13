package com.power.travel.xixuntravel.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class MasterModel implements Serializable {

	private String id;
	private String master;//地主达人  0否  1是
	private String username;
	private String nickname;
	private String sex;
	private String face;
	private String address ;
	private String signature;
	private String if_guide;
	private String if_driver ;
	private String is_follow;
	private String follow_id;
	private String follow;
	private String cityName;
	private String area;
	private String mid;

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getFollow() {
		return follow;
	}

	public void setFollow(String follow) {
		this.follow = follow;
	}

	public String getFollow_id() {
		return follow_id;
	}

	public void setFollow_id(String follow_id) {
		this.follow_id = follow_id;
	}

	public String getIs_follow() {
		return is_follow;
	}

	public void setIs_follow(String is_follow) {
		this.is_follow = is_follow;
	}

	public String getSignature() {
		return signature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
	}
	public String getIf_guide() {
		return if_guide;
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public void setIf_guide(String if_guide) {
		this.if_guide = if_guide;
	}
	public String getIf_driver() {
		return if_driver;
	}
	public void setIf_driver(String if_driver) {
		this.if_driver = if_driver;
	}
	private String Apart;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMaster() {
		return master;
	}
	public void setMaster(String master) {
		this.master = master;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getFace() {
		return face;
	}
	public void setFace(String face) {
		this.face = face;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getApart() {
		return Apart;
	}
	public void setApart(String apart) {
		Apart = apart;
	}
	
}
