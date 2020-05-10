package com.triage.rest.models.nlp;

import java.util.ArrayList;
import java.util.HashMap;

public class BabelNetSynset{
	private String code;
	private String mainSense;
	private ArrayList<String> senses;
	private ArrayList<String> glosses;
	private HashMap<String, Double> domains;
	private String wikiLink;
	
	public BabelNetSynset(String code, String mainSense, ArrayList<String> senses, ArrayList<String> glosses,
			HashMap<String, Double> domains) {
		super();
		this.code = code;
		this.mainSense = mainSense;
		this.senses = senses;
		this.glosses = glosses;
		this.domains = domains;
		this.wikiLink = null;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMainSense() {
		return mainSense;
	}

	public void setMainSense(String mainSense) {
		this.mainSense = mainSense;
	}

	public ArrayList<String> getSenses() {
		return senses;
	}

	public void setSenses(ArrayList<String> senses) {
		this.senses = senses;
	}

	public ArrayList<String> getGlosses() {
		return glosses;
	}

	public void setGlosses(ArrayList<String> glosses) {
		this.glosses = glosses;
	}

	public HashMap<String, Double> getDomains() {
		return domains;
	}

	public void setDomains(HashMap<String, Double> domains) {
		this.domains = domains;
	}

	public String getWikiLink() {
		return wikiLink;
	}

	public void setWikiLink(String wikiLink) {
		this.wikiLink = wikiLink;
	}

	@Override
	public String toString() {
		return "BabelNetSynset [code=" + code + ", mainSense=" + mainSense + ", senses=" + senses + ", glosses="
				+ glosses + ", domains=" + domains + ", wikiLink=" + wikiLink + "]";
	}
}
