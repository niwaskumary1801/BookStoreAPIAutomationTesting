package tests;

import static io.restassured.RestAssured.given;
import org.testng.Assert;
import org.testng.annotations.Test;
import com.aventstack.extentreports.Status;
import base.BasicAPITest;
import config.ConfigFileReader;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import static constants.ApiEndPointsKey.*;

public class VerifyNegativeScenarioUserAndBook extends BasicAPITest {
	public Response responseData;

	// Neg Test1: Verify user creation when Payload data missing.
	@Test
	public void test_CreateUserWhenPayloadDataMissing() {
		test = reportobj.createTest("Create User when Payload data missing");
		String requestBody = """
				{

				}
				""";

		responseData = given().contentType(ContentType.JSON).body(requestBody).when().post(REQUEST_URL_CREATE_USER)
				.then().statusCode(400).extract().response();

		String ResMessage = responseData.jsonPath().getString("message");

		Assert.assertTrue(ResMessage.contains("UserName and Password required"), "Expected Validation Error message");

		test.log(Status.PASS, "User not created and throws message as - UserName and Password required");

		System.out.println(
				"Response Data for create user when Payload data missing : " + responseData.getBody().asString());

		test.log(Status.INFO,
				"Response Data for create user when Payload data missing " + responseData.getBody().asString());

	}

	// Neg Test2: Verify user creation when user enters invalid password

	@Test
	public void test_CreateUserWithInvalidPassword() {
		test = reportobj.createTest("Create User with Invalid Password");
		String requestBody = """
				{
				  "userName": "TestUser1234",
				  "password": "Niwas"
				}
				""";

		responseData = given().contentType(ContentType.JSON).body(requestBody).when().post(REQUEST_URL_CREATE_USER)
				.then().statusCode(400).extract().response();

		String ResMessage = responseData.jsonPath().getString("message");

		Assert.assertTrue(ResMessage.contains("Passwords must have"), "Expected password policy error");

		test.log(Status.PASS, "Invalid password was correctly rejected and not allowed to create user.");

		System.out.println("Response Data for create user with Invalid password: " + responseData.getBody().asString());

		test.log(Status.INFO,
				"Response Data for new user with Invalid password is " + responseData.getBody().asString());

	}

	/**
	 * Neg Test3: Verify Generate token with invalid credentials. Generate token gives
	 * status as 200 for valid and invalid details.
	 */

	@Test
	public void testGenerateTokenInvalidCredentials() {
		test = reportobj.createTest("Generate Token with Invalid User Credentials");

		String requestBody = """
				{
				  "userName": "Invalid User",
				  "password": "WrongPass"
				}
				""";

		responseData = given().contentType(ContentType.JSON).body(requestBody).when().post(REQUEST_URL_GENERATE_TOKEN)
				.then().statusCode(200).extract().response();

		String status = responseData.jsonPath().getString("status");
		Assert.assertEquals(status, "Failed");
		test.log(Status.PASS, "Invalid login attempt was correctly rejected.");

		System.out.println(
				"Response Data for generate token with invalid credentials: " + responseData.getBody().asString()); // helpful
																													// debug
		test.log(Status.INFO,
				"Response Data for generate token with invalid credentials " + responseData.getBody().asString());
	}

	/**
	 * Neg Test4: Verify Create book with no Authorisation and check API responsed status code as 401
	 */
	@Test
	public void createBookInCollection() {

		test = reportobj.createTest("Create Book without authorisation");

		String requestBody = String.format("""
				{
				  "userId": "%s",
				  "collectionOfIsbns": [{ "isbn": "%s" }]
				}
				""", VerifyPositiveScenarioForUserAccountTest.GeneratedUserid, ConfigFileReader.get("ISBN"));

		responseData = given().contentType("application/json").body(requestBody).when().post(REQUEST_URL_ADD_BOOKS)
				.then().statusCode(401).extract().response();

		String ResMessage = responseData.jsonPath().getString("message");

		Assert.assertTrue(ResMessage.contains("User not authorized!"));

		test.log(Status.PASS, "Attempt to create books without authorisation was correctly rejected.");

		System.out.println("Create book without authorisation: " + responseData.getBody().asString()); // helpful
																										// debug
		test.log(Status.INFO, "Create book without authorisation " + responseData.getBody().asString());
	}

	// Neg Test5:Verify Get user with wrong user id to search ,API should respond
	// status code 401 'Not Authorised'.

	@Test
	public void testUserIsCreated() {

		test = reportobj.createTest("Get user but search for wrong user id");

		responseData = given().contentType(ContentType.JSON).pathParam("userId", "invalidUserId").when()
				.get(REQUEST_URL_GET_USER + "{userId}").then().statusCode(401).extract().response();

		String ResMessage = responseData.jsonPath().getString("message");
		Assert.assertTrue(ResMessage.contains("User not authorized!"));
		test.log(Status.PASS, "Attempt to get invalid userid was correctly rejected.");

		System.out.println("Response Data for get user with invalid userid: " + responseData.getBody().asString()); // helpful
		// debug
		test.log(Status.INFO, "Response Data for get user with invalid userid " + responseData.getBody().asString());
	}

	/**
	 * Neg Test6: Verify attempt Update book when don't provide ISBN..
	 */
	@Test(dependsOnMethods = { "tests.VerifyPositiveScenarioForUserAccountTest.NewUserCreation" })
	public void updateBookInCollection() {

		test = reportobj.createTest("Update Book but dont provide ISBN");

		String body = String.format("""
				{
				  "userId": "%s",
				  "isbn": "%s"
				}
				""", VerifyPositiveScenarioForUserAccountTest.GeneratedUserid, "");

		responseData = given().header("Authorization", "Bearer " + VerifyPositiveScenarioForUserAccountTest.token)
				.contentType("application/json").pathParam("isbn", ConfigFileReader.get("ISBN")).body(body)

				.when().put(REQUEST_URL_UPDATE_BOOKS + "{isbn}").then().statusCode(400).extract().response();

		String ResMessage = responseData.jsonPath().getString("message");

		Assert.assertTrue(ResMessage.contains("Request Body is Invalid!"));

		test.log(Status.PASS, "Attempt to update book with no ISBN was correctly rejected.");

		System.out.println("Response Data for update books with no ISBN: " + responseData.getBody().asString()); // helpful
																													// debug
		test.log(Status.INFO, "Response Data for updating book with no ISBN " + responseData.getBody().asString());
	}

	// Neg Test7: Verify Delete book when pass wrong user id.

	@Test()
	public void deleteBookFromCollection() {

		test = reportobj.createTest("Delete Book but dont provide userid");

		String body = String.format("""
				{
				  "userId": "%s",
					"isbn": "%s"
				}
				""", "", ConfigFileReader.get("ISBN"));

		responseData = given().header("Authorization", "Bearer " + VerifyPositiveScenarioForUserAccountTest.token)
				.contentType("application/json").body(body)

				.when().delete(REQUEST_URL_DELETE_BOOK).then().statusCode(401).extract().response();

		String ResMessage = responseData.jsonPath().getString("message");

		Assert.assertTrue(ResMessage.contains("User Id not correct!"));

		test.log(Status.PASS, "Attempt to delete book without passing userid was correctly rejected.");

		System.out
				.println("Response Data for delete books without passing userid: " + responseData.getBody().asString());

		test.log(Status.INFO,
				"Response Data for delete books without passing userid " + responseData.getBody().asString());

	}
}
