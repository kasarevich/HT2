import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class TestJenkins {
    private static final String BASE_URL = "http://localhost:8080";
    private static final String DRIVER_PROPERTY = "webdriver.chrome.driver";
    private static final String DRIVER_PATH = "C:/java_projects/tools/chromedriver_2.45/chromedriver.exe";
    private static final String CAPABILITY_KEY = "chrome.switches";
    private static final String EMPTY_START_PAGE = "--homepage=about:blank";

    private static final String MANAGE_JENKINS_TEXT = "Manage Jenkins";
    private static final String MANAGE_USERS_TEXT = "Manage Users";
    private static final String MANAGE_USERS_SUB_TEXT = "Create/delete/modify users that can log in to this Jenkins";

    private static final String USERNAME = "someuser";
    private static final String PASSWORD = "somepassword";
    private static final String FULLNAME = "Some Full Name";
    private static final String EMAIL = "some@addr.dom";
    private static final String REMOVE_QUESTION = "Are you sure about deleting the user from Jenkins?";

    private static final String ERROR_PAGE_NOT_FOUND = "No suitable page found!";
    private static final String ERROR_MESSAGE_REF_TO_CREATE_NOT_AVAILABLE = "The reference to \"Create User\" is not available!";
    private static final String ERROR_CREATE_USER_FORM = "\"Create user\' form not found";
    private static final String ERROR_CREATE_USER_FORM_DOESNT_EMPTY = "Fields in form \"Create user\' doesn't empty";
    private static final String ERROR_USER_DOESNT_ADD = "Creating user error";
    private static final String ERROR_OF_QUESTION_DISPLAY = "Remove question wasn't shown";
    private static final String ERROR_REMOVING = "The user are still exist";
    private static final String ERROR_DELETE_ADMIN = "\"Delete admin\" is available";
    private static final String ERROR_ENABLE_REFRESH = "\"Enable refresh\" is working incorrect";

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
        Assert.assertTrue(jenkinsPageObject.isAuthPresentForReal(), ERROR_PAGE_NOT_FOUND);
    }

    /**
     * 1.	После клика по ссылке «Manage Jenkins» на странице появляется элемент dt
     * с текстом «Manage Users» и элемент dd с текстом «Create/delete/modify users that can log in to this Jenkins».
      */
    @Test(dependsOnMethods = "testAuth")
    public void testManageJenkinsRef(){
        jenkinsPageObject.signIn();
        String errorsOfSearchManage = jenkinsPageObject.getErrorOnTextAbsence(MANAGE_JENKINS_TEXT);
        Assert.assertEquals(errorsOfSearchManage, "");
        jenkinsPageObject.clickManageJenkins();
        Assert.assertTrue(jenkinsPageObject.isManageJenkinsPresentForReal(), ERROR_PAGE_NOT_FOUND);
        verificationErrors.append(jenkinsPageObject.getErrorOnTextAbsence(MANAGE_USERS_TEXT));
        verificationErrors.append(jenkinsPageObject.getErrorOnTextAbsence(MANAGE_USERS_SUB_TEXT));
    }

    /**
     * 2.	После клика по ссылке, внутри которой содержится элемент dt с текстом «Manage Users»,
     * становится доступна ссылка «Create User».
     */
    @Test(dependsOnMethods = "testManageJenkinsRef")
    public void testManageUsers(){
        jenkinsPageObject.clickManageUsers();
        Assert.assertTrue(jenkinsPageObject.isRefToCreateUserAvailable(), ERROR_MESSAGE_REF_TO_CREATE_NOT_AVAILABLE);
    }

    /**
     * 3.	После клика по ссылке «Create User» появляется форма с тремя полями типа text и двумя
     * полями типа password, причём все поля должны быть пустыми.
     */
    @Test(dependsOnMethods = "testManageUsers")
    public void testCreateUser(){
        jenkinsPageObject.clickCreateUser();
        Assert.assertTrue(jenkinsPageObject.isCreateUserFormPresentForReal(), ERROR_CREATE_USER_FORM);
        Assert.assertTrue(jenkinsPageObject.isFormsCreateUserEmpty(), ERROR_CREATE_USER_FORM_DOESNT_EMPTY);
    }

    /**
     * 4.	После заполнения полей формы («Username» = «someuser», «Password» = «somepassword»,
     * «Confirm password» = «somepassword», «Full name» = «Some Full Name»,
     * «E-mail address» = «some@addr.dom») и клика по кнопке с надписью «Create User»
     * на странице появляется строка таблицы (элемент tr), в которой есть ячейка
     * (элемент td) с текстом «someuser».
     */
    @Test(dependsOnMethods = "testCreateUser")
    public void testAddNewUser(){
        Assert.assertTrue(
                jenkinsPageObject
                .setUsername(USERNAME)
                .setPassword(PASSWORD)
                .setConfirmPassword(PASSWORD)
                .setFullName(FULLNAME)
                .setEmail(EMAIL)
                        .clickCreateNewUserWithData()
                            .isNewUserAdded()
        , ERROR_USER_DOESNT_ADD);
    }

    /**
     * 5.	После клика по ссылке с атрибутом href равным «user/someuser/delete» появляется
     * текст «Are you sure about deleting the user from Jenkins?».
     * 6.	После клика по кнопке с надписью «Yes» на странице отсутствует строка таблицы (элемент tr),
     * с ячейкой (элемент td) с текстом «someuser».
     * На странице отсутствует ссылка с атрибутом href равным «user/someuser/delete».
     * 7.	{На той же странице, без выполнения каких бы то ни было действий}.
     * На странице отсутствует ссылка с атрибутом href равным
     */
    @Test(dependsOnMethods = "testAddNewUser")
    public void testDeleteUser(){
        jenkinsPageObject.clickDeleteUser();
        Assert.assertTrue(jenkinsPageObject.getErrorOnTextAbsence(REMOVE_QUESTION).equalsIgnoreCase(""), ERROR_OF_QUESTION_DISPLAY);
        jenkinsPageObject.clickConfirmDelete();
        Assert.assertFalse(jenkinsPageObject.isNewUserAdded(), ERROR_REMOVING);
        Assert.assertFalse(jenkinsPageObject.isDeleteButtonDisplayed(), ERROR_REMOVING);
        Assert.assertFalse(jenkinsPageObject.isDeleteAdminPresentForReal(), ERROR_DELETE_ADMIN);
        jenkinsPageObject.isAutoRefreshEnabled();
    }

    /**
     * 3(optional).	При клике по ссылке с текстом «ENABLE AUTO REFRESH» эта ссылка пропадает,
     * а вместо неё появляется ссылка с текстом «DISABLE AUTO REFRESH».
     * При клике по ссылке с текстом «DISABLE AUTO REFRESH» эта ссылка пропадает,
     * а вместо неё появляется ссылка с текстом «ENABLE AUTO REFRESH».
     * Т.е. эти две ссылки циклически сменяют друг друга.
     */
    @Test(dependsOnMethods = "testAddNewUser")
    public void testAutoRefresh(){
        boolean isEnabled = jenkinsPageObject.isAutoRefreshEnabled();
        jenkinsPageObject.clickAutoRefresh();
        Assert.assertNotEquals(jenkinsPageObject.isAutoRefreshEnabled(), isEnabled, ERROR_ENABLE_REFRESH);
        jenkinsPageObject.clickAutoRefresh();
        Assert.assertEquals(jenkinsPageObject.isAutoRefreshEnabled(), isEnabled, ERROR_ENABLE_REFRESH);
    }
}
