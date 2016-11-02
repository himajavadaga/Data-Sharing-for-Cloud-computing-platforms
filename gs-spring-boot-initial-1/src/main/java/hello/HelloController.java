package hello;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.parser.JSONParser;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import redis.clients.jedis.Jedis;

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
	
	//display id data of a user
	@RequestMapping(value = "/{uriType}/{id}", method= RequestMethod.GET ,produces = "application/json")	
	public ResponseEntity<Object> get(@RequestHeader  HttpHeaders headers, @PathVariable String uriType, @PathVariable String id)
		throws Exception{
		
		HashMap hm= new HashMap ();
		JSONParser parser = new JSONParser();
		Jedis jedis = new Jedis("127.0.0.1");
		String result =jedis.get("Test"+id);
		System.out.println("re:"+result);
		String Result;
		if (hm.containsKey(id))
			Result = (jedis.get(id));
		    else
			Result = ("Key("+id+") doesn't exist");
		HttpHeaders httpHeaders = new HttpHeaders();
		return new ResponseEntity (Result, httpHeaders, org.springframework.http.HttpStatus.CREATED);
		
	}

}
