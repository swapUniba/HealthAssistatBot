package com.triage.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.triage.rest.models.users.JSONExam;
import com.triage.rest.models.users.SemanticSynonyms;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.text.similarity.LevenshteinDistance;

import com.triage.rest.models.icpc.ICPC2;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;

public class NLPUtils {
	//TODO NOT USED
	/**
	 * Stemming and remove stopwords using Lucene ItalianAnalyzer.
	 * 
	 * @param usermessage: the message to clean.
	 * @return array of term.
	 */
	/*public static String[] generateTerms(String text){
		Query query = null;
		String[] terms = null;
		
		//Parse terms (fake field 't')
		QueryParser qp = new QueryParser("t", new ItalianAnalyzer());
		try {
			query = qp.parse(text);
			String[] dirtyterms = query.toString().split(" ");
			terms = new String[dirtyterms.length];
			
			for(int i=0; i<dirtyterms.length; i++){
				if(!dirtyterms[i].equals(""))
					terms[i] = dirtyterms[i].trim().split(":")[1];
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return terms;
	}*/
	
	//TODO NOT USED
	/**
	 * Remove stopwords using Italian stopword.
	 * 
	 * @param usermessage: the message to clean.
	 * @return array of term.
	 */
	/*public static String[] generateTerms1(String text){
		Query query = null;
		String[] terms = null;
		
		//Parse terms
		CharArraySet itStopwords = new ItalianAnalyzer().getStopwordSet();
		//Fake field t
		QueryParser qp = new QueryParser("t", new StandardAnalyzer(itStopwords));
		try {
			query = qp.parse(text);
			String[] dirtyterms = query.toString().split(" ");
			terms = new String[dirtyterms.length];
			
			for(int i=0; i<dirtyterms.length; i++){
				if(!dirtyterms[i].equals(""))
					terms[i] = dirtyterms[i].trim().split(":")[1];
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return terms;
	}*/

	/**
	 * If you insert a date like: 191120103434243 it will return only 19/11/2010
	 * @param date
	 * @return
	 */
	public static String adjustDateFormat(Date date) {
		if (date != null){
			String date_format = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALIAN).format(date);
			String[] date_regexed = date_format.split("/");
			String year = date_regexed[2].substring(0, 4);
			return date_regexed[0] + "/" + date_regexed[1] + "/" + year;
		}
		return null;
	}

	public static String getDateWithAnotherFormat(Date date){
		String date_format = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALIAN).format(date);
		return date_format;
	}


	/**
	 * Given a text parse the first number
	 * 
	 * @param text: the input message
	 * @return a parsed number
	 */
	public static int parseNumber(String text){
		Pattern p = Pattern.compile("([0-9]+)");
	    Matcher m = p.matcher(text);
	    if (m.find()) {
	        return Integer.parseInt(m.group());
	    }
	    
	    return -1;
	}
	public static String parseNumberII(String text){
		Pattern p = Pattern.compile("\\d+");
		Matcher m = p.matcher(text);
		if (m.find()) {
			return m.group();
		}
		return null;
	}


	public static String parseYear(String text){
		Pattern pattern = Pattern.compile("^[12][0-9]{3}$");
		Matcher matcher = pattern.matcher(text);
		if (matcher.find())
		{
			return matcher.group();
		}
		return null;
	}
	public static ArrayList<ICPC2> filterByLevenshteinDistance(ArrayList<ICPC2> icpcs, 
																String text,
																int distance){
		ArrayList<ICPC2> filtered = new ArrayList<ICPC2>();
		LevenshteinDistance ld = new LevenshteinDistance();
		for(ICPC2 icpc: icpcs){
			int dist = ld.apply(text, icpc.getName());
			if(dist <= distance)
				filtered.add(icpc);
		}
		
		return filtered;
	}

    public static ArrayList<String> filterByLevenshteinDistance_exam(ArrayList<JSONExam> exams,
																	 String text){
        ArrayList<String> filtered = new ArrayList<String>();
        LevenshteinDistance ld = new LevenshteinDistance();
        int distance_min = 1000;
        for( JSONExam x: exams){
        	for(SemanticSynonyms l: x.getSemanticSynonyms()){
        		for(String s: l.getSynonyms()) {
					int dist = ld.apply(text, s);
					if (dist < distance_min) {
						distance_min=dist;
						//hai trovato un nuovo minimo, quindi bisogna resettare le parole trovate fino ad ora
						filtered= new ArrayList<String>();
						filtered.add(s);
					}
					else if(dist == distance_min){
						filtered.add(s);
					}
				}
			}
		}
        return filtered;
    }

	public static String cleanInputMessage(String text){
		return text.trim();
	}
	
	public static String cleanBabelnetSenses(String text){
		if (text == null || text.length() == 0) {
	        return text;
	    }
		
		String cleaned = text.trim().toLowerCase(); //To lowercase
		cleaned = StringUtils.capitalize(cleaned);	//First letter upper
		
		cleaned = cleaned.replaceAll("_", " ");		//Replace _
		
		return cleaned;
	}
	
	public static Date parseDate(String dateStr){
		ArrayList<SimpleDateFormat> datePatterns = new ArrayList<SimpleDateFormat>();
		datePatterns.add(new SimpleDateFormat("dd-MM-yy",Locale.ITALIAN));
		datePatterns.add(new SimpleDateFormat("dd/MM/yy",Locale.ITALIAN));
		datePatterns.add(new SimpleDateFormat("MM/yy",Locale.ITALIAN));
		datePatterns.add(new SimpleDateFormat("ddMMyy",Locale.ITALIAN));
		datePatterns.add(new SimpleDateFormat("dd-MM-yyyy",Locale.ITALIAN));
		datePatterns.add(new SimpleDateFormat("dd/MM/yyyy",Locale.ITALIAN));
		datePatterns.add(new SimpleDateFormat("ddMMyyyy",Locale.ITALIAN));
		for (SimpleDateFormat pattern : datePatterns) {
		    try {
		    	pattern.setLenient(false);
				return pattern.parse(dateStr);
			} catch (java.text.ParseException e) {
			}
		}

		return null;
	}
	
	public static Date parseHour(String hourStr){		
		ArrayList<SimpleDateFormat> datePatterns = new ArrayList<SimpleDateFormat>();
		datePatterns.add(new SimpleDateFormat("HH-mm"));
		datePatterns.add(new SimpleDateFormat("HH:mm"));
		datePatterns.add(new SimpleDateFormat("HH.mm"));
		datePatterns.add(new SimpleDateFormat("HH,mm"));
		datePatterns.add(new SimpleDateFormat("HHmm"));
		
		for (SimpleDateFormat pattern : datePatterns) {
		    try {
		    	pattern.setLenient(false);
				return pattern.parse(hourStr);
			} catch (java.text.ParseException e) {
			}
		}
		
		return null;
	}

	public static boolean isPassedDate(Date date){
		return date.before(new Date());
	}

	/**
	 * prende
	 * @param date1 una data secondo il formato gg/mm/yyyy
	 * @param date2 una data secondo il formato gg/mm/yyyy
	 * @return
	 */
	public static Period difference(Date date1, Date date2){
		//String[] date1parsed= date1.split("/");
		//String[] date2parsed = date2.split("/");
		//LocalDate date1ld = new LocalDate(Integer.valueOf(date1parsed[2]), Integer.valueOf(date1parsed[1]), Integer.valueOf(date1parsed[0]));
		//LocalDate date2ld = new LocalDate(Integer.valueOf(date2parsed[2]), Integer.valueOf(date2parsed[1]), Integer.valueOf(date2parsed[0]));
		Period period = new Period(new LocalDate(date1), new LocalDate(date2), PeriodType.yearMonthDay());
		// System.out.println("Y: " + period.getYears() + ", M: " + period.getMonths() +
		//       ", D: " + period.getDays());
		//return Period.between(date1ld, date2ld);
		return period;
		//  int years = diff.getYears();
		// int months = diff.getMonths();
		//  System.out.println("number of years: " + years);
		// System.out.println("number of months: " + months);
		//return diff;
	}

}
