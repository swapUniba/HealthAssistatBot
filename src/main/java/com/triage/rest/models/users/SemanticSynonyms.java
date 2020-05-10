package com.triage.rest.models.users;

import java.util.ArrayList;

public class SemanticSynonyms{
    private String semantic;
    private ArrayList<String> synonyms;

    public SemanticSynonyms(String semantic) {
        this.semantic = semantic;
    }

    public ArrayList<String> getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(ArrayList<String> synonyms) {
        this.synonyms = synonyms;
    }

    public String getSemantic() {
        return semantic;
    }

    public void setSemantic(String semantic) {
        this.semantic = semantic;
    }

    @Override
    public String toString() {
        return "SemanticSynonyms{" +
                "semantic='" + semantic + '\'' +
                ", synonyms=" + synonyms +
                '}';
    }
}
