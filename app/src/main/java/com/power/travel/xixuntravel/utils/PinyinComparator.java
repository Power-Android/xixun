package com.power.travel.xixuntravel.utils;


import com.power.travel.xixuntravel.model.PhoneModel;

import java.util.Comparator;

public class PinyinComparator implements Comparator<PhoneModel> {

	public int compare(PhoneModel o1, PhoneModel o2) {
		if(o1.getSortLetters().equals("@") || o2.getSortLetters().equals("#")) {
			return -1;
		} else if(o1.getSortLetters().equals("#") || o2.getSortLetters().equals("@")) {
			return 1;
		} else {
			return o1.getSortLetters().compareTo(o2.getSortLetters());
		}
	}

}
