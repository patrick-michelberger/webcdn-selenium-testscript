package info.michelberger.webcdn;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.logging.Level;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class NavigateToUrl {

	static String testpageUrl = "http://ec2-52-29-33-242.eu-central-1.compute.amazonaws.com";

	public static void main(String[] args) throws InterruptedException {
		DesiredCapabilities capabilities = new DesiredCapabilities();
		// Enable Browser Console Logging
		LoggingPreferences logPrefs = new LoggingPreferences();
		logPrefs.enable(LogType.BROWSER, Level.ALL);
		capabilities.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);
		// Set browser vendor
		capabilities.setBrowserName("chrome");

		RemoteWebDriver[] drivers = new RemoteWebDriver[2];

		for (int i = 0; i < drivers.length; i++) {
			RemoteWebDriver driver = null;
			try {
				driver = new RemoteWebDriver(new URL(
						"http://52.28.154.132:4444/wd/hub"), capabilities);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			// Load testpage
			System.out.println("load url: " + testpageUrl);
			driver.get(testpageUrl);

			WebDriverWait wait = new WebDriverWait(driver, 10);
			wait.until(ExpectedConditions.visibilityOfElementLocated(By
					.id("webcdn_image")));

			File scrFile = ((TakesScreenshot) driver)
					.getScreenshotAs(OutputType.FILE);

			try {
				FileUtils.copyFile(scrFile, new File(
						"/Users/patrickmichelberger/Development/projects/webcdn/screenshot_"
								+ i + ".png"));
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				analyzeLog(driver);
				drivers[i] = driver;
			}
		}
		// Quit all browsers
		for (RemoteWebDriver driver : drivers) {
			driver.quit();
		}
		System.out.println("finished");
	}

	public static void analyzeLog(RemoteWebDriver driver) {
		System.out.println("Analyze logs...");
		driver.get("http://testpage.com");
		LogEntries logEntries = driver.manage().logs().get(LogType.BROWSER);
		System.out.println(logEntries.getAll());
		for (LogEntry entry : logEntries) {
			System.out.println(new Date(entry.getTimestamp()) + " "
					+ entry.getLevel() + " " + entry.getMessage());
		}
	}

}