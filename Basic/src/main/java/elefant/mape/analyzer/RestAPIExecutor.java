package elefant.mape.analyzer;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

public class RestAPIExecutor {

	public String get_restapi_json(String url)
	{
		try{
			Client client = ClientBuilder.newClient();
			WebTarget target= client.target(url);
			Invocation.Builder invocationBuilder = target.request(MediaType.TEXT_PLAIN);
			javax.ws.rs.core.Response response = invocationBuilder.get();
			return response.readEntity(String.class);
		} catch(Exception e) {System.out.println(e.toString());}
		
		return "Failed Planning";
	}
	
	public void post_restapi_json(String url, String content)
	{
		try {	
			Client client = ClientBuilder.newClient();
			WebTarget target= client.target(url);
			Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON);
			javax.ws.rs.core.Response response = invocationBuilder.post(Entity.entity(content,MediaType.APPLICATION_JSON));
			System.out.println("reconfiguration execution status: " + response.readEntity(String.class));
			response.close();
		} catch (Exception e) {	System.out.println(e.toString());}
	}	
	
}