
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by jfaniyi on 2/21/17.
 */
public class ElasticSearchTest extends ElasticSearchBaseTest{

    @Test
    public void testInsert() throws Exception {
        ElasticCRUD elastic = new ElasticCRUD(transportClient);
        elastic.insertDocument();
    }
}
