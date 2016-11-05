package hello;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpStatus;
import org.json.simple.parser.JSONParser;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;



import redis.clients.jedis.Jedis;

@RestController
public class HelloController {

	@RequestMapping("/")
	public String index() {
		return "Rest-REDIS!";
	}

	@RequestMapping(value = "/{uriType}", method = RequestMethod.POST, consumes = "application/json")
	public ResponseEntity<Object> create(@RequestHeader HttpHeaders headers, @RequestBody String entity)
			throws Exception {

		HashMap object = new HashMap();
		JSONParser parser = new JSONParser();
		Jedis jedis = new Jedis("127.0.0.1");
		object = (HashMap) parser.parse(entity);
		jedis.set("Test123", object.toString());
		System.out.println(jedis.get("Test123"));
		String j = object.toString();
		HttpHeaders httpHeaders = new HttpHeaders();
		return new ResponseEntity(j, httpHeaders, org.springframework.http.HttpStatus.CREATED);

	}

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
		if (hm.containsKey("id")) {
			String storedID = (String) (hm.get("id"));
			HttpHeaders httpHeaders = new HttpHeaders();
			return new ResponseEntity<Object>(result, httpHeaders, org.springframework.http.HttpStatus.OK);
		} else {
			result = ("Key(" + id + ") doesn't exist");
			HttpHeaders httpHeaders = new HttpHeaders();
			return new ResponseEntity<Object>(httpHeaders, org.springframework.http.HttpStatus.NOT_FOUND);
		}
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
		//	result.delete(); // delete the object
			jedis.del("Test" + id);
			HttpHeaders httpHeaders = new HttpHeaders();
			return new ResponseEntity<Void>(httpHeaders, org.springframework.http.HttpStatus.OK);
		} else
			result = ("Key(" + id + ") doesn't exist");
		HttpHeaders httpHeaders = new HttpHeaders();
		return new ResponseEntity<Void>(httpHeaders, org.springframework.http.HttpStatus.NOT_FOUND);
	}

	// MERGE id data of a user
	@RequestMapping(value = "/{uriType}/{id}", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
	public ResponseEntity<Object> merge(@RequestHeader HttpHeaders headers, @RequestBody String entity,
			@PathVariable String uriType, @PathVariable String id) throws Exception {

		HashMap hm = new HashMap();
		JSONParser parser = new JSONParser();
		Jedis jedis = new Jedis("127.0.0.1");
		String result = jedis.get("Test" + id);
		System.out.println("re:" + result);
		hm = (HashMap) parser.parse(entity);
		if (hm.containsKey(id)) {
			jedis.set("Test123", hm.toString()); // for specific ID ask
													// professor
			System.out.println(jedis.get("Test123"));
			String j = hm.toString();
			// update the key in the database and return
			HttpHeaders httpHeaders = new HttpHeaders();
			return new ResponseEntity(j, httpHeaders, org.springframework.http.HttpStatus.CREATED);
		} else {
			result = ("Key(" + id + ") doesn't exist");
			HttpHeaders httpHeaders = new HttpHeaders();
			return new ResponseEntity<Object>(httpHeaders, org.springframework.http.HttpStatus.NOT_FOUND);
		}

	}
}
