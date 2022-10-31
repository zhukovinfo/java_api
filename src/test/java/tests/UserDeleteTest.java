package tests;

import io.qameta.allure.Description;
import io.restassured.response.Response;
import java.util.Map;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.Test;

class UserDeleteTest extends BaseTestCase {

  @Test
  @Description("Удаление пользователя ID 2 запрещено")
  void testRemoveUser2Denied() {
    Response responseGetAuth = login("vinkotov@example.com", "1234");
    String token = this.getHeader(responseGetAuth, "x-csrf-token");
    String cookie = this.getCookie(responseGetAuth, "auth_sid");

    Response responseDelete = apiCoreRequests.makeDeleteRequest(USER_URL + 2, token, cookie);

    Assertions.assertResponseCodeEquals(responseDelete, 400);
    Assertions.assertResponseTextEquals(responseDelete,
        "Please, do not delete test users with ID 1, 2, 3, 4 or 5.");
  }

  @Test
  @Description("Успешное удаление пользователя")
  void testRemoveUserSuccessfully() {
    //CREATE USER
    Map<String, String> testUserData = DataGenerator.getRegistrationData();
    Response responseCreateAuth = apiCoreRequests.makePostRequest(USER_URL, testUserData);
    String testUserId = responseCreateAuth.jsonPath().getString("id");

    //LOGIN
    Response responseGetAuth = login(testUserData.get("email"), testUserData.get("password"));
    String token = this.getHeader(responseGetAuth, "x-csrf-token");
    String cookie = this.getCookie(responseGetAuth, "auth_sid");

    //DELETE
    Response responseDelete = apiCoreRequests.makeDeleteRequest(USER_URL + testUserId, token, cookie);
    Assertions.assertResponseCodeEquals(responseDelete, 200);

    //GET USER DATA
    Response responseUserData = apiCoreRequests.makeGetRequest(USER_URL + testUserId, token, cookie);
    Assertions.assertResponseCodeEquals(responseUserData, 404);
  }

  @Test
  @Description("Удаление пользователя, будучи авторизованным другим пользователем")
  void createTestUser() {
    //CREATE USER
    Map<String, String> testUserData = DataGenerator.getRegistrationData();
    Response responseCreateAuth = apiCoreRequests.makePostRequest(USER_URL, testUserData);
    String testUserId = responseCreateAuth.jsonPath().getString("id");

    //LOGIN
    Response responseGetAuth = login("vinkotov@example.com", "1234");
    String token = this.getHeader(responseGetAuth, "x-csrf-token");
    String cookie = this.getCookie(responseGetAuth, "auth_sid");

    //DELETE
    Response responseDelete = apiCoreRequests.makeDeleteRequest(USER_URL + testUserId, token, cookie);
    Assertions.assertResponseCodeEquals(responseDelete, 400);
    Assertions.assertResponseTextEquals(responseDelete,
        "Please, do not delete test users with ID 1, 2, 3, 4 or 5.");
  }

}
