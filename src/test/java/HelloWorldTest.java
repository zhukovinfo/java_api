import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
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
}