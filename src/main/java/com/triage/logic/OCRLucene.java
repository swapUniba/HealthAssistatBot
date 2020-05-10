package com.triage.logic;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.it.ItalianAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.NoLockFactory;

import com.triage.rest.models.messages.OCRText;

import java.io.*;


public class OCRLucene {
	//Singleton
	private static OCRLucene instance = null;
	public static OCRLucene getInstance() {
		if(instance == null) {
			instance = new OCRLucene();
		}
		return instance;
	}
	
	private IndexWriter indexWriter;
	//private IndexSearcher indexSearcher;
	private Directory directory;
	private OCRLucene(){
		try {
			Path path = Paths.get("C:/Users/frank/TriageBotRestServer-data/OCRIndex");
			if(!System.getProperty("os.name").equals("Windows 8.1"))
				path = Paths.get("/home/baccaro/TriageBotRestServer-data/OCRIndex");
			this.directory = FSDirectory.open(path, NoLockFactory.INSTANCE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void addOCRDocument(OCRText ocr, int userID){
		try {
			IndexWriterConfig config = new IndexWriterConfig(new ItalianAnalyzer());
	        this.indexWriter = new IndexWriter(directory, config);
			
			System.out.println("--DEBUG lucene" + ocr.toString());
			Document ocrDoc = new Document();
            /*Field to not analyze*/
            ocrDoc.add(new StringField("fileID", ocr.getPhotoID(), Field.Store.YES));
            ocrDoc.add(new StringField("userID", Integer.toString(userID), Field.Store.YES));
            /*Field to analyze*/
            ocrDoc.add(new TextField("ocrText", ocr.getText(), Field.Store.YES));
			
            this.indexWriter.addDocument(ocrDoc);
            this.indexWriter.commit();
            this.indexWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	/**
	 * Ricerca nell'indice.
	 * @param text - il testo da ricercare
	 * @return l'identificativo dell'immagine
	 */
	public int searchFirst(String text, int userID){
		/*ArrayList<String> idsResults = new ArrayList<String>();
		
		QueryParser qpOcrText = new QueryParser("ocrText", new ItalianAnalyzer());
        QueryParser qpUserID = new QueryParser("userID", new KeywordAnalyzer()); ///Query to filter on userID
		BooleanQuery bq;
		try {
			bq = new BooleanQuery.Builder()
						.add(qpOcrText.parse(text), BooleanClause.Occur.MUST)
						.add(qpUserID.parse(Integer.toString(userID)), BooleanClause.Occur.MUST)
						.build();
			TopDocs docs = indexSearcher.search(bq, 200);
			ScoreDoc[] hits = docs.scoreDocs;
			
			for(int i=0; i<hits.length; ++i) {
				int docId = hits[i].doc;
				Document d = indexSearcher.doc(docId);
	        	
				idsResults.add(d.get("fileID"));
			}
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		
		ArrayList<String> idsResults = search(text, userID);
		if(idsResults != null)
			return Integer.parseInt(idsResults.get(0));
		else
			return -1;
	}
	
	/**
	 * Ricerca nell'indice.
	 * @param text - il testo da ricercare
	 * @return l'identificativo dell'immagine
	 */
	public ArrayList<String> search(String text, int userID){
		IndexSearcher indexSearcher = null;
		try {
			IndexReader indexReader = DirectoryReader.open(directory);
	        indexSearcher = new IndexSearcher(indexReader);
		} catch (IOException e1) {
			e1.printStackTrace();
		};
		
		ArrayList<String> idsResults = new ArrayList<String>();
		
		QueryParser qpOcrText = new QueryParser("ocrText", new ItalianAnalyzer());
        QueryParser qpUserID = new QueryParser("userID", new KeywordAnalyzer()); ///Query to filter on userID
		BooleanQuery bq;
		try {
			bq = new BooleanQuery.Builder()
						.add(qpOcrText.parse(text), BooleanClause.Occur.MUST)
						.add(qpUserID.parse(Integer.toString(userID)), BooleanClause.Occur.MUST)
						.build();
			TopDocs docs = indexSearcher.search(bq, 200);
			ScoreDoc[] hits = docs.scoreDocs;
			System.out.println("---DEBUG lucene quey:" +text + ". user id:" + userID );
			System.out.println("---DEBUG lucene query ress:" + hits.length);
			
			for(int i=0; i<hits.length; ++i) {
				int docId = hits[i].doc;
				Document d = indexSearcher.doc(docId);
	        	
				idsResults.add(d.get("fileID"));
			}
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("---DEBUG lucene query ress:" + idsResults);
		
		if(idsResults.size() > 0)
			return idsResults;
		else
			return null;
	}
	
	public void closeIndex(){
        try {
			this.indexWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
