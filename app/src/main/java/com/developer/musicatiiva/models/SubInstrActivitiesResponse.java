package com.developer.musicatiiva.models;


import com.google.gson.annotations.SerializedName;

import java.util.List;


public class SubInstrActivitiesResponse {

	@SerializedName("status_code")
	private String statusCode;


	@SerializedName("description")
	private String description;

	@SerializedName("data")
	private List<AllData> data;

	public List<AllData> getData() {
		return data;
	}

	public void setData(List<AllData> data) {
		this.data = data;
	}

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


}