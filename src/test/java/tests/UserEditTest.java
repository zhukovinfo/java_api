package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Link;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

@Epic("User tests")
@Feature("Edit user data")
class UserEditTest extends BaseTestCase {

  private Map<String, String> testUserData;
  private String testUserId;

  @BeforeEach
  public void createTestUser() {
    testUserData = DataGenerator.getRegistrationData();

    JsonPath responseCreateAuth = RestAssured
        .given()
        .body(testUserData)
        .post(USER_URL)
        .jsonPath();

    testUserId = responseCreateAuth.getString("id");
  }

  @Test
  @Description("Создание нового пользователя")
  @Link("https://jira.project.ru/browse/TESTCASEID-100009")
  @Severity(SeverityLevel.NORMAL)
  void testEditJustCreatedTest() {
    //LOGIN
    Response responseGetAuth = login(testUserData.get("email"), testUserData.get("password"));
    String token = this.getHeader(responseGetAuth, "x-csrf-token");
    String cookie = this.getCookie(responseGetAuth, "auth_sid");

    //EDIT
    String newName = "Changed Name";
    Map<String, String> editData = new HashMap<>();
    editData.put("firstName", newName);

    apiCoreRequests
        .makePutRequest( USER_URL + testUserId, editData, token, cookie);

    //GET
    Response responseUserData = apiCoreRequests
        .makeGetRequest(USER_URL + testUserId, token, cookie);

    Assertions.assertJsonByName(responseUserData, "firstName", newName);
  }

  @Test
  @Description("Изменение данных пользователя, будучи неавторизованными")
  @Link("https://jira.project.ru/browse/TESTCASEID-100010")
  @Severity(SeverityLevel.NORMAL)
  void testEditUserByNotAuthorizedUser() {
    Map<String, String> editData = new HashMap<>();
    editData.put("firstName", "New Name");

    Response responseUserData = apiCoreRequests
        .makePutRequest(USER_URL + testUserId, editData);

    Assertions.assertResponseCodeEquals(responseUserData, 400);
    Assertions.assertResponseTextEquals(responseUserData, "Auth token not supplied");
  }

  @Test
  @Description("Изменение данных пользователя, будучи авторизованными другим пользователем")
  @Link("https://jira.project.ru/browse/TESTCASEID-100011")
  @Severity(SeverityLevel.CRITICAL)
  void testEditUserByOtherAuthorizedUser() {
    Response responseGetAuth = login("vinkotov@example.com","1234");

    String token = this.getHeader(responseGetAuth, "x-csrf-token");
    String cookie = this.getCookie(responseGetAuth, "auth_sid");

    Map<String, String> editData = new HashMap<>();
    editData.put("firstName", "New Name");

    Response responseUserData = apiCoreRequests
        .makePutRequest(USER_URL + testUserId, editData, token, cookie);

    Assertions.assertResponseCodeEquals(responseUserData, 400);
    Assertions.assertResponseTextEquals(responseUserData,
        "Please, do not edit test users with ID 1, 2, 3, 4 or 5.");
  }

  @Test
  @Description("Изменение email пользователя, будучи авторизованными тем же пользователем, на новый email без "
      + "символа @")
  @Link("https://jira.project.ru/browse/TESTCASEID-100012")
  @Severity(SeverityLevel.MINOR)
  void testEditEmailOnInvalidValue() {
    Response responseGetAuth = login(testUserData.get("email"), testUserData.get("password"));
    String token = this.getHeader(responseGetAuth, "x-csrf-token");
    String cookie = this.getCookie(responseGetAuth, "auth_sid");

    Map<String, String> editData = new HashMap<>();
    editData.put("email", "newmail.com");
    testUserData = DataGenerator.getRegistrationData(editData);

    Response responseUserData = apiCoreRequests
        .makePutRequest(USER_URL + testUserId, testUserData, token, cookie);

    Assertions.assertResponseCodeEquals(responseUserData, 400);
    Assertions.assertResponseTextEquals(responseUserData, "Invalid email format");
  }

  @Test
  @Description("Изменение firstName пользователя, будучи авторизованными тем же пользователем, на очень "
      + "короткое значение в один символ")
  @Severity(SeverityLevel.MINOR)
  @Link("https://jira.project.ru/browse/TESTCASEID-100013")
  void testEditFirstNameOnShortValue() {
    Response responseGetAuth = login(testUserData.get("email"), testUserData.get("password"));
    String token = this.getHeader(responseGetAuth, "x-csrf-token");
    String cookie = this.getCookie(responseGetAuth, "auth_sid");

    Map<String, String> editData = new HashMap<>();
    editData.put("firstName", RandomStringUtils.randomAlphabetic(1).toLowerCase());
    testUserData = DataGenerator.getRegistrationData(editData);

    Response responseUserData = apiCoreRequests
        .makePutRequest(USER_URL + testUserId, testUserData, token, cookie);

    Assertions.assertResponseCodeEquals(responseUserData, 400);
    Assertions.assertJsonByName(responseUserData, "error", "Too short value for field firstName");
  }
}
