package com.demo.atm.dto;

public class ErrorDto {

	private int statusCode;
	private String type;
	private String descrpiption;

	public void setStatusCode(int value) {
		this.statusCode = value;
	}

	public void setType(String type) {
		this.type = type;

	}

	public void setDescription(String description) {
		this.descrpiption = description;

	}

	public int getStatusCode() {
		return statusCode;
	}

	public String getType() {
		return type;
	}

	public String getDescrpiption() {
		return descrpiption;
	}

}
