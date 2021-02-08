package com.demo.atm.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class HttpRequestServiceImpl implements HttpRequestService {

	private static final Logger log = LoggerFactory.getLogger(HttpRequestServiceImpl.class);

	@Override
	public  String getResponse(String URL) {

		try {
			String response_string = null;
			StringBuilder response = new StringBuilder();
			URL url = new URL(URL);
			HttpURLConnection httpconn = (HttpURLConnection) url.openConnection();

			log.info("processign url: " + URL);
			log.info("responded with the code: " + httpconn.getResponseCode());
			if (httpconn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				BufferedReader input = new BufferedReader(new InputStreamReader(httpconn.getInputStream()));
				String strLine = null;
				while ((strLine = input.readLine()) != null) {
					response.append(strLine);
				}
				input.close();
				response_string = response.toString();
			}

			httpconn.disconnect();

			return response_string;
		} catch (Exception e) {
			log.error(e.getMessage());
			return null;
		}

	}

}