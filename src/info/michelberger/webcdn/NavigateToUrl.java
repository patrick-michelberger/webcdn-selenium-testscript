package info.michelberger.webcdn;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Set;
import java.util.logging.Level;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
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

	private static String testpageUrl = "http://ec2-52-29-33-242.eu-central-1.compute.amazonaws.com";
	private static String hubUrl = "http://52.28.154.132:4444/wd/hub";
	private static int threadCount = 2;
	private static int timeSpentOnPage = 20000; // [ms]
	private static int getDelay = 5000; // [ms]
	private static int i = 0;

	public static void main(String[] args) throws InterruptedException {
		while (i < threadCount) {
			final int x = i;
			new Thread(new Runnable() {
				public void run() {
					// BROWSER SETTINGS
					DesiredCapabilities capabilities = new DesiredCapabilities();
					capabilities.setBrowserName("chrome");
					LoggingPreferences logPrefs = new LoggingPreferences();
					logPrefs.enable(LogType.BROWSER, Level.ALL);
					capabilities.setCapability(CapabilityType.LOGGING_PREFS,
							logPrefs);
					// CREATE REMOTEWEBDRIVER
					RemoteWebDriver driver = null;
					try {
						driver = new RemoteWebDriver(new URL(hubUrl),
								capabilities);
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
					// DELAY GET REQUESTS TO SIMULATE CONCURRENT USERS
					try {
						Thread.sleep(getDelay * x);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					// 1ST PAGE LOAD
					driver.get(testpageUrl);
					takeScreenshot(driver, x + "_1st");
					// 2ND PAGE LOAD
					openUrlWithNewTab(driver, testpageUrl);
					takeScreenshot(driver, x + "_2nd");
					// SIMULATE TIME, SINGLE USER SPENDS ON THE TEST PAGE
					try {
						Thread.sleep(timeSpentOnPage);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					// OUTPUT BROWSER LOGS AND QUIT SESSSION
					analyzeLog(driver);
					driver.quit();
				}
			}).start();
			i++;
		}
	}

	private static void analyzeLog(RemoteWebDriver driver) {
		LogEntries logEntries = driver.manage().logs().get(LogType.BROWSER);
		System.out.println(logEntries.getAll());
		for (LogEntry entry : logEntries) {
			System.out.println(new Date(entry.getTimestamp()) + " "
					+ entry.getLevel() + " " + entry.getMessage());
		}
	}

	private static void takeScreenshot(RemoteWebDriver driver, String id) {
		// WAIT UNTIL TEST IMAGE IS VISIBLE
		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By
				.id("webcdn_image")));
		// TAKE A SCREENSHOT FOR DOCUMENTATION
		File scrFile = ((TakesScreenshot) driver)
				.getScreenshotAs(OutputType.FILE);
		try {
			FileUtils.copyFile(scrFile, new File(
					"/Users/patrickmichelberger/Development/projects/webcdn/screenshot_"
							+ id + ".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void openUrlWithNewTab(RemoteWebDriver driver, String url) {
		// get a list of the currently open windows
		Set<String> windows = driver.getWindowHandles();
		// open a new window using javascript
		((JavascriptExecutor) driver).executeScript("window.open();");
		// get again a list of the currently open windows
		Set<String> windows2 = driver.getWindowHandles();
		// remove all of the original window handlers from the second list
		windows2.removeAll(windows);
		// save the window handle for the second page
		String secondObjectHandle = ((String) windows2.toArray()[0]);
		driver.switchTo().window(secondObjectHandle);
		driver.get(url);
	}

}