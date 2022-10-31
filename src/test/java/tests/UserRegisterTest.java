package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.Test;

class UserRegisterTest extends BaseTestCase {

  @Test
  void testCreateUserWithExistingEmail() {
    String email = "vinkotov@example.com";

    Map<String, String> userData = new HashMap<>();
    userData.put("email", email);
    userData = DataGenerator.getRegistrationData(userData);

    Response responseCreateAuth = RestAssured
        .given()
        .body(userData)
        .post("https://playground.learnqa.ru/api/user")
        .andReturn();

    Assertions.asserResponseCodeEquals(responseCreateAuth, 400);
    Assertions.asserResponseTextEquals(responseCreateAuth,
        "Users with email '" + email + "' already exists");
  }

  @Test
  void testCreateUserSuccessfully() {
    Map<String, String> userData = DataGenerator.getRegistrationData();

    Response responseCreateAuth = RestAssured
        .given()
        .body(userData)
        .post("https://playground.learnqa.ru/api/user")
        .andReturn();

    Assertions.asserResponseCodeEquals(responseCreateAuth, 200);
    Assertions.assertJsonHasField(responseCreateAuth, "id");
  }
}
