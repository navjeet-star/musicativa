package com.developer.musicatiiva.models;


import com.google.gson.annotations.SerializedName;


public class AddTimerResponse {

	@SerializedName("status_code")
	private String statusCode;

	@SerializedName("data")
	private Data data;

	@SerializedName("description")
	private String description;

	public void setStatusCode(String statusCode){
		this.statusCode = statusCode;
	}

	public String getStatusCode(){
		return statusCode;
	}

	public void setData(Data data){
		this.data = data;
	}

	public Data getData(){
		return data;
	}

	public void setDescription(String description){
		this.description = description;
	}

	public String getDescription(){
		return description;
	}

	@Override
 	public String toString(){
		return 
			"RegisterData{" + 
			"status_code = '" + statusCode + '\'' + 
			",data = '" + data + '\'' + 
			",description = '" + description + '\'' + 
			"}";
		}
}