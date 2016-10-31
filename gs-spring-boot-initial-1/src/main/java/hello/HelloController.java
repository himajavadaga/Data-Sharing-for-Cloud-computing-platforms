package hello;

import org.springframework.web.bind.annotation.RestController;

import redis.clients.jedis.Jedis;

import java.text.ParseException;
import java.util.HashMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RestController
public class HelloController {

	@RequestMapping("/")
	public String index() {
		return "Rest-REDIS!";
	}

	@RequestMapping(value = "/plan", method = RequestMethod.GET, consumes = "application/json")
	public String create(@RequestHeader HttpHeaders headers, @RequestBody String entity)
			throws Exception {

		JSONParser parser = new JSONParser();
		// parser to parse through postman
		Jedis jedis = new Jedis("127.0.0.1");
		// jedis object creation
		JSONObject jsonobject = (JSONObject) parser.parse(entity);
		// jsonobject to parse through jedis entity
		jedis.set("Test123", jsonobject.toJSONString());
		// setting jedis object and convert to string for displaying it on
		// postman
		System.out.println(jedis.get("Test123"));

		/*
		 * HashMap object= new HashMap (); Jedis jedis = new Jedis("127.0.0.1");
		 * 
		 * jedis.set("Test123", entity);
		 * 
		 * String s = jedis.get("Test123"); System.out.println(s); JSONParser
		 * parser = new JSONParser(); object =(HashMap) parser.parse( entity);
		 */

		return jsonobject.toJSONString();
		// Postman displays the result
	}

	// crud operations

}
