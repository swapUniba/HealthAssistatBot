package com.triage.logic;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.triage.rest.enummodels.ICPC2Type;
import com.triage.rest.models.icpc.ICPC2;

public class ICMVReaded{
	private Document docSym;
	private Document docCond;
	
	public ICMVReaded(){
		try {
			//InputStream is = SymptomDaoXML.class.getResourceAsStream("/sintomiEnriched.xml");
			InputStream is = getClass().getResourceAsStream("/com/triage/ICPC2Data/sintomiEnriched.xml");
			System.out.println(is);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			//this.docSym = builder.parse(xmlfile);
			this.docSym = builder.parse(is);
			
			is = getClass().getResourceAsStream("/com/triage/ICPC2Data/patologieEnriched.xml");
			factory = DocumentBuilderFactory.newInstance();
			builder = factory.newDocumentBuilder();
			this.docCond = builder.parse(is);
			
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public ArrayList<ICPC2> getAllSymptoms(){
		ArrayList<ICPC2> syms = new ArrayList<ICPC2>();
		
		NodeList sList = docSym.getElementsByTagName("voce");
		for(int i=0; i<sList.getLength(); i++){
			Node sNode = sList.item(i);
			if (sNode.getNodeType() == Node.ELEMENT_NODE) {
				Element es = (Element) sNode;
				ICPC2 sym = new ICPC2(
							Integer.parseInt(es.getElementsByTagName("id").item(0).getTextContent()), 
							es.getElementsByTagName("entry").item(0).getTextContent(),
							es.getElementsByTagName("descrizione").item(0).getTextContent(), 
							ICPC2Type.SYMPTOM,  
							es.getElementsByTagName("capitolo").item(0).getTextContent());
				
				//Add additional description
				if(es.getElementsByTagName("descrizioniAggiuntive").getLength() > 0){
					//Iterate on 'descrizioneAggiuntive' child node
					Node descsAggNode = es.getElementsByTagName("descrizioniAggiuntive").item(0);
					NodeList descsAggList = descsAggNode.getChildNodes();
					for(int j=0; j<descsAggList.getLength(); j++){
						Node descNode = descsAggList.item(j);
						if(descNode.getNodeName().equalsIgnoreCase("descrizione")){
							sym.addSingeAdditionalDescription(descNode.getTextContent());
						}
					}
				}
					
				//Add synonyms	
				if(es.getElementsByTagName("sinonimi").getLength() > 0){
					Node synsNode = es.getElementsByTagName("sinonimi").item(0);
					NodeList synsList = synsNode.getChildNodes();
					for(int j=0; j<synsList.getLength(); j++){
						Node synNode = synsList.item(j);
						if(synNode.getNodeName().equalsIgnoreCase("sinonimo")){
							sym.addSingeSynonym(synNode.getTextContent());
						}
					}
				}
				
				if(es.getElementsByTagName("wikiLink").getLength() > 0)
					sym.setWikiLink(es.getElementsByTagName("wikiLink").item(0).getTextContent());
				
				syms.add(sym);
			}
		}
		
		return syms;
	}
	
	public ArrayList<ICPC2> getAllConditions(){
		ArrayList<ICPC2> conds = new ArrayList<ICPC2>();
		
		NodeList sList = docCond.getElementsByTagName("voce");
		for(int i=0; i<sList.getLength(); i++){
			Node sNode = sList.item(i);
			if (sNode.getNodeType() == Node.ELEMENT_NODE) {
				Element es = (Element) sNode;
				//TODO remove i, add link glosses, syms ecc
				ICPC2 cond = new ICPC2(
						Integer.parseInt(es.getElementsByTagName("id").item(0).getTextContent()), 
						es.getElementsByTagName("entry").item(0).getTextContent(),
						es.getElementsByTagName("descrizione").item(0).getTextContent(), 
						ICPC2Type.CONDITION,  
						es.getElementsByTagName("capitolo").item(0).getTextContent());
				
				//Add additional description
				if(es.getElementsByTagName("descrizioniAggiuntive").getLength() > 0){
					//Iterate on 'descrizioneAggiuntive' child node
					Node descsAggNode = es.getElementsByTagName("descrizioniAggiuntive").item(0);
					NodeList descsAggList = descsAggNode.getChildNodes();
					for(int j=0; j<descsAggList.getLength(); j++){
						Node descNode = descsAggList.item(j);
						if(descNode.getNodeName().equalsIgnoreCase("descrizione")){
							cond.addSingeAdditionalDescription(descNode.getTextContent());
						}
					}
				}
					
				//Add synonyms	
				if(es.getElementsByTagName("sinonimi").getLength() > 0){
					Node synsNode = es.getElementsByTagName("sinonimi").item(0);
					NodeList synsList = synsNode.getChildNodes();
					for(int j=0; j<synsList.getLength(); j++){
						Node synNode = synsList.item(j);
						if(synNode.getNodeName().equalsIgnoreCase("sinonimo")){
							cond.addSingeSynonym(synNode.getTextContent());
						}
					}
				}
				
				if(es.getElementsByTagName("wikiLink").getLength() > 0)
					cond.setWikiLink(es.getElementsByTagName("wikiLink").item(0).getTextContent());
				
				conds.add(cond);
			}
		}
		
		return conds;
	}

	public ICPC2 getICPC2ById(int icpcId){
		for(ICPC2 sym : this.getAllSymptoms()){
			if(sym.getId() == icpcId)
				return sym;
		}
		for(ICPC2 cond : this.getAllConditions()){
			if(cond.getId() == icpcId)
				return cond;
		}
		
		return null;
	}
	
	public Document getDocSym() {
		return docSym;
	}

	public Document getDocCond() {
		return docCond;
	}

}
