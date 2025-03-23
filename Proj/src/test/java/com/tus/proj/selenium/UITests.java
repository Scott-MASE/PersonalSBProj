package com.tus.proj.selenium;

import java.time.Duration;

import io.github.bonigarcia.wdm.WebDriverManager;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UITests {
	private WebDriver driver;
	private final String BASE_URL = "http://localhost:9092"; // Update if necessary

	@BeforeAll
	void setup() {
		WebDriverManager.chromedriver().setup();
		driver = new ChromeDriver();
		driver.get(BASE_URL);
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10)); // New approach to set implicit wait
	}
	
	
	@Test
	@Order(1)
	void testRegistration() {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));

		WebElement regLink = driver.findElement(By.id("reg-link"));
		regLink.click();
		
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("registration-form")));
		
		WebElement username = driver.findElement(By.id("username"));
		WebElement password = driver.findElement(By.id("password"));
		WebElement passwordConf = driver.findElement(By.id("confirm-password"));
		WebElement registerButton = driver.findElement(By.id("register-btn"));
		
		username.sendKeys("newUser");
		password.sendKeys("Abc123*8");
		passwordConf.sendKeys("Abc123*8");
		registerButton.click();
		
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("login-form")));

		
		
	}
	
	@Test
	@Order(2)
	void testAdminLogin() {
		
		WebElement username = driver.findElement(By.id("username"));
		WebElement password = driver.findElement(By.id("password"));
		WebElement loginButton = driver.findElement(By.id("login-btn"));

		
		username.sendKeys("admin");
		password.sendKeys("admin");
		loginButton.click();

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
		WebElement dataImportElement = wait
				.until(ExpectedConditions.visibilityOfElementLocated(By.id("admin-logout-button")));

	}
	
	@Test
	@Order(3)
	void testAdminEditAndDelete() {
		
	}
	
	@AfterAll
	void teardown() {
		if (driver != null) {
			driver.quit();
		}
	}
	
	
	
}
