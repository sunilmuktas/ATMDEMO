package com.demo.atm.exception;

public class ATMDataNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ATMDataNotFoundException(String message) {
		super(message);
	}

}
