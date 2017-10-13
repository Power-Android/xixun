package com.power.travel.xixuntravel.model;

import java.io.Serializable;

/**
 *
 * 
 * @author fan
 * 
 */
@SuppressWarnings("serial")
public class MyTripModel implements Serializable {

	private String id;//
	private String is_at;//
	private String is_car;//
	private String is_carpool;//
	private String starttime;//
	private String endtime;//
	private String mid;//
	private String title;//
	private String info;//
	private String state;
	private String onclick;//
	private String arr_img;//
	private String addtime;//
	private String province;//
	private String city;//
	private String area;//
	private String address;//


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
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

	public String getIs_at() {
		return is_at;
	}

	public void setIs_at(String is_at) {
		this.is_at = is_at;
	}

	public String getIs_car() {
		return is_car;
	}

	public void setIs_car(String is_car) {
		this.is_car = is_car;
	}

	public String getIs_carpool() {
		return is_carpool;
	}

	public void setIs_carpool(String is_carpool) {
		this.is_carpool = is_carpool;
	}

	public String getStarttime() {
		return starttime;
	}

	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}

	public String getEndtime() {
		return endtime;
	}

	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getArr_img() {
		return arr_img;
	}

	public void setArr_img(String arr_img) {
		this.arr_img = arr_img;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}


}
