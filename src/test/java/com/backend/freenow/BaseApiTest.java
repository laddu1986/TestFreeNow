package com.backend.freenow;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.params.CoreConnectionPNames;
import org.testng.Assert;
import org.testng.SkipException;

import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class BaseApiTest {
	
	@SuppressWarnings("deprecation")
	public BaseApiTest() {
		RestAssured.config = RestAssuredConfig.config()
		        .httpClient(HttpClientConfig.httpClientConfig()
		                .setParam(CoreConnectionPNames.CONNECTION_TIMEOUT, 1000000)
		                .setParam(CoreConnectionPNames.SO_TIMEOUT, 100000));
	}
	
	Response httpresponse;
	
	protected final static Logger LOGGER =  
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	public <T> List<T> requestGET(String URI,Map<String,String> params,Class clazz) {
		if(params!=null) {
		   httpresponse = RestAssured.given().queryParams(params).get(URI);
		}else {
		   httpresponse = RestAssured.given().get(URI);
		}
		validateHttpResponse(httpresponse.statusCode(),httpresponse.getContentType());
		
		JsonPath path  =  httpresponse.getBody().jsonPath();
		List<T> responseDto = path.getList("$", clazz);
		return responseDto;
	}
	
	private void validateHttpResponse(int statusCode,String contentType) {
		SkipException exception = new SkipException("Skipping Test...");
		if(statusCode == 400 || statusCode == 404 ) {
			LOGGER.warning("please check your http request and Auth status");
			throw exception;
		}
		else if(statusCode == 500){
			LOGGER.warning("Something went wrog with Website");
			throw exception;
		}
		else if ((statusCode == 200 || statusCode == 201) && contentType.contains("json") ) {
			LOGGER.info("everything went well. Cheers!!");
		}
		else {
			LOGGER.warning("Application seems to be down at this moment");
			throw exception;
		}
	}
	
	protected void validateEmailPattern(List<String> email) {
	 Pattern VALID_EMAIL_ADDRESS_REGEX = 
			    Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
	 
	email.forEach(k->{
		Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(k);
		Assert.assertTrue(matcher.find());
	});
	}

}
