package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import lib.Assertions;
import lib.BaseTestCase;
import org.junit.Test;

public class UserGetTest extends BaseTestCase {
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

}
