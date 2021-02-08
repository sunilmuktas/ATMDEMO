package com.demo.atm.entity;

import java.util.List;

public class ATM {

	private Address address;
	private Integer distance;
	private List<OpeningHours> openingHours;
	private String functionality;
	private String type;

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public Integer getDistance() {
		return distance;
	}

	public void setDistance(Integer distance) {
		this.distance = distance;
	}

	public List<OpeningHours> getOpeningHours() {
		return openingHours;
	}

	public void setOpeningHours(List<OpeningHours> openingHours) {
		this.openingHours = openingHours;
	}

	public String getFunctionality() {
		return functionality;
	}

	public void setFunctionality(String functionality) {
		this.functionality = functionality;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "ATM [address=" + address + ", distance=" + distance + ", openingHours=" + openingHours
				+ ", functionality=" + functionality + ", type=" + type + "]";
	}

}
