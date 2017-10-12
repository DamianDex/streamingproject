package data;

import org.json.JSONObject;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class JSONDataGeneratorTest {

    JSONDataGenerator objUnderTest = new JSONDataGenerator();

    @Test
    public void generateEmptyJSON() {
        JSONObject jsonObject = objUnderTest.generateRandomKeyIntegerJSON();
        assertEquals(0, jsonObject.keySet().size());
    }

    @Test
    public void generateSingleKeyJSON() {
        JSONObject jsonObject = objUnderTest.generateRandomKeyIntegerJSON("Key1");
        assertEquals(1, jsonObject.keySet().size());
        assertTrue(jsonObject.get("Key1") instanceof Integer);
    }

    @Test
    public void generateDoubleKeyJSON() {
        JSONObject jsonObject = objUnderTest.generateRandomKeyIntegerJSON("Key1", "Key2");
        assertEquals(2, jsonObject.keySet().size());
    }

    @Test
    public void generateStringObjectJSON() throws Exception {
        Map<String, Object> fields= new HashMap<>();
        fields.put("Id", 1);
        fields.put("CustomerId", 1);
        fields.put("Duration", 10.5);
        fields.put("Dropped", false);
        fields.put("Place", "Warsaw");
        fields.put("Orders", Arrays.asList(1, 5, 12));

        JSONObject jsonObject = objUnderTest.generateStringObjectJSON(fields);
        System.out.println(jsonObject);
    }
}