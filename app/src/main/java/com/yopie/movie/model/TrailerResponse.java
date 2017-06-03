package com.yopie.movie.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TrailerResponse{

	@SerializedName("id")
	private int id;

	@SerializedName("results")
	private List<TrailerList> results;

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setResults(List<TrailerList> results){
		this.results = results;
	}

	public List<TrailerList> getResults(){
		return results;
	}

	@Override
 	public String toString(){
		return 
			"TrailerResponse{" + 
			"id = '" + id + '\'' + 
			",results = '" + results + '\'' + 
			"}";
		}
}