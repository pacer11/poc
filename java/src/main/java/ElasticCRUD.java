import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;
import com.google.common.collect.Iterators;
import io.searchbox.client.JestClient;
import org.elasticsearch.action.index.IndexResponse;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

import java.io.IOException;
import java.util.*;

/**
 * Created by jfaniyi on 2/19/17.
 */
public class ElasticCRUD {

    private static final String INDEX_NAME = "people";
    private static final String DOC_TYPE = "docs";

    private Client transportClient;
    private JestClient jestClient;

    private ObjectMapper objectMapper = new ObjectMapper();


    public ElasticCRUD(Client transportClient) {
        this.transportClient = transportClient;
    }
    public ElasticCRUD(JestClient jestClient) { this.jestClient = jestClient; }

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

    public BasicObject insertBasicDocument() {
        BasicObject simpleObject = new BasicObject("idtesting");
        //simpleObject.put("docKey", new Date());
        simpleObject.put("docKey", "testing");
        BasicObject nestedObject = new BasicObject("innerIdtesting");
       // nestedObject.put("innerDocKey", "myStringValue");
        simpleObject.put("outerDocKey", nestedObject);
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        try {
            IndexResponse indexResponse = transportClient.prepareIndex(INDEX_NAME, DOC_TYPE)
                    .setSource(objectMapper.writeValueAsString(simpleObject)).get();
            System.out.println(indexResponse.getId());
            transportClient.admin().indices().prepareRefresh(INDEX_NAME).get();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return simpleObject;
    }

    public Iterator<BasicObject> getDocuemnts() {
        SearchResponse searchResponse =
                transportClient.prepareSearch(INDEX_NAME).setTypes(DOC_TYPE).setQuery(QueryBuilders.matchAllQuery()).get();
        Iterator<SearchHit> searchHits = searchResponse.getHits().iterator();
        return Iterators.transform(searchHits, new Function<SearchHit, BasicObject>() {
            public BasicObject apply(SearchHit input) {
                BasicObject output = null;
                try {
                    output = objectMapper.readValue(input.getSourceAsString(), BasicObject.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return output;
            }
            }
        );
    }
}
