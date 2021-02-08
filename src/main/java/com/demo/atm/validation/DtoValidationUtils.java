package com.demo.atm.validation;

import org.springframework.util.StringUtils;

import com.demo.atm.enums.OPTIONALITY;
import com.demo.atm.exception.DataValidationException;

public class DtoValidationUtils {

	public static void validate(String value, OPTIONALITY optionality) {

		if (value != null) {
			if (optionality == OPTIONALITY.REQUIRED) {
				if (value.isEmpty())
					throw new DataValidationException("required value found as empty");
				if (value.trim().isEmpty())
					throw new DataValidationException("required value found as blank");

			}
		} else {
			throw new DataValidationException("Required value found as null");
		}
	}

}
