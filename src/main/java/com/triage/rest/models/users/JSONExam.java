package com.triage.rest.models.users;

import java.util.ArrayList;

public class JSONExam {
    private String category;
    private ArrayList<SemanticSynonyms> semanticSynonyms;

    public JSONExam(String category) {
        this.category = category;
    }

    public void setSemanticSynonyms(ArrayList<SemanticSynonyms> semanticSynonyms) {
        this.semanticSynonyms = semanticSynonyms;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public ArrayList<SemanticSynonyms> getSemanticSynonyms() {
        return semanticSynonyms;
    }
}
