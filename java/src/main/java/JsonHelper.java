import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jfaniyi on 2/19/17.
 */
public class JsonHelper {

    private static Gson gson = new GsonBuilder().create();


    public static String mapToJSON(Map<String, Object> jsonMap) {
        jsonMap.remove("_id");
        return gson.toJson(jsonMap);
    }

    public static Map<String, Object> jsonToMap(String json) {
        Type typeOfHashMap = new TypeToken<Map<String, Object>>() { }.getType();
        return gson.fromJson(json, typeOfHashMap);
    }

    public static List<String> convertMapListToJsonList(List<Map<String, Object>> items) {
        List<String> jsonList = new ArrayList<String>();
        for (Map<String, Object> item : items) {
            jsonList.add(mapToJSON(item));
        }
        return jsonList;
    }

    public static List<Map<String, Object>> convertJsonListToMapList(List<String> items) {
        List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
        for (String item : items) {
            mapList.add(jsonToMap(item));
        }
        return mapList;
    }

    public static List<Map<String, Object>> loadJson() throws IOException {
        List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();

        InputStream jsStream = JsonHelper.class.getResourceAsStream("datasetfull.json");
        JsonReader reader = new JsonReader(new InputStreamReader(jsStream, "UTF-8"));
        reader.beginArray();
        while (reader.hasNext()) {
            // Read data into object model
            Map<String, Object> item = gson.fromJson(reader, HashMap.class);
            if (item.get("_id") != null) {
                System.out.println("Stream mode: " + item);
                mapList.add(item);
            }
        }
        reader.close();
        return mapList;
    }
}
