package com.triage.utils;

import com.triage.rest.models.users.JSONExam;
import com.triage.rest.models.users.SemanticSynonyms;
import org.apache.commons.lang.ObjectUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;


public class JsonUtils {
    public JsonUtils() {
    }
    public  ArrayList<JSONExam> read_json_exams(){
         ArrayList<JSONExam> exams = new ArrayList<JSONExam>();
        try {
            InputStream is = this.getClass().getResourceAsStream("/com/triage/OCRexamsData/OCRexamsData.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            Object obj = new JSONParser().parse(reader);
            JSONArray exam_categories= (JSONArray) obj;
            for(Object category: exam_categories){
                exams.add(parseExamObject((JSONObject)category));
            }
        } catch (IOException | ParseException  | NullPointerException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        return exams;
    }
    private JSONExam parseExamObject(JSONObject ex){
        JSONExam j_exam= new JSONExam((String)ex.get("categoria"));
        ArrayList<SemanticSynonyms> sm = new ArrayList<SemanticSynonyms>();
       for (Object exam: (JSONArray)ex.get("esami")){
           JSONObject x = (JSONObject) exam;
           SemanticSynonyms l= new SemanticSynonyms((String)x.get("significato"));
           ArrayList<String> synonyms_java = new ArrayList<String>();
           JSONArray synonyms =  (JSONArray)x.get("sinonimi");
           //converto da jsonarray a array in java
           for (Object synonym: synonyms){
               synonyms_java.add((String)synonym);
           }
           l.setSynonyms(synonyms_java);
           sm.add(l);
       }
       j_exam.setSemanticSynonyms(sm);
       return j_exam;
    }
    public String get_semantic(String exam) {
        ArrayList<JSONExam> allExams = new JsonUtils().read_json_exams();
        //recupero la semantica associata al termine scelto cosi da poterlo ritrovare nel database
        String text = null;
        for (JSONExam j : allExams) {
            for (SemanticSynonyms s : j.getSemanticSynonyms()) {
                if (s.getSynonyms().contains(exam)) {
                    text = s.getSemantic();
                    break;
                }
            }
        }
        return text;
    }
}
