import org.apache.commons.lang3.builder.EqualsBuilder;

import java.io.Serializable;
import java.util.Map;
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

    public Object get(String key) {
        return documentMap.get(key);
    }

    public void put(String key, Object value) {
       documentMap.put(key, value);
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
}
