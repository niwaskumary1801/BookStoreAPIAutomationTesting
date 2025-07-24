package tests;

import static constants.ApiEndPointsKey.*;
import static io.restassured.RestAssured.given;
import org.testng.Assert;
import org.testng.annotations.Test;
import com.aventstack.extentreports.Status;
import base.BasicAPITest;
import config.ConfigFileReader;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

// TestCase 1: Verify the new User Creation with valid data 

public class VerifyPositiveScenarioForUserAccountTest extends BasicAPITest {
	
	public static String GeneratedRandomNewUserName = "user" + (int) (Math.random() * 1000) + ("@BookStore.com");
	public static String token;
	public Response responseData;
	public static String GeneratedUserid;

	@Test(priority = 1)
	public void NewUserCreation() {

		test = reportobj.createTest("Create New User Testing Scenario");

		String requestBody = String.format("""
				    {
				      "userName": "%s",
				      "password": "%s"
				    }
				""", GeneratedRandomNewUserName, ConfigFileReader.get("PASSWORD"));

		responseData = given().contentType(ContentType.JSON).body(requestBody).when().post(REQUEST_URL_CREATE_USER).then()
				.statusCode(201).extract().response();

		System.out.println(responseData);

		System.out.println("API Response Data for created user: " + responseData.getBody().asString()); // helpful debug

		// Log added in the report file.
		test.log(Status.INFO, "API Response Data for Newly Created User is " + responseData.getBody().asString());

		GeneratedUserid = responseData.jsonPath().getString("userID");
		Assert.assertNotNull(GeneratedUserid);
		test.log(Status.PASS, "NewUser created with USERID: " + GeneratedUserid);
	}

	// TestCase 2: Verify the Generate New Token for Newly Created User 
	
	@Test(priority = 2, dependsOnMethods = "NewUserCreation")
	public void GenerateNewTokentoAccessUser() {

		test = reportobj.createTest("Generate New Token for Newly CreatedUser Test");

		String requestBody = String.format("""
				    {
				      "userName": "%s",
				      "password": "%s"
				    }
				""", GeneratedRandomNewUserName, ConfigFileReader.get("PASSWORD"));

		responseData = given().contentType(ContentType.JSON).body(requestBody).when().post(REQUEST_URL_GENERATE_TOKEN).then()
				.statusCode(200).extract().response();

		System.out.println(responseData);

		System.out.println("API Response data for generated token: " + responseData.getBody().asString()); // helpful
																											// debug
		test.log(Status.INFO, "API Response data for generated token is " + responseData.getBody().asString());

		token = responseData.jsonPath().getString("token");
		Assert.assertNotNull(token);
		test.log(Status.PASS, "User token created: " + token);
	}

	// TestCase 3: Verify Newly Created User is listed under User List.
	
	@Test(priority = 3, dependsOnMethods = "NewUserCreation")
	public void testUserIsCreated() {

		test = reportobj.createTest("Check user is created");

		responseData = given().contentType(ContentType.JSON).header("Authorization", "Bearer " + token)
				.pathParam("userId", GeneratedUserid).when().get(REQUEST_URL_GET_USER + "{userId}").then().statusCode(200).extract()
				.response();

		System.out.println(responseData);

		System.out.println("API Response Data to check if user present: " + responseData.getBody().asString()); // helpful
																												// debug
		test.log(Status.INFO, "API Response Data to check if user is present " + responseData.getBody().asString());

	}

}
