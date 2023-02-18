package com.developer.musicatiiva.models.forgotPassword;


import com.google.gson.annotations.SerializedName;


public class ForgotPassword{

	@SerializedName("status_code")
	private String statusCode;

	@SerializedName("description")
	private String description;

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

	@Override
 	public String toString(){
		return 
			"ForgotPassword{" + 
			"status_code = '" + statusCode + '\'' + 
			",description = '" + description + '\'' + 
			"}";
		}
}