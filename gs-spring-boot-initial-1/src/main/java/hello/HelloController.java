package hello;

import org.springframework.web.bind.annotation.RestController;

import redis.clients.jedis.Jedis;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Produces;

import org.json.simple.JSONArray;
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

	@RequestMapping(value = "/plan", method = RequestMethod.POST, consumes = "application/json")
	public String create(@RequestHeader HttpHeaders headers, @RequestBody String entity)
			throws Exception {

		HashMap object= new HashMap ();
		JSONParser parser = new JSONParser();
		Jedis jedis = new Jedis("127.0.0.1");
		object = (HashMap) parser.parse(entity);
		jedis.set("Test123", object.toString());
		System.out.println(jedis.get("Test123"));
		return object.toString();
	}
	//display all data
	@RequestMapping(value = "/plan", method= RequestMethod.GET)	
	public String display(@RequestHeader HttpHeaders headers )
		throws Exception{
		
		JSONParser parser = new JSONParser();
		Jedis jedis = new Jedis("127.0.0.1");
		JSONObject jsonobject = (JSONObject) parser.parse();
		jedis.set("Test123", jsonobject.toJSONString());
		System.out.println(jedis.get("Test123"));
		return jsonobject.toJSONString();
	}
	//display all data of a user
	@RequestMapping(value = "/plan/{id}", method= RequestMethod.GET)	
	public String displayuser(@RequestHeader HttpHeaders headers )
		throws Exception{
		
		JSONParser parser = new JSONParser();
		Jedis jedis = new Jedis("127.0.0.1");
		JSONObject jsonobject = (JSONObject) parser.parse();
		jedis.set("Test123", jsonobject.toJSONString());
		System.out.println(jedis.get("Test123"));
		return jsonobject.toJSONString();
	}

}
