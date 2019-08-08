package com.backend.freenow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.collections.CollectionUtils;

import com.backend.constants.DataConstants;
import com.backend.dto.PostCommentResponseDto;
import com.backend.dto.UserPostResponseDto;
import com.backend.dto.UserSearchResponseDto;

import io.restassured.RestAssured;

public class BackendTestChallengeTest extends BaseApiTest {
	
	private  UserSearchResponseDto user;
	private  List<UserPostResponseDto> postList;
	private  Map<String, String> param;
	
	public static String URI;

	@BeforeClass
	public void setUp() {
		RestAssured.baseURI = DataConstants.BASE_URI;
	}

	// Find the specified user in all users.
	@Test(priority = 1)
	public void searchTheUser() {
		URI = "users";
		List<UserSearchResponseDto> userList = this.requestGET(URI, null, UserSearchResponseDto.class);
		try {
			if (userList != null) {
				user = userList.stream().filter(k -> k.getUsername().equals(DataConstants.USERNAME)).findFirst().get();
				LOGGER.info(String.format("Username %s has ID %d",DataConstants.USERNAME, user.getId()));
			}
			else {
				LOGGER.info(String.format("User List received is empty"));
			}
		} catch (NoSuchElementException e) {
			LOGGER.info(String.format("Username %s is not present in Http response", DataConstants.USERNAME));
		}
	}

	// Find posts written by specified user. calling SearchTheUser() method again to
	// make the test isolate
	// Although if above test getting failed, then no use of run below test cases as
	// these are dependent on above.
	@Test(priority = 2)
	public void searchPosts() {
		this.searchTheUser();
		URI = "posts";
		param = new HashMap<String, String>();

		if (user != null) {
			param.put(DataConstants.PARAM_USER_ID, String.valueOf(user.getId()));

			postList = this.requestGET(URI, param, UserPostResponseDto.class);
			if (postList != null && CollectionUtils.hasElements(postList)) {
				LOGGER.info(String.format("Username %s has posts %d", DataConstants.USERNAME, postList.size()));
			} else {
				LOGGER.warning(String.format("Username %s doesn't have any posts", DataConstants.USERNAME));
			}
		}else {
			LOGGER.info(String.format("Username %s is not present in Http response", DataConstants.USERNAME));
		}
	}

	// Find comments for individual posts and validate the email syntax.
	// making the test independent
	@Test(priority = 3)
	public void fetchCommentAndvalidateEmail() {
		searchTheUser();
		searchPosts();
		URI = "comments";
		param = new HashMap<String, String>();
		if (postList != null) {
			postList.forEach(k -> {
				param.put(DataConstants.PARAM_POST_ID, String.valueOf(k.getId()));
				List<PostCommentResponseDto> commentList = this.requestGET(URI, param, PostCommentResponseDto.class);

				if (commentList != null && CollectionUtils.hasElements(commentList)) {
					List<String> emailList = commentList.stream().map(PostCommentResponseDto::getEmail)
							.collect(Collectors.toList());
					this.validateEmailPattern(emailList);
					LOGGER.info(String.format("PostID %s with comments size %d has valid emails", k.getId(),
							commentList.size()));
				} else {
					LOGGER.warning(String.format("PostID %s doesn't have any comments", k.getId()));
				}
			});
		}else {
			LOGGER.warning(String.format("Username %s doesn't have any posts", DataConstants.USERNAME));
		}
	}

	// validate email using RegEx.
	protected void validateEmailPattern(List<String> email) {
		Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile(DataConstants.EMAIL_REGEX,
				Pattern.CASE_INSENSITIVE);
		if (email != null) {
			email.forEach(k -> {
				Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(k);
				Assert.assertTrue(matcher.find(), "Email syntax is not valid");
			});
		}else {
			LOGGER.info("Email List received is empty");
		}
	}

}
