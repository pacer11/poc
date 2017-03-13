import org.apache.commons.lang3.builder.EqualsBuilder;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by jfaniyi on 2/25/17.
 */
public class BasicObject {

    private String id;
    private Map<String, Object> documentMap;

    public BasicObject() {
        documentMap = new TreeMap<String, Object>();
    }

    public BasicObject(String id) {
        this.id = id;
        documentMap = new TreeMap<String, Object>();
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

    public void putAll(Map<String, Object> input) { documentMap.putAll(input);}

    public int size() { return documentMap.size();}

    public boolean containsKey(String key) { return documentMap.containsKey(key);}

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
}
