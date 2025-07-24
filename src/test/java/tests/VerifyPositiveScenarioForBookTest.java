package tests;

import static constants.ApiEndPointsKey.*;
import static io.restassured.RestAssured.given;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.model.Report;

import base.BasicAPITest;
import config.ConfigFileReader;
import io.restassured.response.Response;

public class VerifyPositiveScenarioForBookTest extends BasicAPITest {
	public Response responseData;
	public static String isbnNumber;
	public static String bookTitleName;

	// TestCase1: Verify Create Book with valid UserID and ISBN 
	
	@Test(priority = 1, dependsOnMethods = { "tests.VerifyPositiveScenarioForUserAccountTest.NewUserCreation",
			"tests.VerifyPositiveScenarioForUserAccountTest.GenerateNewTokentoAccessUser" })
	public void createBookInCollection() {

		test = reportobj.createTest("Create Book");

		String requestBody = String.format("""
				{
				  "userId": "%s",
				  "collectionOfIsbns": [{ "isbn": "%s" }]
				}
				""",

				VerifyPositiveScenarioForUserAccountTest.GeneratedUserid, ConfigFileReader.get("ISBN"));
		
		responseData = given().header("Authorization", "Bearer " + VerifyPositiveScenarioForUserAccountTest.token)
				.contentType("application/json").body(requestBody).when().post(REQUEST_URL_ADD_BOOKS).then().statusCode(201)
				.extract().response();

		System.out.println("Response Data for create book: " + responseData.getBody().asString());

		test.log(Status.INFO, "Response Data for create book is " + responseData.getBody().asString());

		isbnNumber = responseData.jsonPath().getString("books[0].isbn");
		Assert.assertNotNull(isbnNumber);
		test.log(Status.PASS, "User created with ISBN Number: " + isbnNumber);
	}
	
	// TestCase2: Verify created Book is listed under Book Collection.

	@Test(priority = 2, dependsOnMethods = "createBookInCollection")
	public void testGetAllBooks() {

		test = reportobj.createTest("Get Books");

		responseData = given().when().get(REQUEST_URL_GET_BOOKS).then().statusCode(200).extract().response();

		System.out.println("Response Data for checking if book created: " + responseData.getBody().asString()); 


		test.log(Status.INFO,
				"Response Data for getting the newly created book is " + responseData.getBody().asString());

		bookTitleName = responseData.jsonPath().getString("books[0].title");
		Assert.assertNotNull(bookTitleName);
		test.log(Status.PASS, "User present with title: " + bookTitleName);
	}

	// TestCase3: Verify update Book with Valid UserId and ISBN.
	
	@Test(priority = 3, dependsOnMethods = { "createBookInCollection" })
	public void updateBookInCollection() {

		test = reportobj.createTest("Update Book not supported hence 400");

		String body = String.format("""
				{
				  "userId": "%s",
				  "isbn": "%s"
				}
				""", VerifyPositiveScenarioForUserAccountTest.GeneratedUserid,
				VerifyPositiveScenarioForBookTest.isbnNumber);

		responseData = given().header("Authorization", "Bearer " + VerifyPositiveScenarioForUserAccountTest.token)
				.contentType("application/json").pathParam("isbn", ConfigFileReader.get("ISBN")).body(body).when()
				.put(REQUEST_URL_UPDATE_BOOKS + "{isbn}").then().statusCode(400).extract().response();

		System.out.println("Response Data for update books: " + responseData.getBody().asString()); 

		test.log(Status.INFO, "Response Data for updating book is " + responseData.getBody().asString());
	}

	// TestCase4: Verify Delete Book with Valid UserId , ISBN and Token
	
	@Test(priority = 4, dependsOnMethods = { "createBookInCollection" })
	public void deleteBookFromCollection() {
		test = reportobj.createTest("Delete Book");

	
		System.out.println("Token used: " + VerifyPositiveScenarioForUserAccountTest.token);
		Assert.assertNotNull(VerifyPositiveScenarioForUserAccountTest.token, "Token is null!");

	
		System.out.println("User Id used: " + VerifyPositiveScenarioForUserAccountTest.GeneratedUserid);
		Assert.assertNotNull(VerifyPositiveScenarioForUserAccountTest.GeneratedUserid, "UserId is null!");

		String accountId = VerifyPositiveScenarioForUserAccountTest.GeneratedUserid;

		String body = String.format("""
				{
				    "userId": "%s",
					"isbn": "%s"
				}
				""", VerifyPositiveScenarioForUserAccountTest.GeneratedUserid,
				VerifyPositiveScenarioForBookTest.isbnNumber);

		responseData = given().header("Authorization", "Bearer " + VerifyPositiveScenarioForUserAccountTest.token)
				.contentType("application/json").body(body).when().delete(REQUEST_URL_DELETE_BOOK).then().statusCode(204).extract()
				.response();

		System.out.println("Response Data for delete book: " + responseData.getBody().asString()); 

		test.log(Status.INFO, "Response Data for deleted book is " + responseData.getBody().asString());

		String respbody = responseData.getBody().asString();
		Assert.assertTrue(respbody == null || respbody.trim().isEmpty(), "Expected empty response body");

		System.out.println("DELETE Response is empty as expected");
		test.log(Status.INFO, "DELETE Response is empty as expected");
	}

	// TestCase5: Verify Deleted Book is no longer listed under Book Collection.
	
	@Test(priority = 5, dependsOnMethods = "deleteBookFromCollection")
	public void checkIfBookDeleted() {

		test = reportobj.createTest("Verify whether Book is Deleted from BookList");

		// Validating PreCondition Parameters
		Assert.assertNotNull(VerifyPositiveScenarioForUserAccountTest.token, "Token is null");
		Assert.assertNotNull(VerifyPositiveScenarioForUserAccountTest.GeneratedUserid, "User ID is null");
		Assert.assertNotNull(VerifyPositiveScenarioForBookTest.isbnNumber, "ISBN is null");

		// Send GET request to fetch user collection
		responseData = given().header("Authorization", "Bearer " + VerifyPositiveScenarioForUserAccountTest.token)
				.when().get(REQUEST_URL_GET_USER + VerifyPositiveScenarioForUserAccountTest.GeneratedUserid).then().statusCode(200)
				.extract().response();

		String responseBody = responseData.getBody().asString();
		System.out.println("User collection after deletion: " + responseBody);
		test.log(Status.INFO, "User collection after delete: " + responseBody);

		// Verify the ISBN is no longer in the user's collection
		Assert.assertFalse(responseBody.contains(VerifyPositiveScenarioForBookTest.isbnNumber),
				"Deleted book still appears in the user's collection.");

		test.log(Status.PASS, "Deleted book is no longer in the user's collection.");
	}
}
