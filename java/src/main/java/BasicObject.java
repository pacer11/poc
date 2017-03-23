import com.google.gson.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.json.simple.JSONObject;

import java.lang.reflect.Type;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by jfaniyi on 2/25/17.
 */
public class BasicObject {

    private String id;
    private Map<String, Object> documentMap = new TreeMap<String, Object>();


    public Gson BuilderSerializer() {
        //return new GsonBuilder().create();
       return new GsonBuilder().registerTypeAdapter(ZonedDateTime.class, new JsonSerializer<ZonedDateTime>() {
            @Override
            public JsonElement serialize(ZonedDateTime zonedDateTime,
                                         Type typeOfT,
                                         JsonSerializationContext context) {
                DateTimeFormatter fmt = DateTimeFormatter.ISO_INSTANT.ofPattern("yyyy-MM-dd'T'HH:mm:ss.n'Z'").withZone(ZoneId.of("UTC"));
                return new JsonPrimitive(fmt.format(zonedDateTime));
            }
        }).create();
    }
    public Gson BuilderDeserializer() {
        //return new GsonBuilder().create();
        return new GsonBuilder().registerTypeAdapter(ZonedDateTime.class, new JsonDeserializer<ZonedDateTime>() {
            @Override
            public ZonedDateTime deserialize(JsonElement jsonElement,
                                         Type typeOfT,
                                         JsonDeserializationContext context) {
                DateTimeFormatter fmt = DateTimeFormatter.ISO_INSTANT.ofPattern("yyyy-MM-dd'T'HH:mm:ss.n'Z'").withZone(ZoneId.of("UTC"));
                return ZonedDateTime.parse(jsonElement.getAsString(), fmt);
            }
        }).create();
    }
    //Gson gson = getBuilder().create();

    public BasicObject() {
    }

    public BasicObject(String id) {
        this.id = id;
    }

    public BasicObject(Map<String, Object> map) {
        documentMap.putAll(map);
    }

    public Set<Map.Entry<String, Object>> entrySet() {return documentMap.entrySet();}


    public Object get(String key) {
        return documentMap.get(key);
    }

    public Boolean getBoolean(String key) {return Boolean.valueOf((String)documentMap.get(key));}

    public String getString(String key) { return (String)documentMap.get(key);}

    public void put(String key, Object value) {
       documentMap.put(key, value);
    }

    public void putAll(Map<String, Object> input) {
        documentMap.putAll(input);
    }

    public String toJSON() { return BuilderSerializer().toJson(this, BasicObject.class);}

    //public String toJSON() {return buildJSON(this).toString();}

    public void remove(String key) { documentMap.remove(key);}

    public int size() { return documentMap.size();}

    public String getId() { return id;}

    public boolean containsKey(String key) { return documentMap.containsKey(key);}

    private JSONObject buildJSON(BasicObject basicObject) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", basicObject.getId());
        for (Map.Entry<String, Object> item : basicObject.entrySet()) {
            if (BasicObject.class.isInstance(item.getValue())) {
                jsonObject.put(item.getKey(), buildJSON((BasicObject)item.getValue()));
            } else {
                jsonObject.put(item.getKey(), item.getValue().toString());
            }
        }
        return jsonObject;
    }

    @Override
    public String toString() {
        return "BasicObject{" +
                "id='" + id + '\'' +
                ", documentMap=" + documentMap +
                '}';
    }

    @Override
    public boolean equals( Object compare ) {
        return new EqualsBuilder().append(documentMap,
                ((BasicObject)compare).documentMap).isEquals();
    }

   // static class BasicObjectSerializer implements JsonSerializer<BasicObject> {
   //     @Override
   //     public JsonElement serialize(BasicObject src, Type typeOfSrc, JsonSerializationContext context) {
   //         return new JsonPrimitive(src.toJSON());
   //     }
   // }
}
