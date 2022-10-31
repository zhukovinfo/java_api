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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@Epic("User tests")
@Feature("Authorization")
class UserAuthTest extends BaseTestCase {

  String cookie;
  String header;
  int userIdOnAuth;

  @BeforeEach
  public void loginUser() {
    Map<String, String> authData = new HashMap<>();
    authData.put("email", "vinkotov@example.com");
    authData.put("password", "1234");

    Response responseGetAuth = apiCoreRequests
        .makePostRequest(LOGIN_URL, authData);

    this.cookie = this.getCookie(responseGetAuth, "auth_sid");
    this.header = this.getHeader(responseGetAuth, "x-csrf-token");
    this.userIdOnAuth = this.getIntFromJson(responseGetAuth, "user_id");
  }

  @Test
  @Description("This test successfully authorize user by email and password")
  @DisplayName("Test positive auth user")
  @Link("https://jira.project.ru/browse/TESTCASEID-100004")
  @Severity(SeverityLevel.CRITICAL)
  void testAuthUser() {
    Response responseCheckAUth = apiCoreRequests
        .makeGetRequest(
            AUTH_URL,
            this.header,
            this.cookie
        );
    Assertions.assertJsonByName(responseCheckAUth, "user_id", this.userIdOnAuth);
  }

  @ParameterizedTest
  @ValueSource(strings = {"cookie", "headers"})
  @Description("This test checks authorization status w/o sending auth cookie or token")
  @DisplayName("Test negative auth user")
  @Severity(SeverityLevel.NORMAL)
  @Link("https://jira.project.ru/browse/TESTCASEID-100005")
  void testNegativeAuthTest(String condition) {
    if (condition.equals("cookie")) {
      Response responseForCheck = apiCoreRequests.makeGetRequestWithCookie(
          AUTH_URL,
          this.cookie
      );
      Assertions.assertJsonByName(responseForCheck, "user_id", 0);
    } else if (condition.equals("headers")) {
      Response responseForCheck = apiCoreRequests.makeGetRequestWithToken(
          AUTH_URL,
          this.header
      );
      Assertions.assertJsonByName(responseForCheck, "user_id", 0);
    } else {
      throw new IllegalArgumentException("Condition value is known: " + condition);
    }
  }
}
