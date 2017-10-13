package com.power.travel.xixuntravel.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ViewsportDetailModel implements Serializable {

	private String id;
	private String title;//
	private String thumb;//
	private String img;//
	private String description;//
	private String star;//
	private String label;//
	private String Apart;//
	private String city ;//
	private String address ;//
	private String content ;//
	private String array_img ;//
	private String addtime  ;//
	private String coordinate_x;
	private String coordinate_y;

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

	public String getThumb() {
		return thumb;
	}

	public void setThumb(String thumb) {
		this.thumb = thumb;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStar() {
		return star;
	}

	public void setStar(String star) {
		this.star = star;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getApart() {
		return Apart;
	}

	public void setApart(String apart) {
		Apart = apart;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getArray_img() {
		return array_img;
	}

	public void setArray_img(String array_img) {
		this.array_img = array_img;
	}

	public String getAddtime() {
		return addtime;
	}

	public void setAddtime(String addtime) {
		this.addtime = addtime;
	}

	public String getCoordinate_x() {
		return coordinate_x;
	}

	public void setCoordinate_x(String coordinate_x) {
		this.coordinate_x = coordinate_x;
	}

	public String getCoordinate_y() {
		return coordinate_y;
	}

	public void setCoordinate_y(String coordinate_y) {
		this.coordinate_y = coordinate_y;
	}

}
