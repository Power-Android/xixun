package com.power.travel.xixuntravel.model;

import java.io.Serializable;

/**
 *
 * 
 * @author fan
 * 
 */
@SuppressWarnings("serial")
public class TravelDetailCommentModel implements Serializable {

	private String id;//
	private String mid;//
	private String travels_id;//
	private String content;//
	private String comment_id;//
	private String comment_upName;
	private String addtime;//
	private String username;//
	private String nickname;//
	private String face;//
	private String age;//
	private String sex;//
	private String if_guide;//
	private String if_driver;//
	
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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
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

	public String getTravels_id() {
		return travels_id;
	}

	public void setTravels_id(String travels_id) {
		this.travels_id = travels_id;
	}

	public String getComment_id() {
		return comment_id;
	}

	public void setComment_id(String comment_id) {
		this.comment_id = comment_id;
	}

	public String getComment_upName() {
		return comment_upName;
	}

	public void setComment_upName(String comment_upName) {
		this.comment_upName = comment_upName;
	}


}
