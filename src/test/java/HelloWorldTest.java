import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Test;

public class HelloWorldTest {

  @Test
  public void testHello() {
    System.out.println("Hello from Andrey Zhukov");
  }

  @Test
  public void testHelloWorld() {
    Response response = RestAssured
        .get("https://playground.learnqa.ru/api/hello")
        .andReturn();
    response.prettyPrint();
  }

}
