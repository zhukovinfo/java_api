package tests;

import io.qameta.allure.Description;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import org.junit.Test;

public class UserGetTest extends BaseTestCase {

  ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

  @Test
  public void testGetUserDataNotAuth() {
    Response responseUserData = RestAssured
        .get("https://playground.learnqa.ru/api/user/2")
        .andReturn();
    Assertions.assertJsonHasField(responseUserData, "username");
    Assertions.assertJsonHasNotField(responseUserData, "firstName");
    Assertions.assertJsonHasNotField(responseUserData, "lastName");
    Assertions.assertJsonHasNotField(responseUserData, "email");
  }

  @Test
  public void testGetUserDataAuthAsSameUser() {
    Map<String, String> authData = new HashMap<>();
    authData.put("email", "vinkotov@example.com");
    authData.put("password", "1234");

    Response responseGetAuth = RestAssured
        .given()
        .body(authData)
        .post("https://playground.learnqa.ru/api/user/login")
        .andReturn();

    String cookie = this.getCookie(responseGetAuth, "auth_sid");
    String header = this.getHeader(responseGetAuth, "x-csrf-token");

    Response responseUserData = RestAssured
        .given()
        .header("x-csrf-token", header)
        .cookie("auth_sid", cookie)
        .get("https://playground.learnqa.ru/api/user/2")
        .andReturn();

    String[] expectedFields = {"username", "firstName", "lastName", "email"};
    Assertions.assertJsonHasFields(responseUserData, expectedFields);
  }


  @Test
  @Description("=Тест, который авторизовывается одним пользователем, но получает данные другого (т.е. с другим ID")
  public void testGetUserDataAuthAsOtherUser() {
    Map<String, String> authData = new HashMap<>();
    authData.put("email", "vinkotov@example.com");
    authData.put("password", "1234");

    Response responseGetAuth = apiCoreRequests
        .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

    String cookie = this.getCookie(responseGetAuth, "auth_sid");
    String header = this.getHeader(responseGetAuth, "x-csrf-token");

    Response responseUserData = apiCoreRequests
        .makeGetRequest("https://playground.learnqa.ru/api/user/1", header, cookie);

    Assertions.assertJsonHasField(responseUserData, "username");
    Assertions.assertJsonHasNotField(responseUserData, "firstName");
    Assertions.assertJsonHasNotField(responseUserData, "lastName");
    Assertions.assertJsonHasNotField(responseUserData, "email");
  }

}
