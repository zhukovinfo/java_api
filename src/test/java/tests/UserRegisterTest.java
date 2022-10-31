package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Link;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@Epic("User tests")
@Feature("Registration")
class UserRegisterTest extends BaseTestCase {

  @Test
  @Description("Создание пользователя с существующим email")
  @Link("https://jira.project.ru/browse/TESTCASEID-100013")
  @Severity(SeverityLevel.NORMAL)
  void testCreateUserWithExistingEmail() {
    String email = "vinkotov@example.com";

    Map<String, String> userData = new HashMap<>();
    userData.put("email", email);
    userData = DataGenerator.getRegistrationData(userData);

    Response responseCreateAuth = apiCoreRequests.makePostRequest(USER_URL, userData);

    Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
    Assertions.assertResponseTextEquals(responseCreateAuth,
        "Users with email '" + email + "' already exists");
  }

  @Test
  @Description("Успешное создание пользователя")
  @Link("https://jira.project.ru/browse/TESTCASEID-100014")
  @Severity(SeverityLevel.CRITICAL)
  void testCreateUserSuccessfully() {
    Map<String, String> userData = DataGenerator.getRegistrationData();

    Response responseCreateAuth = apiCoreRequests.makePostRequest(USER_URL, userData);

    Assertions.assertResponseCodeEquals(responseCreateAuth, 200);
    Assertions.assertJsonHasField(responseCreateAuth, "id");
  }

  @Test
  @Description("Создание пользователя с некорректным email")
  @Link("https://jira.project.ru/browse/TESTCASEID-100015")
  @Severity(SeverityLevel.MINOR)
  void testCreateUserWithInvalidEmail() {
    Map<String, String> userData = new HashMap<>();
    userData.put("email", "vinkotovexample.com");
    userData = DataGenerator.getRegistrationData(userData);

    Response response = apiCoreRequests.makePostRequest(USER_URL, userData);

    Assertions.assertResponseCodeEquals(response, 400);
    Assertions.assertResponseTextEquals(response, "Invalid email format");
  }

  @ParameterizedTest
  @ValueSource(strings = {"email",  "password", "username", "firstName", "lastName"})
  @Description("Создание пользователя без указания одного из полей")
  @Link("https://jira.project.ru/browse/TESTCASEID-100016")
  @Severity(SeverityLevel.NORMAL)
  void testCreateUserWithoutRequiredField(String param) {
    Map<String, String> userData = new HashMap<>();
    userData.put(param, null);
    userData = DataGenerator.getRegistrationData(userData);

    Response response = apiCoreRequests.makePostRequest(USER_URL, userData);

    Assertions.assertResponseCodeEquals(response, 400);
    Assertions.assertResponseTextEquals(response, "The following required params are missed: "+param);
  }

  @Test
  @Description("Создание пользователя с очень коротким именем в один символ")
  @Link("https://jira.project.ru/browse/TESTCASEID-100017")
  @Severity(SeverityLevel.MINOR)
  void testCreateUserWithShortFirstName() {
    String firstName = RandomStringUtils.randomAlphabetic(1).toLowerCase();
    Map<String, String> userData = new HashMap<>();
    userData.put("firstName", firstName);
    userData = DataGenerator.getRegistrationData(userData);

    Response response = apiCoreRequests.makePostRequest(USER_URL, userData);

    Assertions.assertResponseCodeEquals(response, 400);
    Assertions.assertResponseTextEquals(response, "The value of 'firstName' field is too short");
  }

  @Test
  @Description("Создание пользователя с очень длинным именем - длиннее 250 символов")
  @Link("https://jira.project.ru/browse/TESTCASEID-100018")
  @Severity(SeverityLevel.MINOR)
  void testCreateUserWithLongFirstName() {
    String firstName = RandomStringUtils.randomAlphabetic(251).toLowerCase();
    Map<String, String> userData = new HashMap<>();
    userData.put("firstName", firstName);
    userData = DataGenerator.getRegistrationData(userData);

    Response response = apiCoreRequests.makePostRequest(USER_URL, userData);

    Assertions.assertResponseCodeEquals(response, 400);
    Assertions.assertResponseTextEquals(response, "The value of 'firstName' field is too long");
  }
}
