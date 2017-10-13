package com.power.travel.xixuntravel.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class MemberModel implements Serializable {

	private String id;
	private String username;//
	private String nickname;
	private String if_guide;
	private String face;
	private String if_driver;
	private String sex;
	private String address;
	private String signature;

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getFace() {
		return face;
	}

	public void setFace(String face) {
		this.face = face;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getIf_guide() {
		return if_guide;
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

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

}
