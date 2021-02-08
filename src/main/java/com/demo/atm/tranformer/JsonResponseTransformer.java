package com.demo.atm.tranformer;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.demo.atm.entity.ATM;
import com.google.gson.Gson;

@Component
public class JsonResponseTransformer implements JsonResponseTransformService {

	private static final Logger log = LoggerFactory.getLogger(JsonResponseTransformer.class);

	public Map<ATM,String> fromResponsetoArray(String mainResponse) {
		Map<ATM,String> atmCacheMap=new HashMap<>();
		ATM[] atmArray = null;
		if (mainResponse != null && !mainResponse.isEmpty()) {
			try {
				atmArray = new Gson().fromJson(mainResponse, ATM[].class);
				for (ATM atm : atmArray) {
					atmCacheMap.put(atm, atm.getAddress().getCity());
				}
				return atmCacheMap;

			} catch (Exception e) {
				log.error(e.getMessage());
				return null;
			}
		}
		return null;

	}

}
