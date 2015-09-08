package info.michelberger.webcdn;

import java.net.MalformedURLException;
import java.net.URL;

import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

public class NavigateToUrl {

	public static void main(String[] args) {
		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setBrowserName("chrome");
		
		// DRIVER 1
		RemoteWebDriver driver1 = null;
		try {
			driver1 = new RemoteWebDriver(new URL(
					"http://52.28.206.156:4444/wd/hub"), capabilities);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	    driver1.get("http://webcdn.michelberger.info:8000/examples");
	    
	    // DRIVER 2
		RemoteWebDriver driver2 = null;
		try {
			driver2 = new RemoteWebDriver(new URL(
					"http://52.28.206.156:4444/wd/hub"), capabilities);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	    driver2.get("http://webcdn.michelberger.info:8000/examples");
	    
	    try{
	        Thread.sleep(5000000);
	        // Then do something meaningful...
	        System.out.println("Quit...");
		    driver1.quit();
		    driver2.quit();
	    }catch(InterruptedException e){
	        e.printStackTrace();
	    }
	}
}