package com.power.travel.xixuntravel.model;

import java.io.Serializable;

/**
 *
 * @author fan
 * 
 */
@SuppressWarnings("serial")
public class TripDetailCommentModel implements Serializable {

	private String id;//
	private String mid;//
	private String trip_id;//
	private String content;//
	private String comment_id;
//	private String comment_upName;//
	private String addtime;//
	private String username;//
	private String nickname;//
	private String face;//
	private String age;//
	private String sex;//
	private String if_guide;//
	private String if_driver;//
	private String list;
	private String type;
	private String r_id;
	private String upname;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getR_id() {
		return r_id;
	}

	public void setR_id(String r_id) {
		this.r_id = r_id;
	}

	public String getUpname() {
		return upname;
	}

	public void setUpname(String upname) {
		this.upname = upname;
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

	

	public String getTrip_id() {
		return trip_id;
	}

	public void setTrip_id(String trip_id) {
		this.trip_id = trip_id;
	}

	public String getComment_id() {
		return comment_id;
	}

	public void setComment_id(String comment_id) {
		this.comment_id = comment_id;
	}

	public String getList() {
		return list;
	}

	public void setList(String list) {
		this.list = list;
	}
}
