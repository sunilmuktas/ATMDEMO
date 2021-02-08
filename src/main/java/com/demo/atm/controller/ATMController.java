package com.demo.atm.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import com.demo.atm.dto.ErrorDto;
import com.demo.atm.entity.ATM;
import com.demo.atm.enums.OPTIONALITY;
import com.demo.atm.exception.ATMDataNotFoundException;
import com.demo.atm.exception.DataValidationException;
import com.demo.atm.http.HttpRequestService;
import com.demo.atm.tranformer.JsonResponseTransformService;
import com.demo.atm.validation.DtoValidationUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/atm")
@Api(description = "This end point is mainly used to do the Operations on ATMs")
public class ATMController {

	private static final Logger log = LoggerFactory.getLogger(ATMController.class);
	String URL;

	Map<ATM, String> atmCacheMap = new HashMap<>();
	@Autowired
	JsonResponseTransformService service;
	@Autowired
	HttpRequestService httpRequestService;

	public ATMController(@Value("${atm.request.url}") String URL, JsonResponseTransformService service,
			HttpRequestService httpRequestService) {
		this.URL = URL;
		this.service = service;
		this.httpRequestService = httpRequestService;
	}

	@PostConstruct
	public void postConstruct() {
		String response = httpRequestService.getResponse(URL);
		String mainResponse = response.substring(5, response.length());
		atmCacheMap = service.fromResponsetoArray(mainResponse);

		log.info("caching completed");

	}

	@ApiOperation(value = "This  end point is used to find all the ATMS which are available in the URL", produces = "application/json")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "The atm list is retrieved successfully"),
			@ApiResponse(code = 204, message = "No Content found") })
	@GetMapping(value = "/list")
	@Cacheable("/atms")

	public ResponseEntity<?> getAllATMS() {
		if (atmCacheMap == null || atmCacheMap.isEmpty()) {
			log.warn("The deafault cache Values are not found or missing");
			postConstruct();
		}

		List<ATM> atmData = atmCacheMap.keySet().stream().collect(Collectors.toList());

		if (atmData == null || atmData.isEmpty()) {
			log.error("ATM Data not found");
			throw new ATMDataNotFoundException("No ATM data Found");
		}
//		Collections.reverse(atmData);
		return ResponseEntity.ok(atmData);
	}

	@ApiOperation(value = "This  end point is used to find all the ATMS based on the city name", produces = "application/json")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "The atm list is retrieved  based on the city name successfully"),
			@ApiResponse(code = 204, message = "No Content found"),
			@ApiResponse(code = 400, message = "The value you have entered is invalid") })

	@GetMapping(value = "/byCity/{city}")
	@Cacheable("atmsByCity")

	public ResponseEntity<?> getATMSByCity(
			@ApiParam(name = "city", value = "it is used to filter the listofAtmsBy city") @NotNull @PathVariable String city) {
		if (atmCacheMap == null || atmCacheMap.isEmpty()) {
			log.warn("The deafault cache Values are not found or missing");
			postConstruct();
		}

		DtoValidationUtils.validate(city, OPTIONALITY.REQUIRED);

		List<ATM> atmsList = new ArrayList<>();
		atmCacheMap.forEach((key, value) -> {
			if (value.equalsIgnoreCase(city))
				atmsList.add(key);

		});
		if (atmsList != null && !atmsList.isEmpty())
			return ResponseEntity.ok(atmsList);
		log.debug("No data found for the city" + city);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("list/CacheEvict")
	@CacheEvict("/atms")
	public String listAtmsCacheEvict() {
		log.debug("Evacuation of list all atms is completed");
		return "Atms Cache list cleared Successfully";
	}

	@GetMapping("byCity/CacheEvict")
	@CacheEvict("atmsByCity")
	public String AtmsByCityCacheEvict() {
		log.debug("Evacuation of list of atms by city is completed");

		return "Atms Cache data by city  cleared Successfully";
	}

	// Exception Handlers

	@ExceptionHandler(ATMDataNotFoundException.class)
	public ResponseEntity<ErrorDto> dataNotFoundExceptionHandler(WebRequest httpRequest,
			ATMDataNotFoundException atmNotFoundException) {

		return handle(atmNotFoundException, HttpStatus.NO_CONTENT, getRequestUri(httpRequest));

	}

	@ExceptionHandler(DataValidationException.class)
	public ResponseEntity<ErrorDto> dataValidationExceptionHandler(WebRequest httpRequest,
			DataValidationException dataValidationException) {

		return handle(dataValidationException, HttpStatus.BAD_REQUEST, getRequestUri(httpRequest));

	}

	private String getRequestUri(WebRequest request) {
		String uri = null;
		if (request instanceof ServletWebRequest) {
			uri = String.valueOf(((ServletWebRequest) request).getHttpMethod());
			if (((ServletWebRequest) request).getRequest() != null) {
				uri += " " + ((ServletWebRequest) request).getRequest().getRequestURI();
			}
		}
		return uri;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected ResponseEntity<ErrorDto> handle(Throwable t, HttpStatus httpStatus, String requestUri) {
		String type = t.getClass().getSimpleName();
		String description = t.getMessage() != null ? t.getMessage() : "Unknown error";
		if (httpStatus.is5xxServerError())
			log.error(String.format("Encountered unexpected error (code: %s, type: %s, message: %s, uri: %s)",
					httpStatus, type, t.getMessage(), requestUri), t);
		else if (httpStatus != HttpStatus.NOT_FOUND)
			log.warn(String.format("Encountered unexpected error (code: %s, type: %s, message: %s, uri: %s)",
					httpStatus, type, t.getMessage(), requestUri));
		ErrorDto error = new ErrorDto();
		error.setStatusCode(httpStatus.value());
		error.setType(type);
		error.setDescription(description);
		return new ResponseEntity(error, httpStatus);
	}

}
