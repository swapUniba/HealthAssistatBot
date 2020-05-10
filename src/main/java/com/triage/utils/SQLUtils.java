package com.triage.utils;

public class SQLUtils {
	public static String putQuestionMarks(int n){
		StringBuffer sb = new StringBuffer();
		for(int i=0; i<n; i++){
			if(i==n-1)
				sb.append("?");
			else
				sb.append("?,");
		}
		
		return sb.toString();
	}
}
