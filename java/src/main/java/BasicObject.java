import com.google.gson.*;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.LinkedTreeMap;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;


import java.io.Serializable;
import java.lang.reflect.Type;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Created by jfaniyi on 2/25/17.
 */
public class BasicObject extends HashMap<String, Object> implements Serializable {
    @SerializedName("_id")
    private String _id;
   private Map<String, Object> documentMap;
  // private Map<String, Object> documentMap;


    public Gson Builder() {
        //return new GsonBuilder().create();
       return new GsonBuilder()
               .registerTypeAdapter(ZonedDateTime.class, new ZoneDateTimeSerializer())
               .registerTypeAdapter(ZonedDateTime.class, new ZoneDateTimeeDeserializer())
               .registerTypeAdapter(Collection.class, new CollectiveSerializerAdapter())
               .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
               .create();
    }

    //Gson gson = getBuilder().create();

    public BasicObject() { documentMap = new TreeMap<String, Object>(); }

    public BasicObject(String id) {
        this();
        this._id = id;
        documentMap.put("_id", id);
    }

    public BasicObject(Map<String, Object> map) {

        documentMap = new LinkedHashMap<String, Object>(recursiveMap(map));
    }

    public Set<Map.Entry<String, Object>> entrySet() {return documentMap.entrySet();}


    public Object get(String key) {
        return documentMap.get(key);
    }

    public Boolean getBoolean(String key) {return Boolean.valueOf((String)documentMap.get(key));}

    public String getString(String key) { return (String)documentMap.get(key);}

    public BasicObject put(String key, Object value) {
       documentMap.put(key, value);
       return this;
    }

    /**public void putAll(Map<String, Object> input) {
       return recursiveMap(input);
    }**/

    public String toJSON() { return Builder().toJson(this);}

    public BasicObject fromJSON(String json) {return Builder().fromJson(json, BasicObject.class);}

    //public String toJSON() {return buildJSON(this).toString();}

    public Object remove(String key) { return documentMap.remove(key);}

    public int size() { return documentMap.size();}

    public String getId() { return _id;}

    public void setId(String id) { this._id = id;}

    public boolean containsKey(String key) { return documentMap.containsKey(key);}

    private Map<String, Object> recursiveMap(Map<String, Object> map) {
        final BasicObject basicObject = new BasicObject();
        for (Map.Entry<String, Object> entryEntry : map.entrySet()) {
            if (Map.class.isInstance(entryEntry.getValue())) {
                basicObject.put(entryEntry.getKey(), recursiveMap((Map<String, Object>)entryEntry.getValue()));
            } else if (List.class.isInstance(entryEntry.getValue())) {
                List list = new ArrayList<> (((List) entryEntry.getValue()).size());
                for (Object object : ((List)entryEntry.getValue())) {
                    if (Map.class.isInstance(object)) {
                        list.add(recursiveMap((Map<String, Object>)object));
                    } else {
                        list.add(object);
                    }
                }
                basicObject.put(entryEntry.getKey(), list);
            } else {
                basicObject.put(entryEntry.getKey(), entryEntry.getValue());
            }
        }
        return basicObject;
    }

    private JsonObject buildJSON(BasicObject basicObject) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", basicObject.getId());
        for (Map.Entry<String, Object> item : basicObject.entrySet()) {
            if (BasicObject.class.isInstance(item.getValue())) {
                jsonObject.add(item.getKey(), buildJSON((BasicObject)item.getValue()));
            } else {
                jsonObject.addProperty(item.getKey(), item.getValue().toString());
            }
        }
        return jsonObject;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("documentMap", documentMap.toString())
                .append("_id", _id).build();
    }

    @Override
    public boolean equals( Object compare ) {
        if (this == compare) {
            return true;
        }
        if (compare == null || getClass() != compare.getClass()){
            return false;
        }
        BasicObject document = (BasicObject)compare;
        return new EqualsBuilder().append(documentMap,
                document.documentMap).isEquals();
    }

    private static class CollectiveSerializerAdapter implements JsonSerializer<Collection<?>> {
        @Override
        public JsonElement serialize(Collection<?> objects, Type type, JsonSerializationContext context) {
            if (objects == null || objects.isEmpty()) {
                return null;
            }
            JsonArray array = new JsonArray();
            for (Object child : objects) {
                JsonElement element = context.serialize(child);
                array.add(element);
            }
            return array;
        }
    }

    private static class ZoneDateTimeSerializer implements JsonSerializer<ZonedDateTime> {
        @Override
        public JsonElement serialize(ZonedDateTime zonedDateTime,
                                     Type typeOfT,
                                     JsonSerializationContext context) {
            DateTimeFormatter fmt = DateTimeFormatter.ISO_INSTANT.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneId.of("UTC"));
            return new JsonPrimitive(fmt.format(zonedDateTime));
        }
    }

    private static class ZoneDateTimeeDeserializer implements JsonDeserializer<ZonedDateTime> {
        @Override
        public ZonedDateTime deserialize(JsonElement jsonElement,
                                         Type typeOfT,
                                         JsonDeserializationContext context) {
            DateTimeFormatter fmt = DateTimeFormatter.ISO_INSTANT.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneId.of("UTC"));
            return ZonedDateTime.parse(jsonElement.getAsString(), fmt);
        }
    }


   // static class BasicObjectSerializer implements JsonSerializer<BasicObject> {
   //     @Override
   //     public JsonElement serialize(BasicObject src, Type typeOfSrc, JsonSerializationContext context) {
   //         return new JsonPrimitive(src.toJSON());
   //     }
   // }
}
