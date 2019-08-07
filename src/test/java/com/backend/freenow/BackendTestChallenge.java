package com.backend.freenow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.collections.CollectionUtils;

import com.backend.Dto.UserPostResponseDto;
import com.backend.Dto.UserSearchResponseDto;

import io.restassured.RestAssured;

public class BackendTestChallenge extends BaseApiTest {

	private static String USERNAME = "Samantha";
	private UserSearchResponseDto user;
	String URI; 	
	String PARAM_USER_ID = "userId";	
	
	@BeforeClass
	public void setUp() {
		RestAssured.baseURI = "https://jsonplaceholder.typicode.com/";
	}
	
	// Find the specified user in all users.
	@Test(priority = 1)
	public void searchTheUser() {
	  URI  =  "users";
	  List<UserSearchResponseDto> userList = requestGET(URI,null, UserSearchResponseDto.class);
	  
	  try {
	  user = userList.stream().filter(k ->k.getUsername().equals(USERNAME)).findFirst().get();
	  }catch(NoSuchElementException e) {
		  LOGGER.info(String.format("Username %s is not present in Http response", USERNAME));
	  }
	  LOGGER.info(String.format("Username %s has ID %d", USERNAME,user.getId()));	  
	}
	
	//Find posts written by specified user. calling SearchTheUser() method again to make the test isolate
	// Although if above test getting failed, then no use of run below test cases as these are dependent on above.
	@Test(priority = 2)
	public void searchPosts() {
		searchTheUser();
		URI = "posts";
		Map<String,String> param = new HashMap<String,String>();
		param.put(PARAM_USER_ID,String.valueOf(user.getId()));
		
		List<UserPostResponseDto> postList = requestGET(URI,param,UserPostResponseDto.class);
		if(postList!=null && CollectionUtils.hasElements(postList)) {
			LOGGER.info(String.format("Username %s has posts %d", USERNAME,postList.size()));
		}else {
			LOGGER.warning(String.format("Username %s doesn't have any posts", USERNAME));
		}
	}

}
