package com.developer.musicatiiva.models;

import com.google.gson.annotations.SerializedName;


public class Data{

	@SerializedName("id")
	private int id;

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	@Override
 	public String toString(){
		return 
			"Data{" + 
			"id = '" + id + '\'' + 
			"}";
		}
}