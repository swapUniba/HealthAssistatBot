package com.triage.rest.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.triage.rest.models.nlp.BabelNetSynset;
import com.triage.utils.NLPUtils;

public class BabelNetDao {
	//private final String BABELNET_KEY = "30584dd4-4acc-4c2c-bf05-37962c2b0e2c"; //Original
	private final String BABELNET_KEY = "775ad3aa-a56f-4d09-a6b3-654e06b68ff1";
	private final String GETSYNSETIDS_URL = "http://babelnet.io/v4/getSynsetIds";
	private final String GETSYNSET_URL = "http://babelnet.io/v4/getSynset";
	private final String LANG = "IT";
	//private final String WORD_POS = "NOUN";
	
	public ArrayList<String> getSynsetIds(String word){
		Client client = Client.create();
		WebResource service = client
				.resource(UriBuilder.fromUri(GETSYNSETIDS_URL).build())
				.queryParam("word", word)
				.queryParam("langs", LANG)
				//.queryParam("pos", WORD_POS)
				.queryParam("key", BABELNET_KEY);
		String json = service.accept(MediaType.APPLICATION_JSON).get(String.class);
		System.out.println("Json result ids: " + json);
		return parseIds(json);
	}
	
	public BabelNetSynset getSynset(String synId){
		Client client = Client.create();
		WebResource service = client
				.resource(UriBuilder.fromUri(GETSYNSET_URL).build())
				.queryParam("id", synId)
				.queryParam("filterLangs", LANG)
				.queryParam("key", BABELNET_KEY);
		String json = service.accept(MediaType.APPLICATION_JSON).get(String.class);
		System.out.println("Json result syn: " + json);
		
		return parseSynset(json, synId);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private ArrayList<String> parseIds(String json){
		ArrayList<String> ids = new ArrayList<String>();
		
		ArrayList<Object> jsonRoot = new Gson().fromJson(json, ArrayList.class);
		for(Object s: jsonRoot){
			Map<String, String> map = (Map)s;
			if(map.get("pos").equals("VERB") || map.get("pos").equals("NOUN"))
				ids.add(map.get("id"));
		}
		
		return ids;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private BabelNetSynset parseSynset(String json, String synid){
		Map<String, Object> jsonRoot = new Gson().fromJson(json, Map.class);
		String mainSense = (String) jsonRoot.get("mainSense");
		String cleanedMainSense = NLPUtils.cleanBabelnetSenses(mainSense);
		
		ArrayList<String> senses = new ArrayList<String>();
		String wiki_link_sense = null;
		for(Object sense : (ArrayList)jsonRoot.get("senses")){
			Map<String, Object> mapSense = (Map)sense;
			String cleanedSense = NLPUtils.cleanBabelnetSenses((String) mapSense.get("simpleLemma"));
			senses.add(cleanedSense);
			
			if(((String) mapSense.get("source")).equalsIgnoreCase("WIKI") 
					&& wiki_link_sense == null){ //Save wikipedia link
				wiki_link_sense = (String) mapSense.get("simpleLemma");
			}
		}
		
		ArrayList<String> glosses = new ArrayList<String>();
		for(Object gloss : (ArrayList)jsonRoot.get("glosses")){
			Map<String, Object> mapGloss = (Map)gloss;
			glosses.add((String) mapGloss.get("gloss"));
		}
		
		HashMap<String, Double> domains = new HashMap<String,Double>();
		for(String key : ((Map<String,Double>)jsonRoot.get("domains")).keySet()){
			domains.put(key, (Double) ((Map)jsonRoot.get("domains")).get(key));
		}
		
		BabelNetSynset bns = new BabelNetSynset(synid, cleanedMainSense, senses, glosses, domains);
		if(wiki_link_sense != null)
			bns.setWikiLink("it.wikipedia.org/wiki/" + wiki_link_sense);
		return bns;
	}
}
