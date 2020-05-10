package com.triage.rest.models.messages;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@XmlRootElement
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class Response {
	private int chatId;
	private String response;
	private ReplayMarkup replaymarkup;
	private ArrayList<String> images;
	private ArrayList<String> imagesSub;
	private boolean showLinkPreview;
	private boolean showChart;
	
	public Response(){}
	public Response(int chatId, String response) {
		this.chatId = chatId;
		this.response = response;
		this.showLinkPreview = true;
	}

	public boolean isShowChart() {
		return showChart;
	}

	public void setShowChart(boolean showChart) {
		this.showChart = showChart;
	}

	public int getChatId() {
		return chatId;
	}

	public void setChatId(int chatId) {
		this.chatId = chatId;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public ReplayMarkup getReplaymarkup() {
		return replaymarkup;
	}
	
	public void setReplaymarkup(ReplayMarkup replaymarkup) {
		this.replaymarkup = replaymarkup;
	}
	
	public ArrayList<String> getImages() {
		return images;
	}
	
	public void setImages(ArrayList<String> images) {
		this.images = images;
	}
	
	public ArrayList<String> getImagesSub() {
		return imagesSub;
	}
	public void setImagesSub(ArrayList<String> imagesSub) {
		this.imagesSub = imagesSub;
	}
	public boolean isShowLinkPreview() {
		return showLinkPreview;
	}
	public void setShowLinkPreview(boolean showLinkPreview) {
		this.showLinkPreview = showLinkPreview;
	}
	
	@Override
	public String toString() {
		return "Response [chatId=" + chatId + ", response=" + response + ", replaymarkup=" + replaymarkup + ", images="
				+ images + ", imagesSub=" + imagesSub + ", showLinkPreview=" + showLinkPreview + "]";
	}
}
