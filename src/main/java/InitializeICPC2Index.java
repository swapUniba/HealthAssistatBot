import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.apache.lucene.analysis.it.ItalianAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.triage.logic.ICMVReaded;
import com.triage.rest.models.icpc.ICPC2;
import com.triage.utils.ArrayListUtils;

public class InitializeICPC2Index {

	public static void main(String[] args) {
		/*ArrayList<ICPC2> syms = new SymptomDaoXML().getAllSymptoms();
		
		for(ICPC2 sym: syms){
			System.out.println(sym.toString());
		}*/
		
		/*SearchSymptomLucene sdl = new SearchSymptomLucene();
		sdl.createIndex();
		
		ArrayList<ICPC2Score> ress = sdl.detectICPC2("emicrania", ICPC2Type.SYMPTOM, new ArrayList<String>());
		
		for(ICPC2Score res : ress){
			System.out.println(res.getIcpc());
		}*/
		
		ArrayList<ICPC2> syms = new ICMVReaded().getAllSymptoms();
		ArrayList<ICPC2> conds = new ICMVReaded().getAllConditions();
		ArrayList<ICPC2> icpcs = new ArrayList<ICPC2>();
		icpcs.addAll(syms);
		icpcs.addAll(conds);
		
		//Create index
        try {
        	Path path = Paths.get(System.getProperty("user.home") + File.separator + 
					"TriageBotRestServer-data" + File.separator + "ICPC2Index"); //C:\\TriageLucene\\SymptomIndex
			Directory dir = FSDirectory.open(path);

            IndexWriterConfig config = new IndexWriterConfig(new ItalianAnalyzer());
            IndexWriter writer = new IndexWriter(dir, config);
            
            for(ICPC2 icpc : icpcs){
                Document doc = new Document();
                
                /* Identifier field */
                //doc.add(new IntPoint("id", icpc.getId()));
                doc.add(new IntField("id", icpc.getId(), Field.Store.YES));
                //doc.add(new StoredField("id", icpc.getId()));
               
                /*Field to analyze*/
                doc.add(new TextField("name", icpc.getName(), Field.Store.YES));
                doc.add(new TextField("description", icpc.getDescription(), Field.Store.YES));
                
                if(icpc.getAdditionalDescriptions() != null){
                	doc.add(new TextField("additionalDescriptions", 
        					ArrayListUtils.transformArraysToString(icpc.getAdditionalDescriptions()), 
							Field.Store.YES)
                			);
                }
                
                if(icpc.getSynonyms() != null){
                	doc.add(new TextField("synonyms", 
                			ArrayListUtils.transformArraysToString(icpc.getSynonyms()), 
        					Field.Store.YES)
                			);
                }
                
                /*Field to not analyze*/
                doc.add(new StringField("type", icpc.getType().toString(), Field.Store.YES));
                doc.add(new StringField("code", icpc.getCode(), Field.Store.YES));
                if(icpc.getWikiLink() != null)
                	doc.add(new StringField("wikiLink", icpc.getWikiLink(), Field.Store.YES));
                
                writer.addDocument(doc);
            }

            writer.close();
            dir.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

}
