import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by jfaniyi on 2/19/17.
 */
public class ElasticCRUD {

    private static final String INDEX_NAME = "test";
    private static final String DOC_TYPE = "doc";

    private Client transportClient;
    public ElasticCRUD(Client transportClient) {
        this.transportClient = transportClient;
    }

    public void insertDocument() throws Exception {
        List<Map<String, Object>> items = JsonHelper.loadJson();
        for (Map<String, Object> item : items) {
            IndexResponse response = transportClient.prepareIndex(INDEX_NAME, DOC_TYPE, item.get("_id").toString())
                    .setSource(JsonHelper.mapToJSON(item))
                    .get();
            System.out.println(response.getId());
           /** UpdateRequest updateRequest = new UpdateRequest();
            updateRequest.index(INDEX_NAME);
            updateRequest.type(DOC_TYPE);
            updateRequest.id(item.get("_id").toString());
            updateRequest.doc(XContentFactory.jsonBuilder().startObject()
                    .field("name","value").field("name1", "value1").endObject());
           UpdateResponse response = transportClient.(updateRequest).get();
           System.out.println(response.getId()); **/
           //for (String key : item.keySet()) {
            //    builder.field(key, item.get(key));
            //}
           // builder.endObject();
           // UpdateResponse response = transportClient
           //         .prepareUpdate(INDEX_NAME, DOC_TYPE, )
           //         .setUpsert(builder).get();
           // System.out.println(response.getId());
        }

    }
}
