package com.power.travel.xixuntravel.model;

import java.io.Serializable;

/**
 *
 * @author fan
 *
 */
@SuppressWarnings("serial")
public class DriverModel implements Serializable {

	private String id;//
	private String mid;//
	private String name;//
	private String sex;//
	private String worktime;//
	private String car_brand;//
	private String car_models;//
	private String car_age;//
	private String nation;//
	private String other;//
	private String status;
	private String username;//
	private String mobile ;//
	private String nickname;//
	private String face;
	private String age ;
	private String address;
	private String Apart;

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
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getMid() {
		return mid;
	}
	public void setMid(String mid) {
		this.mid = mid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getWorktime() {
		return worktime;
	}
	public void setWorktime(String worktime) {
		this.worktime = worktime;
	}
	public String getNation() {
		return nation;
	}
	public void setNation(String nation) {
		this.nation = nation;
	}
	public String getOther() {
		return other;
	}
	public void setOther(String other) {
		this.other = other;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getAge() {
		return age;
	}
	public void setAge(String age) {
		this.age = age;
	}
	public String getCar_brand() {
		return car_brand;
	}
	public void setCar_brand(String car_brand) {
		this.car_brand = car_brand;
	}
	public String getCar_models() {
		return car_models;
	}
	public void setCar_models(String car_models) {
		this.car_models = car_models;
	}
	public String getCar_age() {
		return car_age;
	}
	public void setCar_age(String car_age) {
		this.car_age = car_age;
	}
	public String getApart() {
		return Apart;
	}
	public void setApart(String apart) {
		Apart = apart;
	}
	
}
