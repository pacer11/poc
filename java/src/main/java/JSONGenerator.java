import io.apptik.json.JsonElement;
import io.apptik.json.JsonObject;
import io.apptik.json.generator.JsonGenerator;
import io.apptik.json.generator.JsonGeneratorConfig;
import io.apptik.json.schema.SchemaV4;

import java.io.IOException;

/**
 *
 */
public class JSONGenerator {

    public static void main(String[] args) {
        int numOfObjects = 100;

        try {
            SchemaV4 schema = new SchemaV4().wrap(JsonElement.readFrom(getSchema()).asJsonObject());
            JsonGeneratorConfig gConf = new JsonGeneratorConfig();
            for (int i=0; i<numOfObjects; i++) {
                JsonObject job = new JsonGenerator(schema, null).generate().asJsonObject();
                System.out.println(job.toString());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getSchema() {
        String schemaString =
        "{\n" +
                "\"type\" : \"object\"," +
                "\"properties\" : {" +
                "\"id\" : {\"type\" : \"string\", \"minimum\": 15, \"maximum\":15  } ," +
                "\"Address\" : " + "{" +
                "\"type\" : \"object\"," +
                "\"properties\" : {" +
                "\"Line1\" : {\"type\" : \"string\", \"minimum\": 10, \"maximum\":25 }, " +
               "\"Line2\" : {\"type\" : \"string\", \"minimum\": 10, \"maximum\":50}, " +
                "\"City\" : {\"type\" : \"string\", \"minimum\": 10, \"maximum\":15}, " +
                "\"State\" : {\"type\" : \"string\", \"minimum\": 2, \"maximum\":2}, " +
                "\"ZipCode\" : {\"type\" : \"integer\", \"minimum\": 5, \"maximum\":6 } " +
                "}" +
                "}," +
                "\"Hobbies\" : {\"enum\" : [\"Fishing\", \"Cooking\", \"Tennis\", \"Singing\"] }, " +
                "\"Age\" : {\"type\" : \"integer\", \"minimum\": 1, \"maximum\":99 }," +
                "\"Homepage\" : {\"type\" : \"string\", \"format\": \"uri\" }," +
                "\"EmailAddress\" : {\"type\" : \"string\", \"format\": \"email\" }, " +
                "\"DateOfBirth\" : {\"type\" : \"string\", \"format\": \"date\" }, " +
                "\"PhoneNumber\" : {\"type\" : \"string\", \"format\": \"phone\" }, " +
                "\"ISPIp\" : {\"type\" : \"string\", \"format\": \"ipv4\" }, " +
                "\"Created\" : {\"type\" : \"string\", \"format\": \"date-time\" } " +
                "}" +
                "}" ;
        return schemaString;
    }

}
