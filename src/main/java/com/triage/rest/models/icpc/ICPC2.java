package com.triage.rest.models.icpc;

import java.util.ArrayList;

import com.triage.rest.enummodels.ICPC2Type;

public class ICPC2{
	private int id;
	private String name;
	private String description;
	private ICPC2Type type;
	private String code;
	private ArrayList<String> additionalDescriptions;
	private ArrayList<String> synonyms;
	private String wikiLink;

	public ICPC2(String name, String description, ICPC2Type type, String code) {
		this.name = name;
		this.description = description;
		this.type = type;
		this.code = code;
		this.additionalDescriptions = new ArrayList<String>();
		this.synonyms = new ArrayList<String>();
		this.wikiLink = null;
	}
	
	public ICPC2(int id){
		this.id = id;
	}
	
	public ICPC2(int id, String name, String description, ICPC2Type type, String code) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.type = type;
		this.code = code;
		this.additionalDescriptions = new ArrayList<String>();
		this.synonyms = new ArrayList<String>();
		this.wikiLink = null;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ICPC2Type getType() {
		return type;
	}

	public void setType(ICPC2Type type) {
		this.type = type;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public ArrayList<String> getAdditionalDescriptions() {
		return additionalDescriptions;
	}

	public void setAdditionalDescriptions(ArrayList<String> additionalDescriptions) {
		this.additionalDescriptions = additionalDescriptions;
	}
	
	public void addSingeAdditionalDescription(String additionalDescription) {
		if(this.additionalDescriptions != null)
			this.additionalDescriptions.add(additionalDescription);
	}

	public ArrayList<String> getSynonyms() {
		return synonyms;
	}

	public void setSynonyms(ArrayList<String> synonyms) {
		this.synonyms = synonyms;
	}
	
	public void addSingeSynonym(String synonym) {
		if(this.synonyms != null)
			this.synonyms.add(synonym);
	}

	public String getWikiLink() {
		return wikiLink;
	}

	public void setWikiLink(String wikiLink) {
		this.wikiLink = wikiLink;
	}

	@Override
	public String toString() {
		return "ICPC2 [id=" + id + ", name=" + name + ", description=" + description + ", type=" + type + ", code="
				+ code + ", additionalDescriptions=" + additionalDescriptions + ", synonyms=" + synonyms + ", wikiLink=" + wikiLink
				+ "]";
	}
}
