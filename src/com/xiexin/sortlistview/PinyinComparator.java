package com.xiexin.sortlistview;

import java.util.Comparator;

import com.xiexin.ces.entry.Employee;

/**
 * 
 * @author allenduan
 *
 */
public class PinyinComparator implements Comparator<Employee> {

	public int compare(Employee o1, Employee o2) {
		if (o1.getSortLetters().equals("@")
				|| o2.getSortLetters().equals("#")) {
			return -1;
		} else if (o1.getSortLetters().equals("#")
				|| o2.getSortLetters().equals("@")) {
			return 1;
		} else {
			return o1.getSortLetters().compareTo(o2.getSortLetters());
		}
	}

}
