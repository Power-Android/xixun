package com.power.travel.xixuntravel.model;

import java.io.Serializable;

/**
 *
 * @author fan
 * 
 */
@SuppressWarnings("serial")
public class AllTravelModel implements Serializable {

	private String id;//
	private String mid;//
	private String content;//
	private String img;//
	private String video;//
	private String onclick;//
	private String addtime;//
	private String username;//
	private String nickname;//
	private String face;//
	private String age;//
	private String sex;//
	private String if_guide;//
	private String if_driver;//
	private String comment_count;//
	private String zanIf;//
	private String zan;//
	private String comment;//

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

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public String getVideo() {
		return video;
	}

	public void setVideo(String video) {
		this.video = video;
	}

	public String getOnclick() {
		return onclick;
	}

	public void setOnclick(String onclick) {
		this.onclick = onclick;
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

	public String getComment_count() {
		return comment_count;
	}

	public void setComment_count(String comment_count) {
		this.comment_count = comment_count;
	}

	public String getZan() {
		return zan;
	}

	public void setZan(String zan) {
		this.zan = zan;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getZanIf() {
		return zanIf;
	}

	public void setZanIf(String zanIf) {
		this.zanIf = zanIf;
	}

}
