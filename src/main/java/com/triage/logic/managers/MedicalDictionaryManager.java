package com.triage.logic.managers;

import java.util.ArrayList;

import org.apache.commons.text.similarity.LevenshteinDistance;

import com.triage.logic.MedicalDictionarySearcher;
import com.triage.rest.dao.MedicalDictionaryDao;
import com.triage.rest.models.users.SearchResult;
import com.triage.rest.models.users.User;

public class MedicalDictionaryManager {
	private User user;
	private MedicalDictionaryDao mddao;
	
	public MedicalDictionaryManager(User user){
		this.user = user;
		this.mddao = new MedicalDictionaryDao();
	}
	
	/**
	 * Crea una nuova istanza di ricerca aggiungendo il testo di ricerca.
	 * In seguito effettua la ricerca e salva i risultati.
	*/
	public ArrayList<SearchResult> searchTerm(String text){
		this.mddao.createNewMedicalDictionary(this.user.getId(), text);
		MedicalDictionarySearcher mds = new MedicalDictionarySearcher(text);
		mds.search();
		
		if(mds.getNresults() > 0)
			this.mddao.saveSearchResults(this.user.getId(), mds.getResults());
		System.out.println("---DEBUG:" + mds.getResults());
		
		return mds.getResults();
	}
	
	private static int MAXIMUM_LEV_DISTANCE = 5;
	/**
	 * Se la ricerca produce più risultati ne seleziona uno utilizzando il nome.
	 * Cerca fra i risultati prima con exact matching e in seguito con distanza levhenstein.
	 */
	public SearchResult doYouMean(String text){
		ArrayList<SearchResult> ress = this.mddao.getSearchResults(this.user.getId());
		
		LevenshteinDistance ld = new LevenshteinDistance();
		SearchResult lowestDistanceResult = null;
		int lowestDistance = MAXIMUM_LEV_DISTANCE + 1;
		for(SearchResult res: ress){
			//Cerca prima con exact matching
			if(text.equalsIgnoreCase(res.getName()))
				return res;
			else{
				//Individua il risultato con distanza minima
				int dist = ld.apply(text, res.getName());
				if(dist <= MAXIMUM_LEV_DISTANCE && dist < lowestDistance){
					lowestDistanceResult = res;
					lowestDistance = dist;
				}
			}
		}
		
		if(lowestDistanceResult != null)
			return lowestDistanceResult;
		else
			return null;
	}
}
