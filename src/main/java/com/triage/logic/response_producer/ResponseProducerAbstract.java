package com.triage.logic.response_producer;

import com.triage.logic.questions.AbstractQuestions;
import com.triage.logic.questions.BaseBotQuestions;
import com.triage.rest.dao.CurrentQuestionDao;
import com.triage.rest.dao.MessageDao;
import com.triage.rest.dao.UserDao;
import com.triage.rest.models.messages.Answer;
import com.triage.rest.models.messages.Message;
import com.triage.rest.models.messages.Question;
import com.triage.rest.models.messages.ReplayMarkup;
import com.triage.rest.models.messages.Response;
import com.triage.rest.models.messages.RestRequestInput;
import com.triage.rest.models.users.User;
import com.triage.utils.ResponseButtonUtils;
import com.vdurmont.emoji.EmojiParser;

public abstract class ResponseProducerAbstract {
	private RestRequestInput restRequest;
	private int chatId;
	private String text;
	
	/* DAOs */
	private UserDao userdao;
	
	private MessageDao msgdao;
	private AbstractQuestions qstdao;
	private CurrentQuestionDao cqdao;
	
	/* Type button(Linear/Grid) */
	private boolean linearButton;
    private boolean gridButton;
    private int ncol;
    
    /* Link previews */
	private boolean showLinkPreview;

    
    /* Results */
    private User user;
    private Question lastq;
    private Question nextq;
    private Response response;
	
	public ResponseProducerAbstract(RestRequestInput restRequest, User user, Question lastq) {
		this.restRequest = restRequest;
		this.chatId = Integer.parseInt(restRequest.getChatId());
		this.text = restRequest.getText();
		this.user = user;
		this.lastq = lastq;
		
		/* DAOs */
		this.msgdao = new MessageDao();
		this.qstdao = new BaseBotQuestions();
		this.cqdao = new CurrentQuestionDao();
		this.userdao = new UserDao();

		/* Type button */
		this.linearButton = true;
		this.gridButton = false;
		this.ncol = 2;
		
		/* Link previews */
		this.showLinkPreview = true;

		/* Results */
		this.nextq = null;
		this.response = null;
	}
	
	public abstract Response produceResponse();
	
	protected Response createResponseObject(){
		//Start building Message(chat message) and Respose(json output) objects
		Message mresp = new Message(this.chatId, true, Long.parseLong(restRequest.getDate()),
									nextq.getQuestionText());
	    Response resp = new Response(this.chatId, nextq.getQuestionText());
		
	    Answer backMenu = new Answer("Menu " + EmojiParser.parseToUnicode(":back:"));
	    //Set answers
		if(nextq.getAnswers().size() != 0){
			if(nextq.getQuestionName() != BaseBotQuestions.MENU && nextq.isSetMenuButton())
				nextq.addSingleAnswer(backMenu);
			
			if(nextq.getAnswers().size() > 4)
				linearButton = false; gridButton = true;
			
			ReplayMarkup rm = null;
			if(linearButton){
				rm = new ReplayMarkup(ResponseButtonUtils.transformToLinearButton(nextq.getAnswers()), true);
			}else if(gridButton){
				rm = new ReplayMarkup(ResponseButtonUtils.transformToGridButton(nextq.getAnswers(), ncol), true);
			}
			resp.setReplaymarkup(rm);
		}else if(nextq.getAnswers().size() == 0){
			//se la domanda che poni non ha risposte da dare (tipo se devi mostrare solo un'immagine o un chart)
			if(nextq.isSetMenuButton()){
				nextq.addSingleAnswer(backMenu);
				ReplayMarkup rm = new ReplayMarkup(ResponseButtonUtils.transformToLinearButton(nextq.getAnswers()), true);
				resp.setReplaymarkup(rm);
			}
		}
		
		//Set preview
		if(this.showLinkPreview)
			resp.setShowLinkPreview(true);
		else
			resp.setShowLinkPreview(false);
		
		//Set images
		if(nextq.getImagesLink().size() != 0){
			resp.setImages(nextq.getImagesLink());
			if(nextq.getImagesSub() != null){
				resp.setImagesSub(nextq.getImagesSub());
			}
		}

		//set show chart
		if(nextq.isShowChart())
			resp.setShowChart(true);
		else
			resp.setShowChart(false);

		cqdao.saveCurrentQuestion(this.chatId, nextq.getQuestionName());
		msgdao.saveNewMessage(mresp);
		
		System.out.println("Complete response: " + resp);
		this.response = resp;
	    return resp;
	}

	public RestRequestInput getRestRequest() {
		return restRequest;
	}

	public void setRestRequest(RestRequestInput restRequest) {
		this.restRequest = restRequest;
	}

	public int getChatId() {
		return chatId;
	}

	public void setChatId(int chatId) {
		this.chatId = chatId;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public UserDao getUserdao() {
		return userdao;
	}

	public void setUserdao(UserDao userdao) {
		this.userdao = userdao;
	}

	public MessageDao getMsgdao() {
		return msgdao;
	}

	public void setMsgdao(MessageDao msgdao) {
		this.msgdao = msgdao;
	}

	public AbstractQuestions getQstdao() {
		return qstdao;
	}

	public void setQstdao(AbstractQuestions qstdao) {
		this.qstdao = qstdao;
	}

	public CurrentQuestionDao getCqdao() {
		return cqdao;
	}

	public void setCqdao(CurrentQuestionDao cqdao) {
		this.cqdao = cqdao;
	}

	public boolean isLinearButton() {
		return linearButton;
	}

	public void setLinearButton(boolean linearButton) {
		this.linearButton = linearButton;
	}

	public boolean isGridButton() {
		return gridButton;
	}

	public void setGridButton(boolean gridButton) {
		this.gridButton = gridButton;
	}

	public int getNcol() {
		return ncol;
	}

	public void setNcol(int ncol) {
		this.ncol = ncol;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Question getLastq() {
		return lastq;
	}

	public void setLastq(Question lastq) {
		this.lastq = lastq;
	}

	public Question getNextq() {
		return nextq;
	}

	public void setNextq(Question nextq) {
		this.nextq = nextq;
	}

	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
	}

	public boolean isShowLinkPreview() {
		return showLinkPreview;
	}

	public void setShowLinkPreview(boolean showLinkPreview) {
		this.showLinkPreview = showLinkPreview;
	}
}
