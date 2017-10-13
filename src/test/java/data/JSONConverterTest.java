package data;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class JSONConverterTest {

    @Test
    public void JSONtoString() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Field1", "Value1");
        jsonObject.put("Field2", 1);
        jsonObject.put("Field3", Arrays.asList(1, 2, 3));
        assertEquals("{\"Field2\":1,\"Field3\":[1,2,3],\"Field1\":\"Value1\"}",
                JSONConverter.JSONtoString(jsonObject));
    }

    @Test
    public void stringToJSON() throws Exception {
        String jsonString = "{\"Field2\":1,\"Field3\":[1,2,3],\"Field1\":\"Value1\"}";
        JSONObject jsonObject = JSONConverter.StringToJSON(jsonString);
        assertEquals("Value1", jsonObject.get("Field1"));
        assertEquals(1, jsonObject.get("Field2"));
        assertEquals(new JSONArray(Arrays.asList(1, 2, 3)).toString(),
                jsonObject.get("Field3").toString());
    }
}