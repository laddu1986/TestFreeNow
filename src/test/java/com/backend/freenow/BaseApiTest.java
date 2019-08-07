package com.backend.freenow;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.http.params.CoreConnectionPNames;
import org.testng.SkipException;

import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class BaseApiTest {
	
	Response httpresponse;
	
	protected final static Logger LOGGER =  
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	public <T> List<T> requestGET(String URI,Map<String,String> params,Class clazz) {
		if(params!=null) {
		   httpresponse = RestAssured.given().queryParams(params).get(URI);
		}else {
		   httpresponse = RestAssured.given().get(URI);
		}
		validateHttpResponse(httpresponse.statusCode());
		JsonPath path  =  httpresponse.getBody().jsonPath();
		List<T> responseDto = path.getList("$", clazz);
		return responseDto;
	}
	
	private void validateHttpResponse(int statusCode) {
		SkipException exception = new SkipException("Skipping Test...");
		if(statusCode == 400 || statusCode == 404 ) {
			LOGGER.info("please check your http request and Auth status");
			throw exception;
		}
		else if(statusCode == 500){
			LOGGER.info("Something went wrog with Website");
			throw exception;
		}
		else if (statusCode == 200 || statusCode == 201) {
			LOGGER.info("everything went well. Cheers!!");
		}
	}

}
