package com.power.travel.xixuntravel.model;

import java.io.Serializable;

/**
 *
 * @author fan
 * 
 */
@SuppressWarnings("serial")
public class ViewsportDetailCommentModel implements Serializable {

	private String id;//
	private String mid;//
	private String sid;//
	private String content;//
	private String star;//
	private String comment_upName;
	private String addtime;//
	private String username;//
	private String nickname;//
	private String face;//
	
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

	

	public String getComment_upName() {
		return comment_upName;
	}

	public void setComment_upName(String comment_upName) {
		this.comment_upName = comment_upName;
	}


}
