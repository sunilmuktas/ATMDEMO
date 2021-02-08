package com.demo.atm.controllerTest;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.demo.atm.controller.ATMController;
import com.demo.atm.entity.ATM;
import com.demo.atm.entity.Address;
import com.demo.atm.exception.DataValidationException;
import com.demo.atm.http.HttpRequestService;
import com.demo.atm.tranformer.JsonResponseTransformService;

class ATMControllerTest {

	@InjectMocks
	ATMController atmController;

	@Mock
	JsonResponseTransformService jsonResponseTransformService;

	@Mock
	HttpRequestService httpRequestService;

	String URL = "https://www.ing.nl/api/locator/atms/";
	String response = ")]}',[{\n" + "\"address\": {\n" + "\"housenumber\": \"test\",\n"
			+ "			\"city\": \"HYDERABAD\",\n" + "		\"geoLocation\": null\n" + "		},\n"
			+ "		\"distance\": 10,\n" + "		\"openingHours\": null,\n"
			+ "		\"functionality\": \"testing1\",\n" + "		\"type\": \"test1\"\n" + "	},\n" + "	{\n"
			+ "		\"address\": {\n" + "			\"housenumber\": \"test\",\n" + "			\"city\": \"PUNE\",\n"
			+ "			\"geoLocation\": null\n" + "		},\n" + "		\"distance\": 11,\n"
			+ "		\"openingHours\": null,\n" + "		\"functionality\": \"testing2\",\n"
			+ "		\"type\": \"test2\"\n" + "	},\n" + "	{\n" + "		\"address\": {\n"
			+ "			\"housenumber\": \"test\",\n" + "			\"city\": \"BANGLORE\",\n"
			+ "			\"geoLocation\": null\n" + "		},\n" + "		\"distance\": 12,\n"
			+ "		\"openingHours\": null,\n" + "		\"functionality\": \"testing3\",\n"
			+ "		\"type\": \"test3\"\n" + "	}\n" + "]";
	Map<ATM, String> atmCacheMap = new HashMap<>();

	String city1 = "Banglore";
	String city2 = "Hyderabad";
	String city3 = "Pune";
	ATM atm1 = new ATM();
	ATM atm2 = new ATM();
	ATM atm3 = new ATM();
	ATM atm4 = new ATM();

	@BeforeEach
	public void beforeEach() {

		MockitoAnnotations.initMocks(this);

		atmController = new ATMController(URL, jsonResponseTransformService, httpRequestService);

		atm1.setDistance(10);
		atm1.setFunctionality("testing1");
		atm1.setType("test1");

		atm1.setAddress(getSeedAddressData(city1));

		atm2.setDistance(11);
		atm2.setFunctionality("testing2");
		atm2.setType("test2");
//		System.out.println(response);

		atm2.setAddress(getSeedAddressData(city2));

		atm3.setDistance(12);
		atm3.setFunctionality("testing3");
		atm3.setType("test3");

		atm3.setAddress(getSeedAddressData(city3));

		atm4.setDistance(13);
		atm4.setFunctionality("testing4");
		atm4.setType("test4");

		atm4.setAddress(getSeedAddressData(city1));

		atmCacheMap.put(atm1, city1);
		atmCacheMap.put(atm2, city2);
		atmCacheMap.put(atm3, city3);
		atmCacheMap.put(atm4, city1);
//		ATM[] array = new ATM[] { atm1, atm2, atm3 };
		when(httpRequestService.getResponse(URL)).thenReturn(response);
		try {
//			doReturn(array).when(jsonResponseTransformer).fromResponsetoArray(URL);
			when(jsonResponseTransformService.fromResponsetoArray(response.substring(5, response.length())))
					.thenReturn(atmCacheMap);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

	@Test
	public void test_getAllATMS_happyPath() {

		List<ATM> atmSet = (List<ATM>) atmController.getAllATMS().getBody();
		assertNotNull(atmSet);

		System.out.println(atmSet);
		assertEquals(atmCacheMap.keySet().size(), atmSet.size());

		assertTrue(atmCacheMap.containsKey(atmSet.get(0)));
		assertTrue(atmCacheMap.containsKey(atmSet.get(1)));
		assertTrue(atmCacheMap.containsKey(atmSet.get(2)));

	}

	@Test
	public void test_getAllATMS_sadPath_diffCityNames() {

		List<ATM> atmSet = (List<ATM>) atmController.getAllATMS().getBody();
		assertNotNull(atmSet);

		assertEquals(atmCacheMap.keySet().size(), atmSet.size());
		assertNotEquals(atmSet.get(0).getAddress().getCity(), "dummy city1");
		assertNotEquals(atmSet.get(1).getAddress().getCity(), "dummy city2");
		assertNotEquals(atmSet.get(2).getAddress().getCity(), "dummy city3");

	}

	@Test
	public void test_getAllATMS_sadPath_differentSize() {
		List<ATM> atmSet = (List<ATM>) atmController.getAllATMS().getBody();
		assertNotNull(atmSet);
		assertNotEquals(2, atmSet.size());

	}

	@Test
	public void test_getATMByCity_happyPath_city1() {

		List<ATM> atmList = (List<ATM>) atmController.getATMSByCity(city1).getBody();
		assertNotNull(atmList);
		assertTrue(atmList.contains(atm1));
		assertTrue(atmList.contains(atm4));
		assertTrue(!atmList.contains(atm2));

	}

	@Test
	public void test_getATMSByCity_happyPath_city2() {
		List<ATM> atmList = (List<ATM>) atmController.getATMSByCity(city2).getBody();
		assertNotNull(atmList);
		assertTrue(atmList.contains(atm2));

	}

	@Test
	public void test_getATMSByCity_happyPath_city3() {
		List<ATM> atmList = (List<ATM>) atmController.getATMSByCity(city3).getBody();
		assertNotNull(atmList);
		assertTrue(atmList.contains(atm3));

	}

	@Test
	public void test_getATMByCity_sadPath_DataValidationException_cityNull() {

		String city = null;
		DataValidationException exception = assertThrows(DataValidationException.class,
				() -> atmController.getATMSByCity(city));
		assertEquals("Required value found as null", exception.getMessage());
	}

	@Test
	public void test_getATMByCity_sadPath_DataValidationException_cityEmpty() {

		String city = "";
		DataValidationException exception = assertThrows(DataValidationException.class,
				() -> atmController.getATMSByCity(city));
		assertEquals("required value found as empty", exception.getMessage());

	}

	@Test
	public void test_getATMByCity_sadPath_DataValidationException_cityBlank() {

		String city = " ";
		DataValidationException exception = assertThrows(DataValidationException.class,
				() -> atmController.getATMSByCity(city));
		assertEquals("required value found as blank", exception.getMessage());

	}

	@Test
	public void test_getATMByCity_sadPath_DataValidationException_diffCity() {

		String city = "12";
		ResponseEntity<?> atmsByCity = atmController.getATMSByCity(city);
		assertEquals(HttpStatus.NO_CONTENT, atmsByCity.getStatusCode());
		assertNull(atmsByCity.getBody());

	}

	public Address getSeedAddressData(String cityName) {
		Address address = new Address();
		address.setCity(cityName);
		address.setHousenumber("test");
		return address;

	}

	public List<ATM> getATMByCity(String city) {
		List<ATM> atmsList = new ArrayList<>();
		atmCacheMap.forEach((key, value) -> {
			if (value.equalsIgnoreCase(city))
				atmsList.add(key);

		});

		return atmsList;
	}

}
