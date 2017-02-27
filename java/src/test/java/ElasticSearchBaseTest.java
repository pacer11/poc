import org.elasticsearch.client.transport.TransportClient;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import pl.allegro.tech.embeddedelasticsearch.EmbeddedElastic;
import pl.allegro.tech.embeddedelasticsearch.IndexSettings;
import pl.allegro.tech.embeddedelasticsearch.PopularProperties;

import java.io.IOException;


/**
 * Created by jfaniyi on 2/22/17.
 */
public class ElasticSearchBaseTest {

    public  final static String ADDRESS = "127.0.0.1";
    public  final static int PORT = 9300;
    public  final static String CLUSTER_NAME = "test";
    public  final static String INDEX_NAME = "people";
    public  final static String DOC_TYPE = "docs";

    public static TransportClient transportClient;
    public static final String elasticsearchVersion = "5.0.0";
    public static EmbeddedElastic embeddedElastic;

   // protected static final Logger LOGGER = LoggerFactory.getLogger(InitializeESTesting.class);

    public ElasticSearchBaseTest() {
        embeddedElastic = EmbeddedElastic.builder()
                .withElasticVersion(elasticsearchVersion)
                .withSetting(PopularProperties.TRANSPORT_TCP_PORT, PORT)
                .withSetting(PopularProperties.CLUSTER_NAME, CLUSTER_NAME)
                .withIndex(INDEX_NAME).build();
                //.withIndex(INDEX_NAME, IndexSettings.builder().withType(DOC_TYPE, "{}").build()).build();
    }


   /** @BeforeClass
    public static  void startElastic() {
        try {
            embeddedElastic.start();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } **/
        /** Settings settings = Settings.builder().put("cluster.name", CLUSTER_NAME).build();
        transportClient = new PreBuiltTransportClient(settings);
        try {
            transportClient.addTransportAddress(
                    new InetSocketTransportAddress(InetAddress.getByName(ADDRESS), PORT)
            );
        } catch (UnknownHostException ex) {
            //LOGGER.error("Couldn't add transport address" + ex);
        }
    } **/

   // @AfterClass
   // public static void stopElastic() {
        /**if (transportClient != null) {
            transportClient.close();
            transportClient = null;
        }**/
    //    embeddedElastic.stop();
  //  }
}
