package elefant.knowledgebase;


import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;

import elefant.mape.analyzer.Requirement;
import elefant.mape.plan.Environment_State;
import elefant.mape.plan.Environment_State_Space;

import static java.util.Arrays.asList;


public class Knowledge {

	MongoClient mongoClient;
	MongoDatabase db;
	
	public Knowledge()
	{
		mongoClient = new MongoClient( "10.8.100.124" , 27017 );
		db = mongoClient.getDatabase("test");
		
	}
	
	public ArrayList<Requirement> get_req_definition()
	{
		ArrayList<Requirement> req_Def_Array = new ArrayList<>();
		FindIterable<Document> coll = db.getCollection("requirement_definition").find();
		for (Document document : coll) {
			document.getString("req_name");
			req_Def_Array.add(new Requirement(document.getString("req_name"),document.getDouble("expected_value_min"),document.getDouble("expected_value_max"),document.getString("metric_id")));
		}
		return req_Def_Array;
	}

	public Hashtable<String, Double> get_req_current_value(ArrayList<Requirement> req_Def_Array, int valid_time_window)
	{

		Hashtable<String, Double> req_monitored_value = new Hashtable<>();
		long oldest_valid_time_milli = System.currentTimeMillis()-valid_time_window;
		
		try {
			
		AggregateIterable<Document> agg_metrics = db.getCollection("measurements").aggregate(asList(
				new Document("$match", new Document("time", new Document("$gt", oldest_valid_time_milli))),
		        new Document("$group", new Document("_id", "$metricid").append("avgValue", new Document("$avg", "$value")))));
				
		for (Requirement req_def : req_Def_Array)
			for (Document document : agg_metrics)
			{
				String id = document.getString("_id");
				if (id!=null)
					if (req_def.metric_id.compareTo(id)==0) 
						req_monitored_value.put(req_def.req_name,document.getDouble("avgValue"));
		
			}
		
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		
		return req_monitored_value;

	}

	public void store_reward(String current_env, String current_config, Hashtable<String, Double> current_req_values) 
	{

		Document reward_doc = new Document()
				.append("environment", current_env)
				.append("configuration", current_config);
		
		ArrayList<Document> req_doc_array = new ArrayList<>();
		
		for (Iterator<String> iterator = current_req_values.keySet().iterator(); iterator.hasNext();) {
			String  req_name = (String) iterator.next();
			req_doc_array.add(new Document().append("req_name", req_name).append("value", current_req_values.get(req_name)));			
		}
		
		reward_doc.append("requirments", req_doc_array);
		
		db.getCollection("rewards").insertOne(reward_doc);

	}
	
	// the output should be a hashMap with metricid and value of each metric
	public Hashtable<String, Double> get_metrics_value(ArrayList<String> metric_list, int valid_time_window)
	{

		Hashtable<String, Double> metrics_value = new Hashtable<>();
		long oldest_valid_time_milli = System.currentTimeMillis() - valid_time_window;

		try {

				AggregateIterable<Document> agg_metrics = db.getCollection("measurements").aggregate(asList(
				new Document("$match", new Document("time", new Document("$gt", oldest_valid_time_milli))),
		        new Document("$group", new Document("_id", "$metricid").append("avgValue", new Document("$avg", "$value")))));
		
				
//		MongoCollection<Document> coll = db.getCollection("metrics");
//		AggregateIterable<Document> agg_metrics = coll.aggregate(asList(
//		     new Document("$group", new Document("_id", "$metricid").append("avg", new Document("$avg", "$value")))));

				for (String metric_id : metric_list)
					for (Document document : agg_metrics)
					{
						String id = document.getString("_id");
						if (id!=null)
							if (metric_id.compareTo(id)==0) 
								metrics_value.put(metric_id,document.getDouble("avgValue"));
					}
		
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return metrics_value;

	}
	
	public FindIterable<Document> get_rulebase()
	{
		return db.getCollection("rules").find();
	}

	public FindIterable<Document> get_environment_definition() {
		return db.getCollection("environment_definition").find();
	}

	public FindIterable<Document> get_metrics_modes_definition() {
		return db.getCollection("metrics_mode").find();
	}

	public String find_config_never_examined(String current_env) {
		
		FindIterable<Document> valid_configs = db.getCollection("valid_configurations").find();
		
		for (Document doc : valid_configs)
			if (has_no_reward_yet(current_env,doc.getString("configuration"))) return doc.getString("configuration");
		
		// all configurations are already explored
		return "None";
	}

	// this method is not tested yet for any false case
	private boolean has_no_reward_yet(String env, String config) {

		
		Iterator<Document> iterable = db.getCollection("rewards").aggregate(asList(
				new Document("$match", new Document("configuration", config).append("environment", env)),
		        new Document("$group", new Document("_id", 1).append("count", new Document("$sum", 1))))).iterator();

		int count = iterable.hasNext() ? (Integer)iterable.next().get("count") : 0;		
		
		if (count==0) return true;
			
		return false;
	}

	// there is a doubt about this method, and whether or not the 
	public void replace_rule(String current_env, String current_config, String proposed_config) {
		
		Configuration config = new Configuration(proposed_config);
		Environment_State es = new Environment_State(current_env);
		
		db.getCollection("rules").updateOne(new Document("environment_state", es.toJsonArray()),
		        new Document("$set", new Document("configuration", config.toJsonArray())));

	}

	public void clear_measurements() {

		db.getCollection("measurements").drop();
	}

	public void clear_rewards() {
		db.getCollection("rewards").drop();
	}

}
