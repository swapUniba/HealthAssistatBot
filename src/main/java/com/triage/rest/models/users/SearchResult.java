package com.triage.rest.models.users;

import com.triage.rest.enummodels.ResultType;

public class SearchResult {
	private int id;
	private String name;
	private String description;
	private ResultType searchType;
	private String wikiLink;
	
	public SearchResult(String name, String description, ResultType searchType) {
		this.name = name;
		this.description = description;
		this.searchType = searchType;
	}
	
	public SearchResult(int id, String name, String description, ResultType searchType) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.searchType = searchType;
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
	
	public ResultType getSearchType() {
		return searchType;
	}
	
	public void setSearchType(ResultType searchType) {
		this.searchType = searchType;
	}
	
	public String getWikiLink() {
		return wikiLink;
	}

	public void setWikiLink(String wikiLink) {
		this.wikiLink = wikiLink;
	}

	@Override
	public String toString() {
		return "SearchResult [id=" + id + ", name=" + name + ", description=" + description
				+ ", searchType=" + searchType + ", wikiLink=" + wikiLink + "]";
	}
}
