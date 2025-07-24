package utility;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class ExtentReportGenerateManager 
{

	public static ExtentReports initReport() 
	{
		ExtentSparkReporter sparkObj = new ExtentSparkReporter("test-output/ExtentReport.html");
		ExtentReports extent = new ExtentReports();
		extent.attachReporter(sparkObj);
		return extent;
	}
}
