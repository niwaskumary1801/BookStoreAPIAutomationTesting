package base;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;

import io.restassured.RestAssured;
import utility.ExtentReportGenerateManager;

public class BasicAPITest {

	protected static ExtentReports reportobj;
	protected ExtentTest test;

	@BeforeSuite
	public void init() {
		RestAssured.baseURI = config.ConfigFileReader.get("API_BASE_URL");
		reportobj = ExtentReportGenerateManager.initReport();
	}

	@AfterSuite
	public void tearDown() {
		reportobj.flush();
	}
}
