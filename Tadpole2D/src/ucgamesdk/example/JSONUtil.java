package ucgamesdk.example;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * @author chenzh
 * 
 */
public class JSONUtil {
    public static JSONObject createJSON(String name, Object value) {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put(name, value);
        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return jsonObj;
    }
}
