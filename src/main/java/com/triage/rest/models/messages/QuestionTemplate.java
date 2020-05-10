package com.triage.rest.models.messages;

import java.io.StringWriter;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;

import com.triage.utils.VelocityInstance;

public class QuestionTemplate extends Question{
	private String templateName;
	private Template template;
	

	public QuestionTemplate(String questionName, String templateName) {
		super(questionName);
		this.templateName = templateName;
		this.template = VelocityInstance.getVelocityEngine().getTemplate(this.templateName, "Windows-1254");//Windows-1254
	}

	public void setVelocityContex(VelocityContext context){
		StringWriter writer = new StringWriter();
        this.template.merge(context, writer);
        this.setText(writer.toString());//this.text = writer.toString();
	}
	
	/*public String getQuestionText(){
		return this.text;
	}*/

}
