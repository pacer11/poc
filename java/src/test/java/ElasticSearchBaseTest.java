import org.elasticsearch.client.transport.TransportClient;

import pl.allegro.tech.embeddedelasticsearch.EmbeddedElastic;
import pl.allegro.tech.embeddedelasticsearch.PopularProperties;

import java.net.MalformedURLException;
import java.net.URL;


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
        try {
            embeddedElastic = EmbeddedElastic.builder()
                  //  .withElasticVersion(elasticsearchVersion)
                    .withSetting(PopularProperties.TRANSPORT_TCP_PORT, PORT)
                    .withSetting(PopularProperties.CLUSTER_NAME, CLUSTER_NAME)
                    .withSetting("path.conf", "/home/jfaniyi/IdeaProjects/elasticsearch/java/src/test/resources")
                    .withSetting("searchguard.ssl.transport.enabled", true)
                    .withSetting("searchguard.ssl.transport.keystore_filepath", "localhost.p12")
                    .withSetting("searchguard.ssl.transport.keystore_password", "changeit")
                    .withSetting("searchguard.ssl.transport.keystore_type", "PKCS12")
                    .withSetting("searchguard.ssl.transport.truststore_filepath", "localhost.p12")
                    .withSetting("searchguard.ssl.transport.truststore_password", "changeit")
                    .withSetting("searchguard.ssl.transport.truststore_type", "PKCS12")
                    .withSetting("searchguard.ssl.transport.enforce_hostname_verification", false)
                    .withDownloadUrl(new URL("file:///home/jfaniyi/Downloads/elasticsearch-5.0.0.zip"))
                    .withPlugin("file:///home/jfaniyi/Downloads/search-guard-ssl-5.0.0-21.zip")
                    .withIndex(INDEX_NAME).build();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }         
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
