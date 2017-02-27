
import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.List;

/**
 * Created by jfaniyi on 2/21/17.
 */
public class ElasticSearchTest {

    private static ElasticSearchBaseTest testBase;
    private static JestClient client;

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
                .Builder("http://localhost:9200")
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
    }

    @Test
    public void testInsert() throws Exception {
        ElasticCRUD elastic = new ElasticCRUD(testBase.transportClient );
        elastic.insertDocument();
    }

    @Test
    public void testSimpleInsert() {
        ElasticCRUD elastic = new ElasticCRUD(testBase.transportClient);
        BasicObject insertedItem = elastic.insertBasicDocument();
        //testBase.embeddedElastic.
       /** try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } **/
        Iterator<BasicObject> results = elastic.getDocuemnts();
       while (results.hasNext()) {
           BasicObject item = results.next();
            System.out.println(item);
            System.out.println(insertedItem);
            Assert.assertEquals(insertedItem, item);
        }

    }
}
