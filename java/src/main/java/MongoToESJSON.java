import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by jfaniyi on 3/2/17.
 */
public class MongoToESJSON {

    public JSONObject recursiveBuilder(BasicObject basicObject) {
        JSONObject jsonObject = new JSONObject();
        for (Map.Entry<String, Object> objectEntry : basicObject.entrySet()) {
            String key = objectEntry.getKey();
            Object value = objectEntry.getValue();
            if ("$and".equals(key) && List.class.isInstance(value)) {
                List<JSONObject> mustValue = new ArrayList<JSONObject>();
                JSONObject boolObject = new JSONObject();
                for (Object item : (List) value) {
                    BasicObject element = (BasicObject)item;
                    mustValue.add(recursiveBuilder(element));
                }
                boolObject.put("must", mustValue);
                jsonObject.put("bool", boolObject);
            }
            else if ("$or".equals(key) && List.class.isInstance(value)) {
                List<JSONObject> shouldValue = new ArrayList<JSONObject>();
                JSONObject boolObject = new JSONObject();
                for (Object item : (List)value ) {
                    BasicObject element = (BasicObject)item;
                    shouldValue.add(recursiveBuilder(element));
                }
                boolObject.put("should", shouldValue);
                jsonObject.put("bool", boolObject);
            }
            else if (BasicObject.class.isInstance(value) && ((BasicObject)value).size() ==1 && ((BasicObject)value).containsKey("$exists")) {
                boolean doesExist = ((BasicObject)value).getBoolean("$exists");
                if (doesExist) {
                    JSONObject existsJson = new JSONObject();
                    existsJson.put("field", key);
                    jsonObject.put("exists", existsJson);
                }  else {
                    JSONObject valueJson = new JSONObject();
                    valueJson.put("field", key);
                    JSONObject existsJson = new JSONObject();
                    existsJson.put("exists", valueJson);
                    JSONObject mustNotJson = new JSONObject();
                    mustNotJson.put("must_not",existsJson);
                    jsonObject.put("bool", mustNotJson);
                }
            } else if (BasicObject.class.isInstance(value) && ((BasicObject) value).size() ==1 && ((BasicObject)value).containsKey("$regex")) {
                String regexValue = ((BasicObject) value).getString("$regex");
                JSONObject regexJson = new JSONObject();
                regexJson.put(key, regexValue);
                jsonObject.put("regexp", regexJson);
            } else if (BasicObject.class.isInstance(value) && ((BasicObject) value).size() ==1 && ((BasicObject)value).containsKey("$not")) {
                    throw new UnsupportedOperationException();
            } else if (BasicObject.class.isInstance(value) && ((BasicObject) value).size() ==1 && ((BasicObject)value).containsKey("$in")) {
                    List list = (List) ((BasicObject)value).get("$in");
                    JSONObject shouldValue = new JSONObject();
                    List<JSONObject> matchValueList = new ArrayList<JSONObject>();
                    JSONObject keyValue = null;
                    for (Object listItem : list) {
                        keyValue = new JSONObject();
                        if (Integer.class.isInstance(listItem)) {
                            listItem = String.valueOf(listItem);
                        } else {
                            listItem = (String)listItem;
                        }
                        keyValue.put(key, listItem);
                        matchValueList.add(keyValue);
                    }
                    shouldValue.put("should", matchValueList);
                    jsonObject.put("bool", shouldValue);
            } else if (BasicObject.class.isInstance(value) && ((BasicObject) value).size() ==1 && ((BasicObject)value).containsKey("$gt")) {
                    JSONObject rangeValue = new JSONObject();
                    JSONObject logicalValue = new JSONObject();
                    logicalValue.put("gt", ((BasicObject)value).get("$gt"));
                    rangeValue.put(key, logicalValue);
                    jsonObject.put("range", rangeValue);
            } else if (BasicObject.class.isInstance(value) && ((BasicObject) value).size() ==1 && ((BasicObject)value).containsKey("$lt")) {
                JSONObject rangeValue = new JSONObject();
                JSONObject logicalValue = new JSONObject();
                logicalValue.put("lt", ((BasicObject)value).get("$lt"));
                rangeValue.put(key, logicalValue);
                jsonObject.put("range", rangeValue);
            } else if (BasicObject.class.isInstance(value) && ((BasicObject) value).size() ==1 && ((BasicObject)value).containsKey("$gte")) {
                JSONObject rangeValue = new JSONObject();
                JSONObject logicalValue = new JSONObject();
                logicalValue.put("gte", ((BasicObject)value).get("$gte"));
                rangeValue.put(key, logicalValue);
                jsonObject.put("range", rangeValue);
            } else if (BasicObject.class.isInstance(value) && ((BasicObject) value).size() ==1 && ((BasicObject)value).containsKey("$lte")) {
                JSONObject rangeValue = new JSONObject();
                JSONObject logicalValue = new JSONObject();
                logicalValue.put("lte", ((BasicObject)value).get("$lte"));
                rangeValue.put(key, logicalValue);
                jsonObject.put("range", rangeValue);
            } else {
                JSONObject matchValue = new JSONObject();
                matchValue.put(key, value);
                jsonObject.put("match", matchValue);
            }
        }
        return jsonObject;
    }
}
