package CRUD;

import java.net.InetAddress;
import java.util.ArrayList;

import org.apache.lucene.search.vectorhighlight.FieldQuery;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

public class SearchAPI {
	private final static String HOST_NAME = "localhost";
	private final static String INDEX_NAME = "test";
	private TransportClient client = null;

	private TransportClient getClient() {
		TransportClient transportClient = null;
		try {
			transportClient = new PreBuiltTransportClient(Settings.EMPTY)
					.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(HOST_NAME), 9300));
		} catch (Exception e) {
			System.out.println("Error Creating Client:" + e.getMessage());
			e.printStackTrace();
		}
		return transportClient;
	}

	public SearchAPI() {
		// TODO Auto-generated constructor stub
		this.client = getClient();
	}

	/*
	 * Takes parameter in following format q=col:val,col:val
	 */
	public ArrayList<String> search(String query, int start, int pageSize, String timeout) {
		ArrayList<String> result = null;
		SearchRequestBuilder searchRequest = client.prepareSearch(INDEX_NAME);
		
		BoolQueryBuilder boolQuery = new BoolQueryBuilder();
		if (query != null && !query.isEmpty()) {
			for (String s : query.split(",")) {
				String[] a = s.split(":");
				boolQuery.must(QueryBuilders.matchQuery(a[0], a[1]));
			}
		}		
		searchRequest = searchRequest.setQuery(boolQuery);
		searchRequest= searchRequest.setFrom(start).setSize(pageSize).setExplain(true);
		SearchResponse response = searchRequest.get(timeout);

		System.out.println("Request:"+searchRequest.toString());
		System.out.println("Time(ms):"+response.getTookInMillis()+", Numfound:"+response.getHits().totalHits());
		SearchHits SearchHits= response.getHits();
		if(SearchHits.totalHits()!=0){
			result=new ArrayList<String>();
			for (SearchHit hit : SearchHits.hits()) {
			result.add(hit.getSource().toString());
		}}
		return result;
	}

}
