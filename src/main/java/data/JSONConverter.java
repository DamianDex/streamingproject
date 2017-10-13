package data;

import org.json.JSONObject;

import java.io.StringWriter;

public class JSONConverter {
    public static String JSONtoString(JSONObject jsonObject) {
        StringWriter out = new StringWriter();
        jsonObject.write(out);
        return out.toString();
    }

    public static JSONObject StringToJSON(String jsonString) {
        return new JSONObject(jsonString);
    }
}
