import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.lucene.analysis.it.ItalianAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class InitializeOCRIndex {

	public static void main(String[] args) {
		IndexWriter indexWriter = null;
		try {
			Path path = Paths.get("C:/Users/Domenico/TriageBotRestServer-data/OCRIndex");
			path = Paths.get("C:/Users/frank/TriageBotRestServer-data/OCRIndex");
			Directory directory = FSDirectory.open(path);
	        
	        IndexWriterConfig config = new IndexWriterConfig(new ItalianAnalyzer());
            indexWriter = new IndexWriter(directory, config);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//Add dummy document
		try {
			Document ocrDoc = new Document();
            /*Field to not analyze*/
            ocrDoc.add(new StringField("fileID", "", Field.Store.YES));
            ocrDoc.add(new StringField("userID", "", Field.Store.YES));
            /*Field to analyze*/
            ocrDoc.add(new TextField("ocrText", "", Field.Store.YES));
			
            indexWriter.addDocument(ocrDoc);
            indexWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
}