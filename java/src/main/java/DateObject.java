import org.apache.commons.lang3.builder.EqualsBuilder;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by jfaniyi on 3/6/17.
 */
public class DateObject {

        private String id;
        private Map<String, Date> documentMap;

        public DateObject() {
            documentMap = new TreeMap<String, Date>();
        }

        public DateObject(String id) {
            this.id = id;
            documentMap = new TreeMap<String, Date>();
        }

        public DateObject(Map<String, Date> map) {
            documentMap.putAll(map);
        }

        public Date get(String key) {
            return documentMap.get(key);
        }

        public void put(String key, Date value) {
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
                    ((DateObject)compare).documentMap).isEquals();
        }


}
