package com.demo.atm.tranformer;

import java.util.Map;

import com.demo.atm.entity.ATM;

public interface JsonResponseTransformService {

	public Map<ATM, String> fromResponsetoArray(String mainData);

}
