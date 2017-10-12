package data;

import org.json.JSONObject;

import java.util.Map;
import java.util.Random;

public class JSONDataGenerator {
    public JSONObject generateRandomKeyIntegerJSON(String... fields) {
        Random random = new Random();
        JSONObject singleJSON = new JSONObject();
        for(String field : fields) {
            singleJSON.put(field, random.nextInt());
        }
        return singleJSON;
    }

    public JSONObject generateStringObjectJSON(Map<String, Object> fields) {
        JSONObject singleJSON = new JSONObject();
        for (String field : fields.keySet()) {
            singleJSON.put(field, fields.get(field));
        }
        return singleJSON;
    }
}
