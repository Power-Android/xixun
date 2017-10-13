package com.power.travel.xixuntravel.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ViewsportModel implements Serializable {

	private String id;
	private String title;//
	private String thumb;//
	private String description;//
	private String star;//
	private String label;//
	private String Apart;//
	private String coordinate_x;//116
	private String coordinate_y;//39

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
