package com.backend.freenow;

import java.util.List;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.backend.Dto.UserSearchResponse;

import io.restassured.RestAssured;

public class BackendTestChallenge extends BaseApiTest {

	private static String USERNAME = "Samantha";
	private UserSearchResponse user;
	 
	
	@BeforeClass
	public void setUp() {
		RestAssured.baseURI = "https://jsonplaceholder.typicode.com/";
	}
	
	// Find the specified user in all users.
	@Test(priority = 1)
	public void searchTheUser() {
	  String URI  =  "users";
	  List<UserSearchResponse> list = requestGET(URI, UserSearchResponse.class);
	  
	  user = list.stream().filter(k ->k.getUsername().equals(USERNAME)).findFirst().get();
	  
	  LOGGER.info(String.format("Username %s has ID %d", USERNAME,user.getId()));	  
	}
	
	
	
	@Test(priority = 2)
	public void searchPosts() {
		System.out.println("Hi There");
	}

}
