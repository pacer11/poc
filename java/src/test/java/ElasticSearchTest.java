
import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequestBuilder;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by jfaniyi on 2/21/17.
 */
public class ElasticSearchTest {

    private static ElasticSearchBaseTest testBase;
    private static JestClient client;

    private static final String INDEX_NAME = "people";
    private static final String DOC_TYPE = "docs";

    @BeforeClass
    public static void startElasticClient() {
        testBase = new ElasticSearchBaseTest();
        try {
            testBase.embeddedElastic.start();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Settings settings = Settings.builder().put("cluster.name", testBase.CLUSTER_NAME).build();
        testBase.transportClient = new PreBuiltTransportClient(settings);
        try {
            testBase.transportClient.addTransportAddress(
                    new InetSocketTransportAddress(InetAddress.getByName(testBase.ADDRESS), testBase.PORT)
            );
        } catch (UnknownHostException ex) {
            //LOGGER.error("Couldn't add transport address" + ex);
        }

        JestClientFactory factory = new JestClientFactory();
        factory.setHttpClientConfig(new HttpClientConfig
                .Builder("http://localhost:9400")
                .multiThreaded(true)
                .build());
         client = factory.getObject();
    }

    @AfterClass
    public static void stopElasticClient() {
        if (testBase.transportClient  != null) {
            testBase.transportClient .close();
            testBase.transportClient  = null;
        }
        testBase.embeddedElastic.stop();
    }

    @Before
    public void refresh() {
        testBase.embeddedElastic.refreshIndices();
        populateMapping();
    }

    /**@Test
    public void testInsert() throws Exception {
        ElasticCRUD elastic = new ElasticCRUD(testBase.transportClient );
        elastic.insertDocument();
    }**/

    @Test
    public void testSimpleInsert() {
        ElasticCRUD elastic = new ElasticCRUD(testBase.transportClient);
        //DateObject insertedItem = elastic.insertBasicDocument();
        BasicObject insertedItem = elastic.insertBasicDocument();
       // System.out.println("insertedItem: " + insertedItem);
        System.out.println("insertedItem json: " + insertedItem.toJSON());

       Iterator<BasicObject> results = elastic.getDocuemntsObject();
        //Iterator<DateObject> results = elastic.getDocuments();
       while (results.hasNext()) {
           BasicObject item = results.next();
          // System.out.println("retrieved item: " + item);
           System.out.println("retrieved item json: " + item.toJSON());
           //DateObject item = results.next();
           Assert.assertEquals(insertedItem.get("createDate"), item.get("createDate"));
           Assert.assertEquals(insertedItem, item);
        }

    }

    @Test
    @Ignore
    public void testInsertQuery() throws Exception {
        MongoToESJSON mongoToESJSON = new MongoToESJSON();
        ElasticCRUD elastic = new ElasticCRUD(testBase.transportClient);
        elastic.insertDocument();
        //Simple query key:value
        BasicObject basicObject = new BasicObject();
        basicObject.put("gender", "female");
        String jsonString = mongoToESJSON.recursiveBuilder(basicObject).toString();
        Iterator<BasicObject> results = elastic.queryDocument(jsonString);
        int genderCounter = 0;
        while (results.hasNext()) {
            System.out.println(results.next());
            genderCounter++;
        }
        Assert.assertEquals("Gender counter should be 4", 4, genderCounter);
       // And Query with Exists
        basicObject = new BasicObject();
        List<BasicObject> andList = new ArrayList<BasicObject>();
        BasicObject basicObjectAnd1 = new BasicObject();
        basicObjectAnd1.put("age", "30");
        BasicObject basicObjectAnd2 = new BasicObject();
        basicObjectAnd2.put("gender", "female");
        BasicObject notExistsObject = new BasicObject();
        notExistsObject.put("$exists", "true");
        BasicObject basicObjectAnd3 = new BasicObject();
        basicObjectAnd3.put("eyeColor", notExistsObject);
        andList.add(basicObjectAnd2);
        andList.add(basicObjectAnd1);
        andList.add(basicObjectAnd3);
        basicObject.put("$and", andList);
        jsonString = mongoToESJSON.recursiveBuilder(basicObject).toString();
        System.out.println("This is jsonString: " + jsonString);
        results = elastic.queryDocument(jsonString);
        int andCounter = 0;
        while (results.hasNext()) {
            System.out.println(results.next());
            andCounter++;
        }
        Assert.assertEquals("Gender counter with age of 30 should be 1", 1, andCounter);
        // Or Query
        basicObject = new BasicObject();
        List<BasicObject> orList = new ArrayList<BasicObject>();
        BasicObject basicObjectOr1 = new BasicObject();
        basicObjectOr1.put("age", "30");
        BasicObject basicObjectOr2 = new BasicObject();
        basicObjectOr2.put("gender", "female");
        orList.add(basicObjectOr1);
        orList.add(basicObjectOr2);
        basicObject.put("$or", orList);
        jsonString = mongoToESJSON.recursiveBuilder(basicObject).toString();
        System.out.println("This is jsonString: " + jsonString);
        results = elastic.queryDocument(jsonString);
        int orCounter = 0;
        while (results.hasNext()) {
            System.out.println(results.next());
            orCounter++;
        }
        Assert.assertEquals("Gender counter with age of 30 should be 5", 5, orCounter);
        // Range Query
        basicObject = new BasicObject();
        BasicObject rangeObject = new BasicObject();
        rangeObject.put("$lte", "22");
        basicObject.put("age", rangeObject);
        jsonString = mongoToESJSON.recursiveBuilder(basicObject).toString();
        System.out.println("This is jsonString: " + jsonString);
        results = elastic.queryDocument(jsonString);
        int rangeCounter = 0;
        while (results.hasNext()) {
            System.out.println(results.next());
            rangeCounter++;
        }
        Assert.assertEquals("Range Counter should be 2", 2, rangeCounter);
    }

    private void populateMapping() {
        PutMappingRequestBuilder builder =
                testBase.transportClient.admin().indices().preparePutMapping(INDEX_NAME).setType(DOC_TYPE);
        JSONObject metaDataValue = new JSONObject();
        JSONObject dateValue = new JSONObject();
        JSONArray mappingArray = new JSONArray();
        JSONObject dynamicTemplate = new JSONObject();
        JSONObject rulesValue = new JSONObject();
        JSONObject rulesMappingValue = new JSONObject();
        dateValue.put("createDate", "java.util.Date");
        dateValue.put("createZonedDateTime", "java.time.ZonedDateTime");
        dynamicTemplate.put("_default_", rulesValue);
        rulesValue.put("match", "_id");
        rulesValue.put("mapping", rulesMappingValue);
        rulesMappingValue.put("type", "keyword");
        rulesMappingValue.put("copy_to", "id");
        mappingArray.add(dynamicTemplate);
        metaDataValue.put("_meta", dateValue);
        metaDataValue.put("dynamic_templates", mappingArray);
        System.out.println(metaDataValue.toString());
        builder.setSource(metaDataValue.toJSONString()).get();
        /**
         * "mappings": {
         "my_type": {
         "dynamic_templates": [
         {
            "populate_id": {
                "match":   "_id",
                "mapping": {
                    "type": "keyword",
                    "copy_to": "id"
                }
            }
         }
         ]
         }
         }
         */
    }
}
