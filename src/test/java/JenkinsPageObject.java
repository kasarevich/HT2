import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class JenkinsPageObject {
    private final static String START_URL = "http://localhost:8080/login?from=%2F";
    private final static String START_TITLE = "Sign in [Jenkins]";
    private final static String LOGIN = "aleksey";
    private final static String PASSWORD = "kasarevich";
    private WebDriverWait wait;
    private final WebDriver driver;

    @FindBy(xpath = "//body")
    private WebElement body;

    @FindBy(name = "j_username")
    private WebElement j_username;

    @FindBy(name = "j_password")
    private WebElement j_password;

    @FindBy(name = "Submit")
    private WebElement signInButton;

    @FindBy(xpath = "//div[@id='tasks']/div[6]/a[last()]")
    private WebElement manageJenkins;

    @FindBy(xpath = "//div[@id='main-panel']/div[16]")
    private WebElement manageUsers;

    @FindBy(linkText = "Create User")
    private WebElement createUser;

    @FindBy(id = "username")
    private WebElement usernameInputField;

    @FindBy(xpath = "//*[@id='main-panel']/form/div/table/tbody/tr/td/input[@name='password1']")
    private WebElement passwordInputField;

    @FindBy(xpath = "//*[@id='main-panel']/form/div/table/tbody/tr/td/input[@name='password2']")
    private WebElement confirmPasswordInputField;

    @FindBy(xpath = "//*[@id='main-panel']/form/div/table/tbody/tr/td/input[@name='fullname']")
    private WebElement fullNameInputField;

    @FindBy(xpath = "//*[@id='main-panel']/form/div/table/tbody/tr/td/input[@name='email']")
    private WebElement emailInputField;


    public JenkinsPageObject(WebDriver driver){
        this.driver = driver;
        this.wait = new WebDriverWait(this.driver, 30);
        if((!driver.getTitle().equals(START_TITLE)) || (!driver.getCurrentUrl().equals(START_URL))){
            throw new IllegalStateException("Wrong site page!");
        }
    }

    public boolean isAuthPresentForReal(){
        wait.until(ExpectedConditions.numberOfElementsToBe(By.xpath("//html/body"), 1));
        Collection<WebElement> forms = driver.findElements(By.tagName("form"));
        if (forms.isEmpty()){
            return false;
        }
        Iterator<WebElement> i = forms.iterator();
        boolean isFormFound = false;
        WebElement form = null;
        try {
            while (i.hasNext()){
                form = i.next();
                if((form.findElement(By.id("j_username")).getAttribute("type").equalsIgnoreCase("text"))
                        && (form.findElement(By.name("j_password")).getAttribute("type").equalsIgnoreCase("password"))
                        && (form.findElement(By.name("Submit")).getAttribute("type").equalsIgnoreCase("submit")) ){
                    isFormFound = true;
                    break;
                }
            }
        }catch (NoSuchElementException e){
            isFormFound = false;
        }
        return isFormFound;
    }

    public boolean isManageJenkinsPresentForReal(){
        wait.until(ExpectedConditions.numberOfElementsToBe(By.xpath("//html/body"), 1));
        Collection<WebElement> descriptions = driver.findElements(By.tagName("dl"));
        if(descriptions.isEmpty()){
            return false;
        }
        Iterator<WebElement> i = descriptions.iterator();
        boolean isPageFound = false;
        WebElement description = null;
        try {
            while (i.hasNext()){
                description = i.next();
                if((description.findElement(By.tagName("dt")).getText().equalsIgnoreCase("Manage Users")) &&
                        (description.findElement(By.tagName("dd")).getText().equalsIgnoreCase("Create/delete/modify users that can log in to this Jenkins"))){
                    isPageFound = true;
                    break;
                }
            }
        }catch (NoSuchElementException e){
            isPageFound = false;
        }
        return isPageFound;
    }

    public boolean isRefToCreateUserAvailable(){
        try {
            driver.findElement(By.linkText("Create User"));
            return true;
        }catch (NoSuchElementException e){
            return false;
        }
    }

    public boolean isCreateUserFormPresentForReal(){
        wait.until(ExpectedConditions.numberOfElementsToBe(By.xpath("//html/body"), 1));
        Collection<WebElement> forms = driver.findElements(By.tagName("table"));
        if (forms.isEmpty()){
            return false;
        }
        Iterator<WebElement> i = forms.iterator();
        boolean isFormFound = false;
        WebElement form = null;
        try {
            while (i.hasNext()){
                form = i.next();
                if((       form.findElement(By.id("username")).getAttribute("type").equalsIgnoreCase("text"))
                        && form.findElement(By.name("password1")).getAttribute("type").equalsIgnoreCase("password")
                        && form.findElement(By.name("password2")).getAttribute("type").equalsIgnoreCase("password")
                        && form.findElement(By.name("fullname")).getAttribute("type").equalsIgnoreCase("text")
                        && form.findElement(By.name("email")).getAttribute("type").equalsIgnoreCase("text")){
                    isFormFound = true;
                    break;
                }
            }
        }catch (NoSuchElementException e){
            isFormFound = false;
        }
        return isFormFound;
    }

    public boolean isFormsCreateUserEmpty(){
            if(    usernameInputField.getAttribute("value").equalsIgnoreCase("")
                && passwordInputField.getAttribute("value").equalsIgnoreCase("")
                && confirmPasswordInputField.getAttribute("value").equalsIgnoreCase("")
                && fullNameInputField.getAttribute("value").equalsIgnoreCase("")
                && emailInputField.getAttribute("value").equalsIgnoreCase("")){
            return true;
        }
        return false;
    }

    // Проверка вхождения подстроки в текст страницы.
    public boolean pageTextContains(String search_string){
        return body.getText().contains(search_string);
    }

    public String getErrorOnTextAbsence(String search_string){
        if (!pageTextContains(search_string)) {
            return "No '" + search_string + "' is found inside page text!\n";
        } else {
            return "";
        }
    }

    public JenkinsPageObject signIn() {
        j_username.clear();
        j_password.clear();
        j_username.sendKeys(LOGIN);
        j_password.sendKeys(PASSWORD);
        signInButton.click();
        return this;
    }

    public JenkinsPageObject setUsername(String username){
        usernameInputField.sendKeys(username);
        return this;
    }
    public JenkinsPageObject setPassword(String password){
        passwordInputField.sendKeys(password);
        return this;
    }
    public JenkinsPageObject setConfirmPassword(String confirmedPass){
        confirmPasswordInputField.sendKeys(confirmedPass);
        return this;
    }
    public JenkinsPageObject setFullName(String fullname){
        fullNameInputField.sendKeys(fullname);
        return this;
    }
    public JenkinsPageObject setEmail(String email){
        emailInputField.sendKeys(email);
        return this;
    }

    public JenkinsPageObject clickManageJenkins() {
        manageJenkins.click();
        return this;
    }

    public JenkinsPageObject clickManageUsers(){
        manageUsers.click();
        return this;
    }

    public JenkinsPageObject clickCreateUser(){
        createUser.click();
        return this;
    }





}
