import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import org.apache.commons.io.IOUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Created by jfaniyi on 2/19/17.
 */
public class JsonHelperTest {

    private static List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
    private static Gson gson = new GsonBuilder().create();

    @BeforeClass
    public static void loadJson() throws IOException {

        InputStream jsStream = JsonHelperTest.class.getResourceAsStream("datasetfull.json");
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
    }

    @Test
    public void testMapToJson() {
        List<String> jsonList = JsonHelper.convertMapListToJsonList(mapList);
        assertEquals("Size should be 7", jsonList.size(), mapList.size());
    }

    @Test
    public void testJsonToMap() {
        List<String> jsonList = JsonHelper.convertMapListToJsonList(mapList);
        assertEquals("Size should be 7", jsonList.size(), mapList.size());
        List<Map<String, Object>> newMapList = JsonHelper.convertJsonListToMapList(jsonList);
        assertEquals("Size should be 7", jsonList.size(), newMapList.size());

    }
}
