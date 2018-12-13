import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class TestJenkins {
    private final static String BASE_URL = "http://localhost:8080";
    private final static String DRIVER_PROPERTY = "webdriver.chrome.driver";
    private final static String DRIVER_PATH = "C:/java_projects/tools/chromedriver_2.45/chromedriver.exe";
    private final static String CAPABILITY_KEY = "chrome.switches";
    private final static String EMPTY_START_PAGE = "--homepage=about:blank";
    private static final String PAGE_NOT_FOUND_MESSAGE = "No suitable page found!";
    private static final String MANAGE_JENKINS_TEXT = "Manage Jenkins";
    private static final String MANAGE_USERS_TEXT = "Manage Users";
    private static final String MANAGE_USERS_SUB_TEXT = "Create/delete/modify users that can log in to this Jenkins";
    private static final String ERROR_MESSAGE_REF_TO_CREATE_NOT_AVAILABLE = "The reference to \"Create User\" is not available!";

    StringBuffer verificationErrors = new StringBuffer();
    WebDriver driver = null;
    JenkinsPageObject jenkinsPageObject;

    @BeforeClass
    public void beforeClass(){
        System.setProperty(DRIVER_PROPERTY, DRIVER_PATH);
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setCapability(CAPABILITY_KEY, EMPTY_START_PAGE);
        driver = new ChromeDriver(chromeOptions);
        driver.get(BASE_URL);
        jenkinsPageObject = PageFactory.initElements(driver, JenkinsPageObject.class);
    }

    @AfterClass
    public void afterClass(){
        String verificationErrorsText = verificationErrors.toString();
        if(!verificationErrorsText.equals("")){
            System.out.println(verificationErrorsText);
            Assert.fail(verificationErrorsText);
        }
    }

    @Test
    public void testAuth(){
        Assert.assertTrue(jenkinsPageObject.isAuthPresentForReal(), PAGE_NOT_FOUND_MESSAGE);
    }

    //  	После клика по ссылке «Manage Jenkins» на странице появляется элемент dt с
//      текстом «Manage Users» и элемент dd с текстом «Create/delete/modify users that can log in to this Jenkins»
// тест падает, если чьто-либо не нашлось.
    @Test(dependsOnMethods = "testAuth")
    public void testManageJenkinsRef(){
        jenkinsPageObject.signIn();
        String errorsOfSearchManage = jenkinsPageObject.getErrorOnTextAbsence(MANAGE_JENKINS_TEXT);
        verificationErrors.append(errorsOfSearchManage);
        Assert.assertEquals(errorsOfSearchManage, "");
        jenkinsPageObject.clickManageJenkins();
        Assert.assertTrue(jenkinsPageObject.isManageJenkinsPresentForReal(), PAGE_NOT_FOUND_MESSAGE);
        verificationErrors.append(jenkinsPageObject.getErrorOnTextAbsence(MANAGE_USERS_TEXT));
        verificationErrors.append(jenkinsPageObject.getErrorOnTextAbsence(MANAGE_USERS_SUB_TEXT));
    }

    @Test(dependsOnMethods = "testManageJenkinsRef")
    public void testManageUsers(){
        jenkinsPageObject.clickManageUsers();
        Assert.assertTrue(jenkinsPageObject.isRefToCreateUserAvailable(), ERROR_MESSAGE_REF_TO_CREATE_NOT_AVAILABLE);// fixme message
    }

    @Test(dependsOnMethods = "testManageUsers")
    public void testCreateUser(){
        jenkinsPageObject.clickCreateUser();
        Assert.assertTrue(jenkinsPageObject.isCreateUserFormPresentForReal());// fixme message
        Assert.assertTrue(jenkinsPageObject.isFormsCreateUserEmpty());// fixme message
    }

    @Test(dependsOnMethods = "testCreateUser")
    public void testAddNewUser(){

    }
}
