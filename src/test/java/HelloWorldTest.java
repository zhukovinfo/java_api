import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

public class HelloWorldTest {

  @Test
  public void testHello() {
    System.out.println("Hello from Andrey Zhukov");
  }

  @Test
  public void testRestAssured() {
    Map<String, String> data = new HashMap<>();
    data.put("login", "secret_login2");
    data.put("password", "secret_pass2");
    Response responseForGet = RestAssured
        .given()
        .body(data)
        .when()
        .post("https://playground.learnqa.ru/api/get_auth_cookie")
        .andReturn();
    String responseCookie = responseForGet.getCookie("auth_cookie");

    Map<String, String> cookies = new HashMap<>();
    if (responseCookie != null) {
      cookies.put("auth_cookie", responseCookie);
    }

    Response responseForCheck = RestAssured
        .given()
        .body(data)
        .cookies(cookies)
        .when()
        .post("https://playground.learnqa.ru/api/check_auth_cookie");
    responseForCheck.print();
  }

  @Test
  public void Ex5Test() {
    JsonPath responseJsonPath = RestAssured
        .get("https://playground.learnqa.ru/api/get_json_homework")
        .jsonPath();

    System.out.println(responseJsonPath.get("messages[1].message").toString());
  }

  @Test
  public void Ex6RedirectTest() {
    Response response = RestAssured
        .given()
        .redirects()
        .follow(false)
        .when()
        .get("https://playground.learnqa.ru/api/long_redirect")
        .andReturn();
    String headerLocation = response.getHeader("location");
    System.out.println(headerLocation);
  }

  @Test
  public void Ex7LongRedirectTest() {
    int count = 0;
    String location = "https://playground.learnqa.ru/api/long_redirect";
    int statusCode;
    do {
      Response response = RestAssured
          .given()
          .redirects()
          .follow(false)
          .get(location)
          .andReturn();
      location = response.getHeader("location");
      statusCode = response.statusCode();
      count++;
      if (location != null) {
        System.out.println(count + " " + location);
      }
    }
    while (statusCode != 200);
  }

  @Test
  public void Ex8TokensTest() throws InterruptedException {
    String location = "https://playground.learnqa.ru/ajax/api/longtime_job";

    JsonPath responseWithToken = RestAssured
        .get(location)
        .jsonPath();
    String token = responseWithToken.get("token");
    int seconds =responseWithToken.get("seconds");

    JsonPath responseBeforeTaskCompleted = RestAssured
        .given()
        .params("token", token)
        .get(location)
        .jsonPath();
    String status = responseBeforeTaskCompleted.get("status");
    assertThat(status, equalTo("Job is NOT ready"));

    TimeUnit.SECONDS.sleep(seconds);

    JsonPath responseAfterTaskCompleted = RestAssured
        .given()
        .params("token", token)
        .get(location)
        .jsonPath();

    status = responseAfterTaskCompleted.get("status");
    assertThat(status, equalTo("Job is ready"));
    String result = responseAfterTaskCompleted.get("result");
    assertThat(result, CoreMatchers.notNullValue());
  }

  @Test
  public void Ex9MatchPasswordTest() {
    List<String> passwords = Stream.of("password", "password", "123456", "123456", "123456", "123456", "123456",
        "123456", "123456", "123456", "123456", "password", "password", "password", "password", "password", "password",
        "123456789", "12345678", "12345678", "12345678", "12345", "12345678", "12345", "12345678", "123456789",
        "qwerty", "qwerty", "abc123", "qwerty", "12345678", "qwerty", "12345678", "qwerty", "12345678", "password",
        "abc123", "qwerty", "abc123", "qwerty", "12345", "football", "12345", "12345", "1234567", "monkey", "monkey",
        "123456789", "123456789", "123456789", "qwerty", "123456789", "111111", "12345678", "1234567", "letmein",
        "111111", "1234", "football", "1234567890", "letmein", "1234567", "12345", "letmein", "dragon", "1234567",
        "baseball", "1234", "1234567", "1234567", "sunshine", "iloveyou", "trustno1", "111111", "iloveyou", "dragon",
        "1234567", "princess", "football", "qwerty", "111111", "dragon", "baseball", "adobe123[a]", "football",
        "baseball", "1234", "iloveyou", "iloveyou", "123123", "baseball", "iloveyou", "123123", "1234567", "welcome",
        "login", "admin", "princess", "abc123", "111111", "trustno1", "admin", "monkey", "1234567890", "welcome",
        "welcome", "admin", "qwerty123", "iloveyou", "1234567", "1234567890", "letmein", "abc123", "solo", "monkey",
        "welcome", "1q2w3e4r", "master", "sunshine", "letmein", "abc123", "111111", "abc123", "login", "666666",
        "admin", "sunshine", "master", "photoshop", "111111", "1qaz2wsx", "admin", "abc123", "abc123", "qwertyuiop",
        "ashley", "123123", "1234", "mustang", "dragon", "121212", "starwars", "football", "654321", "bailey",
        "welcome", "monkey", "access", "master", "flower", "123123", "123123", "555555", "passw0rd", "shadow",
        "shadow", "shadow", "monkey", "passw0rd", "dragon", "monkey", "lovely", "shadow", "ashley", "sunshine",
        "master", "letmein", "dragon", "passw0rd", "654321", "7777777", "123123", "football", "12345", "michael",
        "login", "sunshine", "master", "!@#$%^&*", "welcome", "654321", "jesus", "password1", "superman",
        "princess", "master", "hello", "charlie", "888888", "superman", "michael", "princess", "696969", "qwertyuiop",
        "hottie", "freedom", "aa123456", "princess", "qazwsx", "ninja", "azerty", "123123", "solo", "loveme",
        "whatever", "donald", "dragon", "michael", "mustang", "trustno1", "batman", "passw0rd", "zaq1zaq1", "qazwsx",
        "password1", "password1", "Football", "password1", "000000", "trustno1", "starwars", "password1", "trustno1",
        "qwerty123", "123qwe")
        .distinct().collect(Collectors.toList());

    for (String password: passwords) {
      Map<String, String> params = new HashMap<>();
      params.put("login", "super_admin");
      params.put("password", password);
      Response getPasswordResponse = RestAssured
          .given()
          .params(params)
          .get("https://playground.learnqa.ru/ajax/api/get_secret_password_homework")
          .andReturn();
      String authCookie = getPasswordResponse.cookie("auth_cookie");

      Response checkAUthCookieResponse = RestAssured
          .given()
          .cookies("auth_cookie", authCookie)
          .get("https://playground.learnqa.ru/ajax/api/check_auth_cookie")
          .andReturn();

      boolean isPasswordValid = !Objects.equals(checkAUthCookieResponse.asString(), "You are NOT authorized");
      if (isPasswordValid) {
        System.out.println("You are authorized " + password);
        return;
      }
    }
  }
}