import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;
import com.google.common.collect.Iterators;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.ClusterState;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Created by jfaniyi on 2/19/17.
 */
public class ElasticCRUD {

    private static final String INDEX_NAME = "people";
    private static final String DOC_TYPE = "docs";

    private Client transportClient;

    private ObjectMapper objectMapper = new ObjectMapper();
    DateTimeFormatter fmt = DateTimeFormatter.ISO_INSTANT.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneId.of("UTC"));
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);


    public ElasticCRUD(Client transportClient) {
        this.transportClient = transportClient;
    }

    public void insertDocument() throws Exception {
        List<Map<String, Object>> items = JsonHelper.loadJson();
        for (Map<String, Object> item : items) {
            IndexResponse response = transportClient.prepareIndex(INDEX_NAME, DOC_TYPE, item.get("_id").toString())
                    .setSource(JsonHelper.mapToJSON(item))
                    .get();
            transportClient.admin().indices().prepareRefresh(INDEX_NAME).get();
        }

    }

    public void insertDocumentBulk() throws Exception {
        List<Map<String, Object>> items = JsonHelper.loadJson();
        BulkRequestBuilder builder = transportClient.prepareBulk();
        IndexRequestBuilder indexBuilder = transportClient.prepareIndex(INDEX_NAME, DOC_TYPE);
        for (Map<String, Object> item : items) {
            indexBuilder.setId(item.get("_id").toString()).setSource(JsonHelper.mapToJSON(item));
            builder.add(indexBuilder);
        }
        BulkResponse response = builder.get();
        transportClient.admin().indices().prepareRefresh(INDEX_NAME).get();
    }

   // public DateObject insertBasicDocument() {
      public BasicObject insertBasicDocument() {
        //DateObject simpleObject = new DateObject("idtesting");
        //simpleObject.put("docKey", new Date());
        //simpleObject.put("docKey", "testing");
          BasicObject simpleObject = new BasicObject("idtesting");
          simpleObject.put("dockey", "docvalue");
          simpleObject.put("createDate", new Date());
          simpleObject.put("createZonedDateTime", ZonedDateTime.ofInstant(Instant.now(), ZoneId.of("UTC")));
          //simpleObject.put("createDate", new Date());
          //BasicObject nestedObject = new BasicObject("innerIdtesting");
          //nestedObject.put("innerDocKey", "myStringValue");
         //simpleObject.put("outerDocKey", nestedObject);
          System.out.println("Json before insert is: " + simpleObject.toJSON());
        IndexResponse indexResponse = transportClient.prepareIndex(INDEX_NAME, DOC_TYPE, simpleObject.getId())
                    .setSource(simpleObject.toJSON()).get();
            transportClient.admin().indices().prepareRefresh(INDEX_NAME).get();
            getIndexMappting();
        return simpleObject;
    }

    public void getIndexMappting() {
        List<String> fieldList = new ArrayList<String>();
        ClusterState cs = transportClient.admin().cluster().prepareState().setIndices(INDEX_NAME).execute().actionGet().getState();
        IndexMetaData imd = cs.getMetaData().index(INDEX_NAME);
        MappingMetaData mdd = imd.mapping(DOC_TYPE);
        Map<String, Object> map = null;

        try {
            map = mdd.getSourceAsMap();
            System.out.println(map);
        } catch (IOException e) {
            e.printStackTrace();
        }
        fieldList = getList("", map);

        System.out.println("Field List:");
        for (String field : fieldList) {
            System.out.println(field);
        }
    }

    //public Iterator<BasicObject> getDocuemntsDate() {
      public Iterator<DateObject> getDocuments() {
        SearchResponse searchResponse =
                transportClient.prepareSearch(INDEX_NAME).setTypes(DOC_TYPE).addSort("_uid", SortOrder.ASC).setQuery(QueryBuilders.matchAllQuery()).get();
        Iterator<SearchHit> searchHits = searchResponse.getHits().iterator();
        return Iterators.transform(searchHits, new Function<SearchHit, DateObject>() {
            public DateObject apply(SearchHit input) {
               //BasicObject output = null;
                 DateObject output = null;
                try {
                    output = objectMapper.readValue(input.getSourceAsString(), DateObject.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return output;
            }
            }
        );
    }

    public Iterator<BasicObject> getDocuemntsObject() {
        SearchResponse searchResponse =
                transportClient.prepareSearch(INDEX_NAME).setTypes(DOC_TYPE).addSort("_uid", SortOrder.ASC).setQuery(QueryBuilders.matchAllQuery()).get();
        Iterator<SearchHit> searchHits = searchResponse.getHits().iterator();
        return Iterators.transform(searchHits, new Function<SearchHit, BasicObject>() {
                    public BasicObject apply(SearchHit input) {
                        BasicObject output = new BasicObject();
                        output.setId(input.getId());
                        Map<String, Object> resultMap = input.sourceAsMap();
                        for (Map.Entry<String, Object> resultEntry : resultMap.entrySet()) {
                                Map<String, Object> metaDateMap = getMetaData();
                                if (metaDateMap.containsKey(resultEntry.getKey())) {
                                    try {
                                        Class<?> className = Class.forName((String)metaDateMap.get(resultEntry.getKey()));
                                        if (Date.class.isAssignableFrom(className)) {
                                            Date dateObject = dateFormat.parse((String)resultEntry.getValue());
                                            //dateObject.setTime(Integer.valueOf((Integer) resultEntry.getValue()));
                                            output.put(resultEntry.getKey(), dateObject);
                                        } else if (ZonedDateTime.class.isAssignableFrom(className)) {
                                            output.put(resultEntry.getKey(), ZonedDateTime.parse((String)resultEntry.getValue(), fmt));
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    output.put(resultEntry.getKey(), resultEntry.getValue());
                                }
                            }
                            output.remove("id");
                        return output;
                    }
                }
        );
    }

    public Iterator<BasicObject> queryDocument(String jsonQueryString) {
        SearchResponse searchResponse =
                transportClient.prepareSearch(INDEX_NAME).setTypes(DOC_TYPE).setQuery(QueryBuilders.wrapperQuery(jsonQueryString)).get();
        Iterator<SearchHit> searchHits = searchResponse.getHits().iterator();
        return Iterators.transform(searchHits, new Function<SearchHit, BasicObject>() {
                    public BasicObject apply(SearchHit input) {
                        BasicObject output = null;
                        try {
                            output = new BasicObject();
                            output.putAll(input.getSource());
                            //output = objectMapper.readValue(input.getSourceAsString(), BasicObject.class);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return output;
                    }
                }
        );
    }

    public boolean updateByQuery(String jsonQueryString) {
       // UpdateByQueryRequestBuilder updateByQueryRequestBuilder = UpdateByQueryAction.INSTANCE.newRequestBuilder(transportClient).source(INDEX_NAME);
        return false;
    }

    private static List<String> getList(String fieldName, Map<String, Object> mapProperties) {
        List<String> fieldList = new ArrayList<String>();
        Map<String, Object> map = (Map<String, Object>) mapProperties.get("properties");
        Set<String> keys = map.keySet();
        for (String key : keys) {
            if (((Map<String, Object>) map.get(key)).containsKey("type")) {
                fieldList.add( key);
            } else {
                //List<String> tempList = getList(fieldName + "" + key + ".", (Map<String, Object>) map.get(key));
                List<String> tempList = getList(key + ".", (Map<String, Object>) map.get(key));
                fieldList.addAll(tempList);
            }
        }
        return fieldList;
    }

    private Map<String, Object> getMetaData() {
        ClusterState cs = transportClient.admin().cluster().prepareState().setIndices(INDEX_NAME).execute().actionGet().getState();
        IndexMetaData imd = cs.getMetaData().index(INDEX_NAME);
        MappingMetaData mdd = imd.mapping(DOC_TYPE);
        Map<String, Object> map = null;

        try {
            map = mdd.getSourceAsMap();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, Object> metaDataMap = new HashMap<String, Object>();
        Map<String, Object> metaMap = (Map<String, Object>) map.get("_meta");
        Set<String> keys = metaMap.keySet();
        for (String key : keys) {
            metaDataMap.put(key, metaMap.get(key));
        }
        return metaDataMap;
    }
}
