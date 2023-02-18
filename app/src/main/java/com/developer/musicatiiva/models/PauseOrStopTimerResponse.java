package com.developer.musicatiiva.models;


import com.google.gson.annotations.SerializedName;


public class PauseOrStopTimerResponse {

	@SerializedName("status_code")
	private String statusCode;


	@SerializedName("description")
	private String description;

	private AnalysisData analysisData=null;


	public void setStatusCode(String statusCode){
		this.statusCode = statusCode;
	}

	public String getStatusCode(){
		return statusCode;
	}



	public void setDescription(String description){
		this.description = description;
	}

	public String getDescription(){
		return description;
	}

	public AnalysisData getAnalysisData() {
		return analysisData;
	}

	public void setAnalysisData(AnalysisData analysisData) {
		this.analysisData = analysisData;
	}
}