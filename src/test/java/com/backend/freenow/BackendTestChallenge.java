package com.backend.freenow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.collections.CollectionUtils;

import com.backend.Dto.PostCommentResponseDto;
import com.backend.Dto.UserPostResponseDto;
import com.backend.Dto.UserSearchResponseDto;

import io.restassured.RestAssured;

public class BackendTestChallenge extends BaseApiTest {

	private static String USERNAME = "Samantha";
	private UserSearchResponseDto user;
	private List<UserPostResponseDto> postList;
	Map<String,String> param;
	String URI; 	
	String PARAM_USER_ID = "userId";
	String PARAM_POST_ID = "postId";
	
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
		//searchTheUser();
		URI = "posts";
	    param = new HashMap<String,String>();
		param.put(PARAM_USER_ID,String.valueOf(user.getId()));
		
	    postList = requestGET(URI,param,UserPostResponseDto.class);
		if(postList!=null && CollectionUtils.hasElements(postList)) {
			LOGGER.info(String.format("Username %s has posts %d", USERNAME,postList.size()));
		}else {
			LOGGER.warning(String.format("Username %s doesn't have any posts", USERNAME));
		}
	}
	
	//Find comments for individual posts and validate the email syntax.
	//making the test independent
	@Test(priority = 3)
	public void fetchCommentAndvalidateEmail() {
//		searchTheUser();
//		searchPosts();
		URI = "comments";
		param = new HashMap<String,String>();
		postList.forEach(k->{		
			
			param.put(PARAM_POST_ID,String.valueOf(k.getId()));
			List<PostCommentResponseDto> commentList = requestGET(URI,param,PostCommentResponseDto.class);
			
			if(commentList!=null && CollectionUtils.hasElements(commentList)) {
				List<String> emailList = commentList.stream().map(PostCommentResponseDto::getEmail).collect(Collectors.toList());
				validateEmailPattern(emailList);
				LOGGER.info(String.format("PostID %s with comments size %d has valid emails", k.getId(),commentList.size()));
			}else {
				LOGGER.warning(String.format("PostID %s doesn't have any comments", k.getId()));
			}
			
		});
		
	}

}
