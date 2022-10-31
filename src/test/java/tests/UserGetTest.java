package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Flaky;
import io.qameta.allure.Issue;
import io.qameta.allure.Link;
import io.qameta.allure.Links;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import lib.Assertions;
import lib.BaseTestCase;
import org.junit.jupiter.api.Test;

@Epic("User tests")
@Feature("View User Info")
class UserGetTest extends BaseTestCase {

  @Test
  @Description("Получение данных пользователя неавторизованным пользователем")
  @Link("https://jira.project.ru/browse/TESTCASEID-100001")
  @Severity(SeverityLevel.NORMAL)
  @Flaky
  void testGetUserDataNotAuth() {
    Response responseUserData = RestAssured
        .get(USER_URL + "2")
        .andReturn();
    Assertions.assertJsonHasField(responseUserData, "username");
    Assertions.assertJsonHasNotField(responseUserData, "firstName");
    Assertions.assertJsonHasNotField(responseUserData, "lastName");
    Assertions.assertJsonHasNotField(responseUserData, "email");
  }

  @Test
  @Description("Получение данных пользователя другим авторизованным пользователем")
  @Severity(SeverityLevel.MINOR)
  @Link("https://jira.project.ru/browse/TESTCASEID-100002")
  void testGetUserDataAuthAsSameUser() {
    Map<String, String> authData = new HashMap<>();
    authData.put("email", "vinkotov@example.com");
    authData.put("password", "1234");

    Response responseGetAuth = RestAssured
        .given()
        .body(authData)
        .post(LOGIN_URL)
        .andReturn();

    String cookie = this.getCookie(responseGetAuth, "auth_sid");
    String header = this.getHeader(responseGetAuth, "x-csrf-token");

    Response responseUserData = RestAssured
        .given()
        .header("x-csrf-token", header)
        .cookie("auth_sid", cookie)
        .get(USER_URL + "2")
        .andReturn();

    String[] expectedFields = {"username", "firstName", "lastName", "email"};
    Assertions.assertJsonHasFields(responseUserData, expectedFields);
  }

  @Test
  @Description("Тест, который авторизовывается одним пользователем, но получает данные другого (т.е. с другим ID")
  @Link("https://jira.project.ru/browse/TESTCASEID-100003")
  @Severity(SeverityLevel.CRITICAL)
  void testGetUserDataAuthAsOtherUser() {
    Map<String, String> authData = new HashMap<>();
    authData.put("email", "vinkotov@example.com");
    authData.put("password", "1234");

    Response responseGetAuth = apiCoreRequests
        .makePostRequest(LOGIN_URL, authData);

    String cookie = this.getCookie(responseGetAuth, "auth_sid");
    String header = this.getHeader(responseGetAuth, "x-csrf-token");

    Response responseUserData = apiCoreRequests
        .makeGetRequest(USER_URL + "1", header, cookie);

    Assertions.assertJsonHasField(responseUserData, "username");
    Assertions.assertJsonHasNotField(responseUserData, "firstName");
    Assertions.assertJsonHasNotField(responseUserData, "lastName");
    Assertions.assertJsonHasNotField(responseUserData, "email");
  }

}
