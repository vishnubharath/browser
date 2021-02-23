package com.vbj.automate.browser;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.vbj.automate.browser.model.Slot;

import dnl.utils.text.table.TextTable;

public class Util {

	public static boolean waitAndClick(WebDriver driver, String xPath) {
		System.out.println("Waiting for " + xPath);
		new WebDriverWait(driver, 10000).until(webDriver -> driver.findElement(By.xpath(xPath)));
		System.out.println("Found " + xPath);
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		driver.findElement(By.xpath(xPath)).click();

		return true;
	}
	
	public static boolean waitAndClickCss(WebDriver driver, String cssSelector) {
		System.out.println("Waiting for " + cssSelector);
		new WebDriverWait(driver, 10000).until(webDriver -> driver.findElement(By.cssSelector(cssSelector)));
		System.out.println("Found " + cssSelector);
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		driver.findElement(By.cssSelector(cssSelector)).click();

		return true;
	}
	
	public static boolean waitAndClickElement(WebDriver driver, WebElement element) {
		System.out.println("Waiting for " + element);
		new WebDriverWait(driver, 10000).until(webDriver -> element);
		System.out.println("Found " + element);
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		element.click();

		return true;
	}

	public static boolean waitForPageLoad(WebDriver driver) {
		new WebDriverWait(driver, 10000).until(webDriver -> ((JavascriptExecutor) webDriver)
				.executeScript("return document.readyState").equals("complete"));
		return true;
	}
	
	public static boolean waitAndCompare(WebDriver driver, String xPath, String compareText) throws InterruptedException {
		new WebDriverWait(driver, 10000).until(webDriver -> driver.findElement(By.xpath(xPath)));
        while(driver.findElement(By.xpath(xPath)) != null) {
        	System.out.println(driver.findElement(By.xpath(xPath)).getText());
        	if(driver.findElement(By.xpath(xPath)).getText() != null && 
        			driver.findElement(By.xpath(xPath)).getText().equalsIgnoreCase(compareText)) {
                driver.findElement(By.xpath(xPath)).click();
                break;
        	}else {
        		System.out.println("Wating for element " + xPath);
                Thread.sleep(1000);
        	}
        }
        
        return true;
	}
	
	public static String prettyPrint(List<Slot> slots) {
		
		StringBuilder prettyString = new StringBuilder();
		
		int locationMax = 0;
		int availableMax = 0;
		int daysMax = 0;
		for (Slot slot : slots) {
			locationMax = findMax(locationMax, slot.location);
			availableMax = findMax(availableMax, slot.available);
			daysMax = findMax(daysMax, slot.available);
			
		}
		System.out.println(locationMax);
		System.out.println(availableMax);
		System.out.println(daysMax);
		
		// Print Table
//	
//		System.out.print(String.format("%-" + locationMax + "s", "LOCATION :-"));
//		System.out.print(String.format("%-" + daysMax + "s", "DAY :-"));
//		System.out.println(String.format("%-" + availableMax + "s", "AVAILABLE :-"));
//		
//		prettyString.append(String.format("%-" + locationMax + "s", "LOCATION :-"));
//		prettyString.append(String.format("%-" + daysMax + "s", "DAY :-"));
//		prettyString.append(String.format("%-" + availableMax + "s", "AVAILABLE :-") + "\n");
		
		for (Slot slot : slots) {
			System.out.print(String.format("%-" + locationMax + "s", slot.location));
			System.out.print(String.format("%-" + daysMax + "s", slot.day) + "\n");
			//System.out.print(String.format("%-" + availableMax + "s", slot.available));
			System.out.println();
			
			prettyString.append(String.format("%-" + locationMax + "s", slot.location));
			prettyString.append(String.format("%-" + daysMax + "s", slot.day) + "\n");
			//prettyString.append(String.format("%-" + availableMax + "s", slot.available) + "\n");
		}
		
		return prettyString.toString();
	}
	
	public static String getTable(List<Slot> slots){
		String[] columnNames = {"location", "days"};
		Object[][] slotsx = new String[slots.size()][2];
		for(int i =0;i < slots.size(); i++) {
			slotsx[i][0] = slots.get(i).location;
			slotsx[i][1] = slots.get(i).day;
		}
		
		
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		TextTable tt = new TextTable(columnNames, slotsx);
		tt.setAddRowNumbering(true);
		tt.printTable(new PrintStream(outputStream), 0);
		
        String output = null;
        try {
            output = outputStream.toString("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
		
        System.out.println(output);
        
        return output;
	}

	public static int findMax(int maxSize, String val) {
		if(val.contains("\n")) {
			String[] snippets = val.split("\n");
			int maxsizesnippet = 0;				 
			for(String snippet : snippets ) {
				if(snippet.length() > maxsizesnippet)
					maxsizesnippet = snippet.length();
			}
			maxSize = maxsizesnippet;
		}else {
			if (val.length() > maxSize)
				maxSize = val.length();
		}
		return maxSize;
	}
	
	public static void sendEmail(String table) {
		final String username = "maxapplog@gmail.com";

        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        final String password = "noispasswordneeded";
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true"); //TLS
        
        Session session = Session.getInstance(prop,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("maxapplog@gmail.com"));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse("vishnubharathj@gmail.com")
            );
            message.setSubject("Drive Test Availability");
			message.setText("time stamp \n" +
            table);

            Transport.send(message);

            System.out.println("Done");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
	
}
