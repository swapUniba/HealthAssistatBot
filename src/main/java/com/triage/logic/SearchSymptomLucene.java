package com.triage.logic;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.apache.lucene.analysis.it.ItalianAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.triage.rest.enummodels.ICPC2Type;
import com.triage.rest.models.icpc.ICPC2;

import java.io.*;


public class SearchSymptomLucene {
	private IndexSearcher indexSearcher;
	
	public SearchSymptomLucene(){
		//Comment this try-catch block below before initializing index
		try {
			//C:\\Users\\Domenico or System.getProperty("user.home")
			Path path = Paths.get("C:/Users/frank/TriageBotRestServer-data/OCRIndex");
			path = Paths.get("/home/baccaro/TriageBotRestServer-data/ICPC2Index");
			Directory directory = FSDirectory.open(path);
	        IndexReader indexReader =  DirectoryReader.open(directory);
	        indexSearcher = new IndexSearcher(indexReader);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static final int SEARCH_BY_NAME_LIMIT = 5;
	/**
	 * Dato un nome ritorna i primi risultati di Lucene
	 * @param name
	 * @return
	 */
	public ArrayList<ICPC2> getICPC2sByName(String name){
		name = name.toLowerCase();
		LevenshteinDistance ld = new LevenshteinDistance();
		ArrayList<ICPC2> icpcs = new ArrayList<ICPC2>();
		
		QueryParser qpname = new QueryParser("name", new ItalianAnalyzer());
		BooleanQuery bq;
		try {
			bq = new BooleanQuery.Builder()
					.add(qpname.parse(name), BooleanClause.Occur.MUST)
					.build();
			TopDocs docs = indexSearcher.search(bq, SEARCH_BY_NAME_LIMIT);
			ScoreDoc[] hits = docs.scoreDocs;
			
			for(int i=0; i<hits.length; ++i) {
				int docId = hits[i].doc;
				Document d = indexSearcher.doc(docId);
	        	
				ICPC2 icpc = new ICPC2(Integer.parseInt(d.get("id")), d.get("name"), 
							d.get("description"), ICPC2Type.valueOf(d.get("type").toUpperCase()),
							d.get("code"));
				icpc.setWikiLink(d.get("wikiLink"));
	        	
	        	int score = ld.apply(name, icpc.getName().toLowerCase());
	        	if(icpc.getName().toLowerCase().contains(name) || score < 5)
		        	icpcs.add(icpc);
	        }
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return icpcs;
	}
	
	/**
	 * Dato un nome ritorna il primo risultato di Lucene
	 * @param name
	 * @return
	 */
	/*public ICPC2 getICPC2ByName(String name){
		ArrayList<ICPC2> icpcs = getICPC2sByName(name);
		
		if(icpcs == null) return null;
		return icpcs.get(0);
	}*/
}
