package lib;

import static org.hamcrest.Matchers.hasKey;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.restassured.http.Headers;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;

public class BaseTestCase {

  private static final String HOST = "https://playground.learnqa.ru/";
  public static final String USER_URL = HOST + "api/user/";
  public static final String LOGIN_URL = HOST + "api/user/login";
  public static final String AUTH_URL = HOST + "api/user/auth";
  public ApiCoreRequests apiCoreRequests =  new ApiCoreRequests();

  protected String getHeader(Response response, String name) {
    Headers headers = response.getHeaders();

    assertTrue(headers.hasHeaderWithName(name), "Response doesn't have header with name " + name);
    return headers.getValue(name);
  }

  protected String getCookie(Response response, String name) {
    Map<String, String> cookies = response.getCookies();
    assertTrue(cookies.containsKey(name), "Response doesn't have cookie with name " + name);
    return cookies.get(name);
  }

  protected int getIntFromJson(Response response, String name) {
    response.then().assertThat().body("$", hasKey(name));
    return response.jsonPath().getInt(name);
  }

  protected Response login(String email, String password) {
    Map<String, String> authData = new HashMap<>();
    authData.put("email", email);
    authData.put("password", password);

    return apiCoreRequests.makePostRequest(LOGIN_URL, authData);
  }
}
