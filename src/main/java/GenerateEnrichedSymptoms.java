import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.triage.logic.ElaborateBabelnetSynsets;
import com.triage.logic.ICMVReaded;
import com.triage.rest.models.icpc.ICPC2;
import com.triage.rest.models.nlp.BabelNetSynset;

public class GenerateEnrichedSymptoms {
	
	private final static int MAX = 100015;
	private final static String INSPECT = "INSPECT";
	
	public static void main(String[] args) throws TransformerException {
		ICMVReaded symDao = new ICMVReaded();
		//Commentare una delle due righe sottostanti
		//ArrayList<ICPC2> syms = symDao.getAllSymptoms(); //Sintomi
		ArrayList<ICPC2> syms = symDao.getAllConditions(); //Patologie
		
		ArrayList<ICPC2> noResults = new ArrayList<ICPC2>();
		ArrayList<ICPC2> multipleResults = new ArrayList<ICPC2>();
		ArrayList<ICPC2> oneResults = new ArrayList<ICPC2>();
		HashMap<Integer, ArrayList<BabelNetSynset>> synsets = new HashMap<Integer, ArrayList<BabelNetSynset>>();
		HashMap<Integer, ArrayList<String>> synsetsLink = new HashMap<Integer, ArrayList<String>>();

		for(ICPC2 icpc: syms){
			if(icpc.getId() >= MAX)
				break;
			
			String correctName = icpc.getName().replaceAll("a'", "à").
												replaceAll("e'", "è").
												replaceAll("i'", "ì").
												replaceAll("o'", "ò").
												replaceAll("u'", "ù");
			ElaborateBabelnetSynsets ebs = new ElaborateBabelnetSynsets(correctName, new String[]{correctName});
			ebs.setAllMSynsets(true);
			ebs.findMedicalSynsets();
			
			ArrayList<BabelNetSynset> babelNetResults = ebs.getDictTermSyn().get(correctName);
			if(babelNetResults.size() == 0){
				noResults.add(icpc);
			}else if(babelNetResults.size() == 1){
				oneResults.add(icpc);
				synsets.put(icpc.getId(), babelNetResults);
				
				ArrayList<String> links = new ArrayList<String>();
				links.add(babelNetResults.get(0).getWikiLink());
				synsetsLink.put(icpc.getId(), links);
			}else if(babelNetResults.size() > 1){
				multipleResults.add(icpc);
				synsets.put(icpc.getId(), babelNetResults);
				
				ArrayList<String> links = new ArrayList<String>();
				for(BabelNetSynset bns : babelNetResults){
					links.add(bns.getWikiLink());
				}
				synsetsLink.put(icpc.getId(), links);
			}
		}
		
		System.out.println("--- No results ---");
		for(ICPC2 icpc: noResults){
			System.out.println(" - " + icpc);
		}
		
		System.out.println("--- Ready to merge ---");
		for(ICPC2 icpc: oneResults){
			System.out.println(" - Id:" + icpc.getId() + 
								", Name:" + icpc.getName() + 
								", Wiki link:" + synsetsLink.get(icpc.getId()) +
								", Synset:" + synsets.get(icpc.getId()));
		}
		
		System.out.println("--- Manual merge ---");
		for(ICPC2 icpc: multipleResults){
			System.out.println(" - Id:" + icpc.getId() + 
					", Name:" + icpc.getName() + 
					", Wiki link:" + synsetsLink.get(icpc.getId()) +
					", Synset:" + synsets.get(icpc.getId()));
		}
		
		
		//Create new xml file
		//Commentare una delle due righe sottostanti
		//Document doc = symDao.getDocSym(); //Sintomi
		Document doc = symDao.getDocCond(); //Patologie
		NodeList symsList = doc.getElementsByTagName("voce");
		//assert(symsList.getLength() == syms.size());
		for(int i=0; i<symsList.getLength(); i++){
			Node symNode = symsList.item(i);
			ICPC2 icpc = syms.get(i);
			if(icpc.getId() >= MAX)
				break;
			
			if (symNode.getNodeType() == Node.ELEMENT_NODE) {
				Element symElem = (Element) symNode;
				assert(symElem.getElementsByTagName("entry").item(0).getTextContent()
						.equalsIgnoreCase(icpc.getName()));
				
				// append a new id to voce
				Element idElem = doc.createElement("id");
				idElem.appendChild(doc.createTextNode(Integer.toString(i)));
				symNode.appendChild(idElem);
				
				if(!oneResults.contains(icpc))
					continue;
				//Only for automatic merge add glosses and syns
				
				// append glosses to voce
				Element glossesElem = doc.createElement("descrizioniAggiuntive");
				ArrayList<String> glosses = synsets.get(icpc.getId()).get(0).getGlosses();
				if(glosses.size() == 0 || glosses == null)
					glossesElem.appendChild(doc.createTextNode(INSPECT));
				else{
					for(String gloss : glosses){
						Element glossElem = doc.createElement("descrizione");
						glossElem.appendChild(doc.createTextNode(gloss));
						glossesElem.appendChild(glossElem);
					}
					
				}			
				symNode.appendChild(glossesElem);
				
				// append synonyms to voce //TODO put as sublist
				Element synsElem = doc.createElement("sinonimi");
				ArrayList<String> syns = removeDuplicate(synsets.get(icpc.getId()).get(0).getSenses());
				if(syns.size() == 0 || syns == null)
					synsElem.appendChild(doc.createTextNode(INSPECT));
				else{
					for(String syn : syns){
						Element synElem = doc.createElement("sinonimo");
						synElem.appendChild(doc.createTextNode(syn));
						synsElem.appendChild(synElem);
					}
				}
				symNode.appendChild(synsElem);
				
				// append a wikipedia link to voce
				Element wikiLinkElem = doc.createElement("wikiLink");
				if(synsetsLink.get(icpc.getId()).size() == 1){
					if(synsetsLink.get(icpc.getId()).get(0) == null)
						wikiLinkElem.appendChild(doc.createTextNode(INSPECT));
					else
						wikiLinkElem.appendChild(doc.createTextNode(synsetsLink.get(icpc.getId()).get(0)));
				}
				symNode.appendChild(wikiLinkElem);
				
				System.out.println("---Final---Id:" + i + 
											", Glosses:" + glosses + 
											", Syns:" + syns + 
											", Wiki:" + synsetsLink.get(icpc.getId()).get(0));
			}
		}
		
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		String filepath = "C:\\Users\\Domenico\\workspace\\TriageBotRestServer\\patologieEnriched.xml";
		StreamResult result = new StreamResult(new File(filepath));
		transformer.transform(source, result);

		System.out.println("Done");
	}
	
	private static ArrayList<String> removeDuplicate(ArrayList<String> arr){
		ArrayList<String> cleaned = new ArrayList<String>();
		
		for(String str: arr){
			if(!cleaned.contains(str))
				cleaned.add(str);
		}
		
		return cleaned;
	}
	
	private static String toStringArray(ArrayList<String> arr){
		String str = "";
		if(arr.size() == 0 || arr == null)
			return INSPECT;
		
		for(String s: arr){
			str += s + ". ";
		}
		str = str.trim();
		str = str.substring(0, str.length()-1); //remove last dot(.)
		
		return str;
	}
}
