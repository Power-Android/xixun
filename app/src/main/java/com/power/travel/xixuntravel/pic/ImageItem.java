package com.power.travel.xixuntravel.pic;

import java.io.Serializable;

/**
 * 图片对象
 * 
 * @author Administrator
 * 
 */
public class ImageItem implements Serializable {
	public String imageId;
	public String thumbnailPath;
	public String imagePath;
	public boolean isSelected = false;
}
