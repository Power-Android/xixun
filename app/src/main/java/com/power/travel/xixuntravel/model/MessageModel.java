package com.power.travel.xixuntravel.model;

import java.io.Serializable;

/**
 *
 * @author fan
 *
 */
@SuppressWarnings("serial")
public class MessageModel implements Serializable {

	private String id;//
	private String mid;//
	private String cid;//
	private String type;//
	private String content;//
	private String state;
	private String addtime;
	private String username;//
	private String nickname;//
	private String face;
	private String age ;
	private String sex;//
	
	private String if_guide;//
	
	private String if_driver;//
	private String Data;//
	
	
	

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

	public String getMid() {
		return mid;
	}
	public void setMid(String mid) {
		this.mid = mid;
	}

	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}

	public String getAge() {
		return age;
	}
	public void setAge(String age) {
		this.age = age;
	}
	public String getCid() {
		return cid;
	}
	public void setCid(String cid) {
		this.cid = cid;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getAddtime() {
		return addtime;
	}
	public void setAddtime(String addtime) {
		this.addtime = addtime;
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
	public String getData() {
		return Data;
	}
	public void setData(String data) {
		Data = data;
	}
	
}
