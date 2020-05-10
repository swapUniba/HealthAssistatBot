package com.triage.logic;

import java.util.ArrayList;

import com.triage.rest.enummodels.ResultType;
import com.triage.rest.models.icpc.ICPC2;
import com.triage.rest.models.nlp.BabelNetSynset;
import com.triage.rest.models.users.SearchResult;

/**
 * Make a Medical Dictionary search
 * @author Domenico Vitarella
 *
 */
public class MedicalDictionarySearcher{
	private String text;
	
	private boolean lucene;
	private boolean babelnet;
	
	private SearchSymptomLucene searchLucene;
	private ArrayList<ICPC2> icpcResults;
	private ElaborateBabelnetSynsets searchBabelnet;
	private ArrayList<BabelNetSynset> babelNetResults;
	
	private ArrayList<SearchResult> results;
	private int nresults;
	
	public MedicalDictionarySearcher(String text){
		this.text = text;
		
		this.lucene = false;
		this.babelnet = false;
		
		this.searchLucene = new SearchSymptomLucene();
		this.icpcResults = new ArrayList<ICPC2>();
		this.searchBabelnet = new ElaborateBabelnetSynsets(this.text, new String[]{this.text});
		this.searchBabelnet.setAllMSynsets(true);
		this.babelNetResults = new ArrayList<BabelNetSynset>();
	}
	
	public void search(){
		ArrayList<ICPC2> icpcs = this.searchLucene.getICPC2sByName(this.text);
		//Se esiste un risultato che match col nome distanza lev 2 restituisci solo quello
		// altrimenti restituiscili tutti
		this.icpcResults = filterByExactMatchICPC(icpcs, text);
		
		if(icpcResults == null || icpcResults.size() == 0){
			this.searchBabelnet.findMedicalSynsets();
			ArrayList<BabelNetSynset> bnss = this.searchBabelnet.getDictTermSyn().get(this.text);
			ArrayList<BabelNetSynset> filteredBnss = filterByExactMatchBabelNet(bnss, text);
			ArrayList<BabelNetSynset> finalBnss = new ArrayList<BabelNetSynset>();
			for(BabelNetSynset bns : filteredBnss){
				if(bns.getGlosses().size() > 0){
					//Some synsets doesn't have description and this are not important
					//in medical dictionary application
					finalBnss.add(bns);
				}
			}
			this.babelNetResults = finalBnss;
						
			this.nresults = babelNetResults.size();
			if(this.nresults > 0)
				this.babelnet = true; this.lucene = false;
		}else{
			this.lucene = true; this.babelnet = false;
			this.nresults = icpcResults.size();
		}
	}

	public int getNresults() {
		return nresults;
	}

	public boolean isLucene() {
		return lucene;
	}

	public boolean isBabelnet() {
		return babelnet;
	}

	public ArrayList<ICPC2> getIcpcResults() {
		return icpcResults;
	}

	public ArrayList<BabelNetSynset> getBabelNetResults() {
		return babelNetResults;
	}
	
	public ArrayList<SearchResult> getResults(){
		if(this.results != null){
			return this.results;
		}else{
			ArrayList<SearchResult> results = new ArrayList<SearchResult>();
			if(this.lucene){
				for(ICPC2 icpc: this.icpcResults){
					SearchResult sr = new SearchResult(icpc.getName(), 
														icpc.getDescription(),
														ResultType.ICPC2);
					sr.setWikiLink(icpc.getWikiLink());
					results.add(sr);
				}
			}else if(this.babelnet){
				for(BabelNetSynset bns: this.babelNetResults){
					SearchResult sr = new SearchResult(bns.getMainSense(), 
														bns.getGlosses().get(0),
														ResultType.BABELNET);
					sr.setWikiLink(bns.getWikiLink());
					results.add(sr);
				}
			}
			
			this.results = results;
			return results;
		}
	}

	private static ArrayList<ICPC2> filterByExactMatchICPC(ArrayList<ICPC2> icpcs, String text){
		ArrayList<ICPC2> filtered = new ArrayList<ICPC2>();
		
		for(ICPC2 icpc : icpcs){
			//Cerca prima con exact matching
			if(text.equalsIgnoreCase(icpc.getName())){//Trovato con exact matching
				 filtered.add(icpc);
				 return filtered; 
			}
		}
		
		//Non trovato restituiscili tutti
		return icpcs;
	}
	
	private static ArrayList<BabelNetSynset> filterByExactMatchBabelNet(
													ArrayList<BabelNetSynset> bnss, String text){
		ArrayList<BabelNetSynset> filtered = new ArrayList<BabelNetSynset>();
		
		for(BabelNetSynset bns : bnss){
			//Cerca prima con exact matching
			if(text.equalsIgnoreCase(bns.getMainSense())){//Trovato con exact matching
				 filtered.add(bns);
				 return filtered; 
			}
		}
		
		//Non trovato restituiscili tutti
		return bnss;
	}
	
	@Override
	public String toString() {
		return "MedicalDictionarySearcher [text=" + text + ", lucene=" + lucene + ", babelnet=" + babelnet
				+ ", icpcResults=" + icpcResults + ", babelNetResults=" + babelNetResults + ", nresults=" + nresults
				+ "]";
	}
}
