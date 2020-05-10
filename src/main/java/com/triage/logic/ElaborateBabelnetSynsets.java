package com.triage.logic;

import java.util.ArrayList;
import java.util.HashMap;

import com.triage.rest.dao.BabelNetDao;
import com.triage.rest.models.nlp.BabelNetSynset;

public class ElaborateBabelnetSynsets {
	/** Max number of syns to add in the user message.*/
	private final int MAX_NUMBER_SYN = 3;
	/** Max number of glosses to add in the user message.*/
	//private final int MAX_NUMBER_GLOSSES = 1;
	/** Name of medical domain */
	private final String MEDICAL_DOMAIN_NAME = "HEALTH_AND_MEDICINE";
	
	private BabelNetDao bndao;
	
	private String text;
	private String[] terms;
	private HashMap<String, ArrayList<BabelNetSynset>> dictTermSyn;
	private ArrayList<String> synonyms;
	private ArrayList<String> glosses;
	private boolean allMSynsets;
	
	public ElaborateBabelnetSynsets(String text, String[] terms){
		bndao = new BabelNetDao();
		
		this.text = text;
		this.terms = terms;
		this.dictTermSyn = new HashMap<String, ArrayList<BabelNetSynset>>();
		this.synonyms = null;
		this.glosses = null;
		this.allMSynsets = false;
		//findMedicalSyns();
	}

	/**
	 * Find all synsets of every terms.
	 *
	 */
	public void findMedicalSynsets(){
		for(int i=0; i<this.terms.length; i++){
			ArrayList<String> ids = bndao.getSynsetIds(this.terms[i]);
			ArrayList<BabelNetSynset> synsets = new ArrayList<BabelNetSynset>();
			
			for(String id: ids){
				BabelNetSynset bns = bndao.getSynset(id);
				if(bns.getDomains().containsKey(MEDICAL_DOMAIN_NAME)){
					//if(bns.getDomains().get(MEDICAL_DOMAIN_NAME) > 0){
						synsets.add(bns);
						if(!allMSynsets)
							break;
				}
			}
			this.dictTermSyn.put(this.terms[i], synsets);
		}
	}
	
	public ArrayList<String> getSynonyms(){
		if(this.synonyms == null){
			ArrayList<String> syns = new ArrayList<String>();
			for(String key: dictTermSyn.keySet()){
				if(dictTermSyn.get(key).size() == 0)
					continue;
				BabelNetSynset bns = dictTermSyn.get(key).get(0); //Take only first medical synset
				//Take first three for each term
				int ninserted = 0;
				for(String sense: bns.getSenses()){
					//String nsense = sense.replace("_", " ");
					if(ninserted >= MAX_NUMBER_SYN) break;
					if(this.text.contains(sense)) continue;
					if(!syns.contains(sense)){
						syns.add(sense);
						ninserted++;
					}
				}
			}
			this.synonyms = syns;
		}
		
		return this.synonyms;
	}
	
	public ArrayList<String> getGlosses() {
		if(this.glosses == null){
			ArrayList<String> glosss = new ArrayList<String>();
			for(String key: dictTermSyn.keySet()){
				BabelNetSynset bns = dictTermSyn.get(key).get(0); //Take only first medical synset
				if(!glosss.contains(bns.getGlosses().get(0)) && 
					bns.getGlosses().size()> 0)
					glosss.add(bns.getGlosses().get(0));
			}
			this.glosses = glosss;
		}
		
		return this.glosses;
	}
	
	public void setAllMSynsets(boolean allMSynsets) {
		this.allMSynsets = allMSynsets;
	}

	public HashMap<String, ArrayList<BabelNetSynset>> getDictTermSyn() {
		return dictTermSyn;
	}

	public void setTerms(String[] terms) {
		this.terms = terms;
	}
}
