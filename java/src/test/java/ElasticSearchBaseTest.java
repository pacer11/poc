import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by jfaniyi on 2/22/17.
 */
public class ElasticSearchBaseTest {

    private final static String ADDRESS = "127.0.0.1";
    private final static int PORT = 9300;
    private final static String CLUSTER_NAME = "test";
    protected final static String INDEX_NAME = "test";

    protected static TransportClient transportClient;

   // protected static final Logger LOGGER = LoggerFactory.getLogger(InitializeESTesting.class);

    @BeforeClass
    public static void setupTransportClient() {
        Settings settings = Settings.builder().put("cluster.name", CLUSTER_NAME).build();
        transportClient = new PreBuiltTransportClient(settings);
        try {
            transportClient.addTransportAddress(
                    new InetSocketTransportAddress(InetAddress.getByName(ADDRESS), PORT)
            );
        } catch (UnknownHostException ex) {
            //LOGGER.error("Couldn't add transport address" + ex);
        }
    }

    @AfterClass
    public static void tearDownTransportClient() {
        if (transportClient != null) {
            transportClient.close();
            transportClient = null;
        }
    }
}
