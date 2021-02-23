package com.vbj.automate.browser;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.vbj.automate.browser.model.Slot;

@SpringBootApplication
@EnableScheduling
public class BrowserApplication implements CommandLineRunner {

	private static List<Slot> slots = new ArrayList<Slot>();
	private static String arg_0 = "C:\\dev\\softwares\\webdriver\\chromedriver_win32\\chromedriver.exe";

	public static void main(String[] args) {
		SpringApplication.run(BrowserApplication.class, args);
	}

	@Override	
	public void run(String... args) throws Exception {

		arg_0 = args[0];
		if (args.length == 0) {
			System.setProperty("webdriver.chrome.driver",
					arg_0);
		} else {
			System.setProperty("webdriver.chrome.driver", arg_0);
		}		
		schedule();
	}

	private void schedule() throws InterruptedException, SAXException, IOException, ParserConfigurationException {
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		WebDriver driver = new ChromeDriver();
		driver.get(DriveTestConstants.DRIVETEST);
		Util.waitForPageLoad(driver);

		// Book Test
		driver.findElement(By.xpath("//*[@id=\"content\"]/div[1]/div/div/div/div/div/div/div/a[1]")).click();

		// User Information
		Util.waitForPageLoad(driver);
		Util.waitAndClick(driver, "//*[@id=\"emailAddress\"]");
		driver.findElement(By.xpath("//*[@id=\"emailAddress\"]")).sendKeys("vishnubharathj@gmail.com");
		driver.findElement(By.xpath("//*[@id=\"confirmEmailAddress\"]")).sendKeys("vishnubharathj@gmail.com");
		driver.findElement(By.xpath("//*[@id=\"licenceNumber\"]")).sendKeys("J09537721870329");
		driver.findElement(By.xpath("//*[@id=\"licenceExpiryDate\"]")).sendKeys("20240104");
		driver.findElement(By.xpath("//*[@id=\"regSubmitBtn\"]/span")).click();

		// Select G
		Util.waitAndClick(driver, "//*[@id=\"Gbtn\"]");
		driver.findElement(By.xpath("//*[@id=\"booking-licence\"]/div/form/div/div[4]/button/span")).click();

		// Reschedule
		driver.get(DriveTestConstants.DRIVETEST + "book-a-road-test/booking.html#/verify-driver");
		Util.waitForPageLoad(driver);
		Util.waitAndClick(driver, "//*[@id=\"regSubmitBtn\"]/span");

		Util.waitForPageLoad(driver);
		Thread.sleep(5000);
		Util.waitAndCompare(driver, "//*[@id=\"7973589\"]/div/div/div[2]/div/span[2]", "Reschedule Test");

		Util.waitAndCompare(driver,
				"//*[@id=\"page_book_a_road_test_booking\"]/div[4]/div/div/div/div/div/div[4]/button[1]", "reschedule");

		Util.waitForPageLoad(driver);

		// locationLineBlock
		Util.waitAndClickCss(driver, ".locationLineBlock");
		List<WebElement> elements = driver.findElements(By.cssSelector(".locationLineBlock"));
		for (int i = 0; i < elements.size(); i++) {
			System.out.println("\n"+ driver.findElements(By.cssSelector(".locationLineBlock")).get(i).getText());
			int elementPosition = driver.findElements(By.cssSelector(".locationLineBlock")).get(i).getLocation().getY();
			String js = String.format("window.scroll(0, %s)", elementPosition - 100);
			((JavascriptExecutor) driver).executeScript(js);

			elementPosition = driver.findElements(By.cssSelector(".dtc_listings")).get(0).getLocation().getY();
			js = String.format("window.scroll(0, %s)", elementPosition - 100);
			((JavascriptExecutor) driver).executeScript(js);

			Thread.sleep(1000);			
			Util.waitAndClickElement(driver, driver.findElements(By.cssSelector(".locationLineBlock")).get(i));
			Thread.sleep(1000);
			List<WebElement> continueButton = driver.findElements(By.cssSelector(".booking-submit"));
			Util.waitAndClickElement(driver, continueButton.get(0));
			Thread.sleep(3000);

			
			// Collect Data 1
			List<WebElement> dates = driver.findElements(By.cssSelector(".date-link"));
			for (WebElement date : dates) {
				
				Document document = builder.parse(new InputSource(new StringReader(
						date.findElement(By.cssSelector(".appointmentNotice")).getAttribute("innerHTML"))));
				NodeList nList = document.getElementsByTagName("div");
				
				if (!date.getAttribute("class").contains("disabled")) {
					Slot slot = new Slot(
							driver.findElements(By.cssSelector(".locationLineBlock")).get(i).getText().split("\n")[0],
							date.getText() + " " + driver.findElements(By.cssSelector(".calendar-header")).get(0).getText().trim(), nList.item(0).getTextContent().split(" ")[0]);
					slots.add(slot);
					//System.out.println(slot);
					Util.getTable(slots);
				}
			}
			
			// .calendar-header :nth-child(3)
			driver.findElements(By.cssSelector(".calendar-header :nth-child(3)")).get(0).click();
			Thread.sleep(3000);
			// Collect Data 2
			dates = driver.findElements(By.cssSelector(".date-link"));
			for (WebElement date : dates) {
				
				Document document = builder.parse(new InputSource(new StringReader(
						date.findElement(By.cssSelector(".appointmentNotice")).getAttribute("innerHTML"))));
				NodeList nList = document.getElementsByTagName("div");
				
				if (!date.getAttribute("class").contains("disabled")) {
					Slot slot = new Slot(
							driver.findElements(By.cssSelector(".locationLineBlock")).get(i).getText().split("\n")[0],
							date.getText() + " " + driver.findElements(By.cssSelector(".calendar-header")).get(0).getText().trim(), nList.item(0).getTextContent().split(" ")[0]);
					slots.add(slot);
					//System.out.println(slot);
					Util.getTable(slots);
				}
			}

		}

		Util.getTable(slots);		
		Util.sendEmail(Util.getTableHTML(slots));
		
		driver.close();
	}

}