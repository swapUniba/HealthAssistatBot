package com.triage.rest.models.icpc;

public class ICPC2Score {
	private ICPC2 icpc;
	private float score;
	private int position;
	
	public ICPC2Score(ICPC2 icpc, float score, int position) {
		this.icpc = icpc;
		this.score = score;
		this.position = position;
	}
	
	public ICPC2 getIcpc() {
		return icpc;
	}
	
	public void setIcpc(ICPC2 icpc) {
		this.icpc = icpc;
	}
	
	public float getScore() {
		return score;
	}
	
	public void setScore(float score) {
		this.score = score;
	}
	
	public int getPosition() {
		return position;
	}
	
	public void setPosition(int position) {
		this.position = position;
	}
	
	@Override
	public String toString() {
		return "ICPC2Score [icpc=" + icpc + ", score=" + score + ", position=" + position + "]";
	}
}
