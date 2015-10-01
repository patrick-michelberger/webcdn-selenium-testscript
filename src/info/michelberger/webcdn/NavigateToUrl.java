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
	static String hubUrl = "http://52.28.154.132:4444/wd/hub";

	static int threadCount = 2; // number of concurrent users
	static int timeSpentOnPage = 20000; // time a user spends on the test page
										// [ms]
	static int getDelay = 5000; // time between each GET request [ms]

	static int i = 0;

	public static void main(String[] args) throws InterruptedException {

		while (i < threadCount) {
			final int x = i;
			new Thread(new Runnable() {
				public void run() {

					// Browser settings
					DesiredCapabilities capabilities = new DesiredCapabilities();
					capabilities.setBrowserName("chrome");
					LoggingPreferences logPrefs = new LoggingPreferences();
					logPrefs.enable(LogType.BROWSER, Level.ALL);
					capabilities.setCapability(CapabilityType.LOGGING_PREFS,
							logPrefs);

					// Create RemoteWebDriver
					RemoteWebDriver driver = null;
					try {
						driver = new RemoteWebDriver(new URL(hubUrl),
								capabilities);
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}

					// Delay GET requests to simulate concurrent users
					try {
						Thread.sleep(getDelay * x);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					// Load test page
					driver.get(testpageUrl);

					// Simulate time, single user spends on the test page
					try {
						Thread.sleep(timeSpentOnPage);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					// Wait until test image is visible
					WebDriverWait wait = new WebDriverWait(driver, 10);
					wait.until(ExpectedConditions.visibilityOfElementLocated(By
							.id("webcdn_image")));

					// Take a screenshot for documentation
					File scrFile = ((TakesScreenshot) driver)
							.getScreenshotAs(OutputType.FILE);
					try {
						FileUtils.copyFile(scrFile, new File(
								"/Users/patrickmichelberger/Development/projects/webcdn/screenshot_"
										+ x + ".png"));
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						// Output browser logs and quit the session
						analyzeLog(driver);
						driver.quit();
					}
				}
			}).start();
			i++;
		}
	}

	public static void analyzeLog(RemoteWebDriver driver) {
		LogEntries logEntries = driver.manage().logs().get(LogType.BROWSER);
		System.out.println(logEntries.getAll());
		for (LogEntry entry : logEntries) {
			System.out.println(new Date(entry.getTimestamp()) + " "
					+ entry.getLevel() + " " + entry.getMessage());
		}
	}
}