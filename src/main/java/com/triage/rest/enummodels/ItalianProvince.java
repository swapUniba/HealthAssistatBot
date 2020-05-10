package com.triage.rest.enummodels;

import java.util.ArrayList;
import java.util.HashMap;

import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.model.ExtractedResult;

public class ItalianProvince {
	
	private static ArrayList<String> provinces(){
		ArrayList<String> arr = new ArrayList<String>();
		arr.add("AG");
		arr.add("AL");
		arr.add("AN");
		arr.add("AO");
		arr.add("AR");
		arr.add("AP");
		arr.add("AT");
		arr.add("AV");
		arr.add("BA");
		arr.add("BT");
		arr.add("BL");
		arr.add("BN");
		arr.add("BG");
		arr.add("BI");
		arr.add("BO");
		arr.add("BZ");
		arr.add("BS");
		arr.add("BR");
		arr.add("CA");
		arr.add("CL");
		arr.add("CB");
		arr.add("CI");
		arr.add("CE");
		arr.add("CT");
		arr.add("CZ");
		arr.add("CH");
		arr.add("CO");
		arr.add("CS");
		arr.add("CR");
		arr.add("KR");
		arr.add("CN");
		arr.add("EN");
		arr.add("FM");
		arr.add("FE");
		arr.add("FI");
		arr.add("FG");
		arr.add("FC");
		arr.add("FR");
		arr.add("GE");
		arr.add("GO");
		arr.add("GR");
		arr.add("IM");
		arr.add("IS");
		arr.add("SP");
		arr.add("AQ");
		arr.add("LT");
		arr.add("LE");
		arr.add("LC");
		arr.add("LI");
		arr.add("LO");
		arr.add("LU");
		arr.add("MC");
		arr.add("MN");
		arr.add("MS");
		arr.add("MT");
		arr.add("ME");
		arr.add("MI");
		arr.add("MO");
		arr.add("MB");
		arr.add("NA");
		arr.add("NO");
		arr.add("NU");
		arr.add("OT");
		arr.add("OR");
		arr.add("PD");
		arr.add("PA");
		arr.add("PR");
		arr.add("PV");
		arr.add("PG");
		arr.add("PU");
		arr.add("PE");
		arr.add("PC");
		arr.add("PI");
		arr.add("PT");
		arr.add("PN");
		arr.add("PZ");
		arr.add("PO");
		arr.add("RG");
		arr.add("RA");
		arr.add("RC");
		arr.add("RE");
		arr.add("RI");
		arr.add("RN");
		arr.add("RM");
		arr.add("RO");
		arr.add("SA");
		arr.add("VS");
		arr.add("SS");
		arr.add("SV");
		arr.add("SI");
		arr.add("SR");
		arr.add("SO");
		arr.add("TA");
		arr.add("TE");
		arr.add("TR");
		arr.add("TO");
		arr.add("OG");
		arr.add("TP");
		arr.add("TN");
		arr.add("TV");
		arr.add("TS");
		arr.add("UD");
		arr.add("VA");
		arr.add("VE");
		arr.add("VB");
		arr.add("VC");
		arr.add("VR");
		arr.add("VV");
		arr.add("VI");
		arr.add("VT");
		
		return arr;
	}
	
	public static HashMap<String, String> mappingProvinces(){
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("Agrigento", "AG");
		map.put("Alessandria", "AL");
		map.put("Ancona", "AN");
		map.put("Aosta", "AO");
		map.put("Arezzo", "AR");
		map.put("Ascoli Piceno", "AP");
		map.put("Asti", "AT");
		map.put("Avellino", "AV");
		map.put("Bari", "BA");
		map.put("Barletta", "BT");
		map.put("Andria", "BT");
		map.put("Trani", "BT");
		map.put("Belluno", "BL");
		map.put("Benevento", "BN");
		map.put("Bergamo", "BG");
		map.put("Biella", "BI");
		map.put("Bologna", "BO");
		map.put("Bolzano", "BZ");
		map.put("Brescia", "BS");
		map.put("Brindisi", "BR");
		map.put("Cagliari", "CA");
		map.put("Caltanissetta", "CL");
		map.put("Campobasso", "CB");
		map.put("Carbonia", "CI");
		map.put("Iglesias", "CI");
		map.put("Caserta", "CE");
		map.put("Catania", "CT");
		map.put("Catanzaro", "CZ");
		map.put("Chieti", "CH");
		map.put("Como", "CO");
		map.put("Cosenza", "CS");
		map.put("Cremona", "CR");
		map.put("Crotone", "KR");
		map.put("Cuneo", "CN");
		map.put("Enna", "EN");
		map.put("Fermo", "FM");
		map.put("Ferrara", "FE");
		map.put("Firenze", "FI");
		map.put("Foggia", "FG");
		map.put("Forlì Cesena", "FC");
		map.put("Frosinone", "FR");
		map.put("Genova", "GE");
		map.put("Gorizia", "GO");
		map.put("Grosseto", "GR");
		map.put("Imperia", "IM");
		map.put("Isernia", "IS");
		map.put("La Spezia", "SP");
		map.put("L'Aquila", "AQ");
		map.put("Latina", "LT");
		map.put("Lecce", "LE");
		map.put("Lecco", "LC");
		map.put("Livorno", "LI");
		map.put("Lodi", "LO");
		map.put("Lucca", "LU");
		map.put("Macerata", "MC");
		map.put("Mantova", "MN");
		map.put("Massa Carrara", "MS");
		map.put("Matera", "MT");
		map.put("Messina", "ME");
		map.put("Milano", "MI");
		map.put("Modena", "MO");
		map.put("Monza e della Brianza", "MB");
		map.put("Napoli", "NA");
		map.put("Novara", "NO");
		map.put("Nuoro", "NU");
		map.put("Olbia Tempio", "OT");
		map.put("Oristano", "OR");
		map.put("Padova", "PD");
		map.put("Palermo", "PA");
		map.put("Parma", "PR");
		map.put("Pavia", "PV");
		map.put("Perugia", "PG");
		map.put("Pesaro e Urbino", "PU");
		map.put("Pescara", "PE");
		map.put("Piacenza", "PC");
		map.put("Pisa", "PI");
		map.put("Pistoia", "PT");
		map.put("Pordenone", "PN");
		map.put("Potenza", "PZ");
		map.put("Prato", "PO");
		map.put("Ragusa", "RG");
		map.put("Ravenna", "RA");
		map.put("Reggio Calabria", "RC");
		map.put("Reggio Emilia", "RE");
		map.put("Rieti", "RI");
		map.put("Rimini", "RN");
		map.put("Roma", "RM");
		map.put("Rovigo", "RO");
		map.put("Salerno", "SA");
		map.put("Medio Campidano", "VS");
		map.put("Sassari", "SS");
		map.put("Savona", "SV");
		map.put("Siena", "SI");
		map.put("Siracusa", "SR");
		map.put("Sondrio", "SO");
		map.put("Taranto", "TA");
		map.put("Teramo", "TE");
		map.put("Terni", "TR");
		map.put("Torino", "TO");
		map.put("Ogliastra", "OG");
		map.put("Trapani", "TP");
		map.put("Trento", "TN");
		map.put("Treviso", "TV");
		map.put("Trieste", "TS");
		map.put("Udine", "UD");
		map.put("Varese", "VA");
		map.put("Venezia", "VE");
		map.put("Verbano", "VB");
		map.put("Cusio", "VB");
		map.put("Ossola", "VB");
		map.put("Vercelli", "VC");
		map.put("Verona", "VR");
		map.put("Vibo Valentia", "VV");
		map.put("Vicenza", "VI");
		map.put("Viterbo", "VT");
		
		return map;
	}
	
	public static String getProvinceExact(String text){
		//Check directly on abreviation id
		for(String p : provinces()){
			if(text.equalsIgnoreCase(p))
				return p;
		}
		
		//Check on complete province name
		for(String s : mappingProvinces().keySet()){
			if(text.equalsIgnoreCase(s)){
				return mappingProvinces().get(s);
			}
		}
		
		return null;
	}
	
	public static String getProvinceFuzzy(String text){
		//Check on provinces name (using fuzzy search - lev dist)
		HashMap<String, String> mappingProv = mappingProvinces();
		ExtractedResult res = FuzzySearch.extractOne(text, mappingProv.keySet());
		if(res != null && res.getScore() > 85){
			return mappingProv.get(res.getString());
		}
		
		return null;
	}
}
