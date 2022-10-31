package lib;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringEscapeUtils;

public class DataGenerator {
  public static String getRandomEmail() {
    String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());
    return "learnqa" + timestamp + "@example.com";
  }

  public static Map<String, String> getRegistrationData() {
    Map<String, String> data = new HashMap<>();
    data.put("email", DataGenerator.getRandomEmail());
    data.put("password", "123");
    data.put("username", "learnqa");
    data.put("firstName", "learnqa");
    data.put("lastName", "learnqa");

    return data;
  }

  public static Map<String, String> getRegistrationData(Map<String, String> nonDefaultValues) {
    Map<String, String> defaultValues = DataGenerator.getRegistrationData();

    Map<String, String> userData = new HashMap<>();
    String[] keys = {"email", "password", "username", "firstName", "lastName"};
    for (String key: keys) {
      if (nonDefaultValues.containsKey(key)) {
        userData.put(key, nonDefaultValues.get(key));
      } else {
        userData.put(key, defaultValues.get(key));
      }
    }

    return userData;
  }
}
