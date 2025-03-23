package com.tus.proj.selenium;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import io.github.bonigarcia.wdm.WebDriverManager;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
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
	void testAdminEditUser() {
	    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

	    // Locate the "Edit" button for user 'newUser' in the table and click it.
	    WebElement editButton = wait.until(ExpectedConditions.elementToBeClickable(
	        By.xpath("//tr[td[text()='newUser']]//button[contains(@class, 'edit-btn')]")
	    ));
	    editButton.click();

	    // Wait until the edit user modal is visible and assert its display.
	    WebElement editModal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("editUserModal")));
	    assertTrue(editModal.isDisplayed(), "Edit modal is not displayed");

	    // Enter new username and password.
	    WebElement usernameField = editModal.findElement(By.id("username"));
	    WebElement passwordField = editModal.findElement(By.id("password"));

	    usernameField.clear();
	    usernameField.sendKeys("renamedUser");

	    passwordField.clear();
	    passwordField.sendKeys("Abc123*9");

	    // Save the changes.
	    WebElement saveButton = editModal.findElement(By.id("save-or-create"));
	    saveButton.click();

	    // Wait until the modal is no longer visible.
	    wait.until(ExpectedConditions.invisibilityOf(editModal));

	    // Validate that the username has been updated in the table.
	    WebElement updatedUser = wait.until(ExpectedConditions.visibilityOfElementLocated(
	        By.xpath("//tr[td[text()='renamedUser']]")
	    ));
	    assertTrue(updatedUser.isDisplayed(), "Username not updated to 'renamedUser'");
	}
	
	@Test
	@Order(4)
	void testAdminDeleteUser() {
	    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));

	    // Locate and click the "Delete" button for the renamed user.
	    WebElement deleteButton = wait.until(ExpectedConditions.elementToBeClickable(
	        By.xpath("//tr[td[text()='renamedUser']]//button[contains(@class, 'delete-btn')]")
	    ));
	    deleteButton.click();

	    // Wait until the delete user modal appears and assert that it is displayed.
	    WebElement deleteModal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("deleteUserModal")));
	    assertTrue(deleteModal.isDisplayed(), "Delete modal is not displayed");

	    // Confirm the deletion by clicking the "Delete" button.
	    WebElement confirmDeleteButton = deleteModal.findElement(By.id("confirmDelete"));
	    confirmDeleteButton.click();

	    // Wait for the modal to close.
	    wait.until(ExpectedConditions.invisibilityOf(deleteModal));

	    // Assert that the alert appears with the message "User deleted."
	    WebElement alertMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
	        By.xpath("//div[contains(@class, 'alert-success') and text()='User deleted.']")
	    ));
	    assertTrue(alertMessage.isDisplayed(), "Success alert 'User deleted.' not displayed");

	    // Ensure the user is no longer present in the user table.
	    List<WebElement> deletedUser = driver.findElements(By.xpath("//tr[td[text()='renamedUser']]"));
	    assertTrue(deletedUser.isEmpty(), "User 'renamedUser' was not deleted");

	    // Logout to conclude the test.
	    WebElement logoutButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("admin-logout-button")));
	    logoutButton.click();
	}




	
	@Test
	@Order(5)
	void testUserCreateNote() {
		
		WebElement username = driver.findElement(By.id("username"));
		WebElement password = driver.findElement(By.id("password"));
		WebElement loginButton = driver.findElement(By.id("login-btn"));

		
		username.sendKeys("user");
		password.sendKeys("user");
		loginButton.click();

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("logout-button")));
		
		WebElement createNote = driver.findElement(By.id("create-note"));
		createNote.click();
		
	    WebElement createNoteModal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("createNoteModal")));
	    assert(createNoteModal.isDisplayed()) : "Create modal is not displayed";
	    
	    WebElement noteTitle = createNoteModal.findElement(By.id("noteTitle"));
	    noteTitle.clear();
	    noteTitle.sendKeys("Note Title");
	    
	    // Fill in the Tag field
	    WebElement noteTag = createNoteModal.findElement(By.id("noteTag"));
	    noteTag.clear();
	    noteTag.sendKeys("Tag");
	    
	    // Select Priority from the dropdown
	    WebElement notePriority = createNoteModal.findElement(By.id("notePriority"));
	    new Select(notePriority).selectByValue("HIGH");
	    
	    // Select Access from the dropdown
	    WebElement noteAccess = createNoteModal.findElement(By.id("noteAccess"));
	    new Select(noteAccess).selectByValue("PUBLIC");
	    
	    // Fill in the Deadline field (e.g., December 31, 2025)
	    WebElement noteDeadline = createNoteModal.findElement(By.id("noteDeadline"));
	    noteDeadline.clear();
	    noteDeadline.sendKeys("2025 12 12");
	    
	    // Click the "Create Note" button
	    WebElement createButton = createNoteModal.findElement(By.id("save-or-create"));
	    createButton.click();
	    
	    WebElement note = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("button.note-tile-btn[data-note-id='1']")));
	    assertNotNull(note, "Note with id 1 does not exist.");	
	}
	
	@Test
	@Order(6)
	void testEditAndSaveNoteContent() {
	    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
	    
	    // Locate the note tile with data-note-id="1" and click it.
	    WebElement noteTile = wait.until(ExpectedConditions.elementToBeClickable(
	        By.cssSelector("button.note-tile-btn[data-note-id='1']")
	    ));
	    noteTile.click();
	    
	    // Wait until the edit note modal is visible.
	    WebElement editNoteModal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("editNoteModal")));
	    assertTrue(editNoteModal.isDisplayed(), "Edit Note Modal did not appear after clicking the note tile");
	    
	    // Locate the content text area and change the note content.
	    WebElement contentTextArea = editNoteModal.findElement(By.id("editNoteContent"));
	    contentTextArea.clear();
	    contentTextArea.sendKeys("Updated note content for testing.");

	    // Click the "Save" button.
	    WebElement saveButton = editNoteModal.findElement(By.cssSelector("button.btn-primary.w-100.mt-3"));
	    saveButton.click();

	    // Wait until the modal closes.
	    wait.until(ExpectedConditions.invisibilityOf(editNoteModal));

	    WebElement alertMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
		        By.xpath("//div[contains(@class, 'alert-success') and text()='Note updated successfully!']")
		    ));
		    assertTrue(alertMessage.isDisplayed(), "Success alert 'User deleted.' not displayed");


	}

	
	@Test
	@Order(7)
	void testNoteEditModal() {
	    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));

	    // Locate the cog button for the note with id=1 and click it.
	    WebElement cogButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("cog-btn-1")));
	    cogButton.click();

	    // Wait until the "Create Note" modal is visible.
	    WebElement createNoteModal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("createNoteModal")));
	    assertTrue(createNoteModal.isDisplayed(), "Create Note Modal did not appear after clicking the cog button");

	    // Fill in the title field.
	    WebElement titleInput = createNoteModal.findElement(By.id("noteTitle"));
	    titleInput.clear();
	    titleInput.sendKeys("UpdateNote");

	    // Fill in the tag field.
	    WebElement tagInput = createNoteModal.findElement(By.id("noteTag"));
	    tagInput.clear();
	    tagInput.sendKeys("UpdatedTag");

	    // Select a priority from the dropdown.
	    WebElement priorityDropdown = createNoteModal.findElement(By.id("notePriority"));
	    Select prioritySelect = new Select(priorityDropdown);
	    prioritySelect.selectByValue("HIGH");

	    // Select an access type from the dropdown.
	    WebElement accessDropdown = createNoteModal.findElement(By.id("noteAccess"));
	    Select accessSelect = new Select(accessDropdown);
	    accessSelect.selectByValue("PRIVATE");

	    // Set the deadline to today's date.
	    WebElement deadlineInput = createNoteModal.findElement(By.id("noteDeadline"));
	    deadlineInput.sendKeys("2025 12 13");

	    // Click the "Save" button.
	    WebElement saveButton = createNoteModal.findElement(By.id("save-or-create"));
	    saveButton.click();

	    // Wait until the modal is no longer visible to ensure it's properly closed.
	    wait.until(ExpectedConditions.invisibilityOf(createNoteModal));

	    // Check for the "showAlert" popup indicating a successful update.
	    WebElement alertMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
		        By.xpath("//div[contains(@class, 'alert-success') and text()='Note updated successfully!']")
		    ));
		    assertTrue(alertMessage.isDisplayed(), "Success alert 'User deleted.' not displayed");

	}

	
	@Test
	@Order(8)
	void testOpenAndCloseDeleteNoteModal() {
	    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

	    // Locate the trash button for the note with id=1 and click it.
	    WebElement trashButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("trash-btn-1")));
	    trashButton.click();

	    // Wait until the "Delete Note" modal is visible.
	    WebElement deleteNoteModal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("deleteNoteModal")));

	    // Assert that the modal is displayed.
	    assertTrue(deleteNoteModal.isDisplayed(), "Delete Note Modal did not appear after clicking the trash button");

	    // Close the modal using the close button.
	    WebElement closeButton = deleteNoteModal.findElement(By.className("btn-close"));
	    closeButton.click();

	    // Wait until the modal is no longer visible to ensure it's properly closed.
	    wait.until(ExpectedConditions.invisibilityOf(deleteNoteModal));
	}

	
	

	
	@AfterAll
	void teardown() {
		if (driver != null) {
			driver.quit();
		}
	}
	
	
	
}
