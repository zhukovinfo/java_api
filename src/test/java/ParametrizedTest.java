import static org.junit.jupiter.api.Assertions.assertEquals;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ParametrizedTest {

  @ParameterizedTest
  @ValueSource(strings = {"", "Jonh", "Pete"})
  void testHelloMethodWithoutName(String name) {
    Map<String, String> queryParams = new HashMap<>();

    if (name.length() > 0) {
      queryParams.put("name", name);
    }

    JsonPath response = RestAssured
        .given()
        .queryParams(queryParams)
        .get("https://playground.learnqa.ru/api/hello")
        .jsonPath();
    String answer = response.getString("answer");
    String expectedName = (name.length() > 0) ? name : "someone";
    assertEquals("Hello, " + expectedName, answer, "The answer is not expected");
  }
}
