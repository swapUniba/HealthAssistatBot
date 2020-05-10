package com.triage.utils;

import java.util.ArrayList;

public class ArrayListUtils {
	
	/**
	 * Concatenate a string object of an array.
	 */
	public static String transformArraysToString(ArrayList<String> arr){
		StringBuffer sb = new StringBuffer();
		for(String s: arr)
			sb.append(s.trim() + " ");
		return sb.toString().trim();
	}
	
	/**
	 * Count the number of distinc string items.
	 */
	public static int countDistinct(ArrayList<String> arr){
		ArrayList<String> distinct = new ArrayList<String>();
		for(String s: arr){
			if(!distinct.contains(s))
				distinct.add(s);
		}
		
		return distinct.size();
	}
}
