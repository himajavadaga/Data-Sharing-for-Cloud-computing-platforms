package CRUD;

import java.io.Reader;
import java.net.InetAddress;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.File;
import java.util.ArrayList;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.httpclient.HttpStatus;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.appserv.util.cache.Cache;

import java.util.Iterator;
import redis.clients.jedis.Jedis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

@RestController
public class HelloController {

	public String index() {
		return "Rest-REDIS!";
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/{uriType}", method = RequestMethod.POST, consumes = "application/json")
	public ResponseEntity<Object> create(@RequestHeader HttpHeaders headers, @RequestBody String entity)
			throws Exception {

		JSONObject object = new JSONObject();
		JSONParser parser = new JSONParser();
		Jedis jedis = new Jedis("127.0.0.1");
		object = (JSONObject) parser.parse(entity);
		String value = jedis.set("Test" + object.get("id"), object.toString());
		System.out.println(jedis.get("Test" + object.get("id")));

		MessageDigest messageDigest = MessageDigest.getInstance("MD5");
		byte[] bytesOfMessage = object.toJSONString().getBytes("UTF-8");
		byte[] thedigest = messageDigest.digest(bytesOfMessage);
		String eTag = thedigest.toString();
		object.put("ETag", eTag);

		// jedis.set(id, jsonObj.toJSONString());
		value = jedis.set("Test" + object.get("id"), object.toString());

		String j = object.toString();
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set("ETag", eTag);
		return new ResponseEntity(j, httpHeaders, org.springframework.http.HttpStatus.CREATED);

	

	// display id data of a user
	@RequestMapping(value = "/{uriType}/{id}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Object> get(@RequestHeader HttpHeaders headers, @PathVariable String uriType,
			@PathVariable String id) throws Exception {

		HashMap hm = new HashMap();
		JSONParser parser = new JSONParser();
		Jedis jedis = new Jedis("127.0.0.1");
		String result = jedis.get("Test" + id);
		System.out.println("re:" + result);
		hm = (HashMap) parser.parse(result);

		String eTag = headers.getFirst("If-None-Match");
		if (eTag != null) {
			if (!eTag.isEmpty() && result.contains(eTag)) {
				// headers..sendError(304, "Object is not changed");
				HttpHeaders httpHeaders = new HttpHeaders();
				// httpHeaders.set(", headerValue);
				return new ResponseEntity<Object>(httpHeaders, org.springframework.http.HttpStatus.NOT_MODIFIED);
			}
		} else {
			HttpHeaders httpHeaders = new HttpHeaders();
			return new ResponseEntity<Object>(result, httpHeaders, org.springframework.http.HttpStatus.OK);

		}
		return null;

	}

	// delete id data of a user
	@RequestMapping(value = "/{uriType}/{id}", method = RequestMethod.DELETE, produces = "application/json")
	public ResponseEntity<Void> delete(@RequestHeader HttpHeaders headers, @PathVariable String uriType,
			@PathVariable String id) throws Exception {

		HashMap hm = new HashMap();
		JSONParser parser = new JSONParser();
		Jedis jedis = new Jedis("127.0.0.1");
		String result = jedis.get("Test" + id);
		System.out.println("re:" + result);
		hm = (HashMap) parser.parse(result);
		if (hm.containsKey("id")) {
			// yt result.delete(); // delete the object
			jedis.del("Test" + id);
			HttpHeaders httpHeaders = new HttpHeaders();
			return new ResponseEntity<Void>(httpHeaders, org.springframework.http.HttpStatus.OK);
		} else
			result = ("Key(" + id + ") doesn't exist");
		HttpHeaders httpHeaders = new HttpHeaders();
		return new ResponseEntity<Void>(httpHeaders, org.springframework.http.HttpStatus.NOT_FOUND);
	}

	// MERGE id data of a user
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/{uriType}/{id}", method = RequestMethod.PATCH, consumes = "application/json")
	public String merge(@RequestHeader HttpHeaders headers, @PathVariable("id") String id, @PathVariable String uriType,
			@RequestBody String entity) throws Exception {

		JSONParser parser = new JSONParser();
		Jedis jedis = new Jedis("127.0.0.1");
		JSONObject storedObj = (JSONObject) parser.parse(jedis.get("Test" + id));
		String stored = jedis.get("Test" + id); // getting data as a blob

		System.out.println("stored is " + stored);

		if (!stored.isEmpty()) {
			System.out.println(" ID Found " + stored);
		} else
			System.out.println("ID not found");

		JSONObject jsonMap = new JSONObject(); // used for storing final output

		HashMap<String, Object> entitiyBody = (HashMap<String, Object>) parser.parse(entity);

		HashMap<String, List> map2 = new HashMap<>();
		HashMap<String, HashMap> map = new HashMap<>();
		try {

			parse(storedObj, map, map2);

			String add_key = "__" + uriType + "__" + id;// creating key in
														// memory(uritype+id)

			System.out.println("Map2 completed parse : " + map2);

			HashMap<String, List> patchMap2 = new HashMap<>();

			HashMap<String, HashMap> patchMap = new HashMap<>();
			entitiyBody.put("id", id);

			entitiyBody.put("type", uriType);
			parse(entitiyBody, patchMap, patchMap2);

			map.get(add_key).putAll(patchMap.get(add_key));

			for (String key : patchMap2.keySet()) {
				if (map2.containsKey(key)) {

					ArrayList<String> list = (ArrayList) patchMap2.get(key);
					ArrayList maplist = (ArrayList) map2.get(key);

					for (String listKey : list) {
						if (!maplist.contains(listKey)) {
							maplist.add(listKey);
						}
						if (map.containsKey(listKey)) {

							HashMap<String, Object> temp = (HashMap) map.get(listKey);
							temp.putAll(patchMap.get(listKey));
							map.put(listKey, temp);
						} else {
							map.put(listKey, patchMap.get(listKey));
						}
					}

				} else {
					map2.put(key, patchMap2.get(key));
				}
			}

			HashMap<String, Object> output = new HashMap<>();
			output.putAll((Map<? extends String, ? extends Object>) map.get("__" + uriType + "__" + id));

			for (String key : map2.keySet()) {

				ArrayList<HashMap> listMap = new ArrayList<>();
				ArrayList<String> value = (ArrayList) map2.get(key);
				System.out.println("patchMap2 key is: " + key);

				String[] sp = key.split("__");
				String addr = sp[3];

				for (String mapKey : value) {

					listMap.add((HashMap) map.get(mapKey));
				}
				output.put(addr, listMap);
				jsonMap.putAll(output);
				System.out.println("JSON Object map : " + jsonMap.toJSONString());

				jedis.set("Test" + id, jsonMap.toJSONString());

				System.out.println(map.toString());
			}

		} catch (Exception e) {
			e.printStackTrace();
			return "BadRequest.PatchRequestNotValidated";
		}

		return "Patch request successful" + jsonMap.toJSONString();
	}

	// Parse method
	@SuppressWarnings("rawtypes")
	public String parse(HashMap<String, Object> storedObj, HashMap<String, HashMap> map, HashMap<String, List> map2)
			throws Exception {
		String id = (String) ((Map) storedObj).get("id");

		String type = (String) ((Map) storedObj).get("type");
		HashMap temp = new HashMap();
		Iterator iterator = storedObj.keySet().iterator();
		for (String key : storedObj.keySet()) {

			Object value = storedObj.get(key);
			System.out.println("key" + key);
			if (value instanceof Map) {
				// recurse call the method again

				ArrayList list = new ArrayList<>();
				String value_id = (String) ((Map) value).get("id");
				String value_type = (String) ((Map) value).get("type");
				String relation_key = "__" + type + "__" + id + "__" + key;
				String relation_value = "__" + value_type + "__" + value_id;
				list.add(relation_value);
				System.out.println(" relation_key" + relation_key);
				if (map2.containsKey(relation_key))
					list = (ArrayList) map2.get(relation_key);
				map2.put(relation_key, list);
				System.out.println(" Map2 contains: " + map2.toString());
				parse((HashMap) value, map, map2);
			} else if (value instanceof List) {

				Object entry = ((List) value).get(0);// checking first entry
				System.out.println("entry" + entry);
				if (entry instanceof Map) { // copy above
					for (int i = 0; i < ((List) value).size(); i++) {
						HashMap entityMap = (HashMap) ((List) value).get(i);
						ArrayList list = new ArrayList<>();
						String value_id = (String) ((Map) entityMap).get("id");
						String value_type = (String) ((Map) entityMap).get("type");
						String relation_key = "__" + type + "__" + id + "__" + key;
						String relation_value = "__" + value_type + "__" + value_id;
						if (map2.containsKey(relation_key))
							list = (ArrayList) map2.get(relation_key);
						list.add(relation_value);
						map2.put(relation_key, list);
						System.out.println(" Map2 contains: " + map2.toString());
						parse((HashMap) entityMap, map, map2);
					}
				}
			} else {
				temp.put(key, (String) value);
				map.put("__" + type + "__" + id, temp);
				System.out.println("map contains " + map.toString());
			}
		}
		return null;
	}
}
