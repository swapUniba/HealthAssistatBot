package com.triage.logic;

import io.github.firemaples.language.Language;
import io.github.firemaples.translate.Translate;

public class BingTranslate {
	private final static  String KEY = "0d11109447b84983a5a7720ecd128f76";
	
	public static String translateItaEng(String toTranslate){
		Translate.setSubscriptionKey(KEY);
	    
	    String translatedText = null;
		try {
			translatedText = Translate.execute(toTranslate, Language.ITALIAN, Language.ENGLISH);
		} catch (Exception e) {
			e.printStackTrace();
		}
	    return translatedText;
	}
}
