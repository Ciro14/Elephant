package de.uni_due.paluno.sse.elefant.knowledgebase;


import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import de.uni_due.paluno.sse.elefant.mape.analyzer.Requirement;
import de.uni_due.paluno.sse.elefant.mape.plan.Environment_State;
import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;

import static java.util.Arrays.asList;


public class Knowledge {

	MongoClient mongoClient;
	MongoDatabase db;
	
	Random random_generator = new Random();
	
	public Knowledge(String intial_db_name, String db_name)
	{
		mongoClient = new MongoClient( "localhost" , 27017 );
		db = mongoClient.getDatabase(db_name);
		//MongoDatabase initial_db = mongoClient.getDatabase(intial_db_name);
		//db = clone_database(initial_db, db_name);
		
		System.out.println();
	}
	
	public void drop_db(){
		db.drop();
	}
	
	public void store_permanent_db(String permanent_db_name) {
		clone_database(db,permanent_db_name);
	}
	
	public MongoDatabase clone_database(MongoDatabase fromDB, String db_name) {
		
		for (String name : mongoClient.listDatabaseNames())
			if (name.contains(db_name)) 
				mongoClient.getDatabase(db_name).drop();

				
		MongoDatabase clonned_db = mongoClient.getDatabase(db_name);

		
		for (String collectionName : fromDB.listCollectionNames()) {
			if(!collectionName.contains("system.indexes")&&!collectionName.contains("system.js")){
				List<Document> collectionToCopy = getCollectionAsList(fromDB,collectionName);			
				clonned_db.getCollection(collectionName).insertMany(collectionToCopy);
			}
		}
		return clonned_db;
	}
	
	private List<Document> getCollectionAsList(MongoDatabase fromDB, String collectionName){
		List<Document> collectionAsList = new ArrayList<Document>();
		
		FindIterable<Document> iterable = fromDB.getCollection(collectionName).find();
		for (Document document : iterable) {
			collectionAsList.add(document);
		}
		return collectionAsList;
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

			System.out.println(db.getCollection("measurements").count());
			
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

	
	public void update_reward_one_requirement(String current_env, String current_config, Hashtable<String, Double> current_req_values) 
	{

		String req_name = current_req_values.keySet().iterator().next();
		double new_req_value = current_req_values.get(req_name);
		
		if(has_no_reward_yet(current_env, current_config)) store_reward_one_requirement(current_env, current_config,current_req_values);
	
		double current_req_value = db.getCollection("rewards").find().iterator().next().getDouble(req_name);
		
		//take an average but we can play with this alot by giving different weights
		double next_req_value = (new_req_value + current_req_value)/2;

		
		db.getCollection("rewards").updateOne(new Document("environment",current_env).append("configuration", current_config),
		        new Document("$set", new Document(req_name, next_req_value)));
		
	}
	
	public void store_reward_one_requirement(String current_env, String current_config, Hashtable<String, Double> current_req_values) 
	{

		String req_name = current_req_values.keySet().iterator().next();
		double req_value = current_req_values.get(req_name);

		Document reward_doc = new Document()
				.append("environment", current_env)
				.append("configuration", current_config)
				.append(req_name, req_value);
		
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

	public String pick_random_config() {

		FindIterable<Document> valid_configs = db.getCollection("valid_configurations").find();
		ArrayList<String> list_valid_configs = new ArrayList<>();
		
		for (Document document : valid_configs) {
			list_valid_configs.add(document.getString("configuration"));
		}
		
		int num_valid_configs = list_valid_configs.size();
		if (num_valid_configs!=0) {
			String config = list_valid_configs.get(random_generator.nextInt(num_valid_configs));
//			System.out.println("pick_random_config: " + config);
			return config;
		}

		// no configuration
		System.out.println("WARNING in pick_random_config: no valid configuration was retrieved...");
		return "None";		
		
	}

	//this is a temporal method only for experiment purpose
	//specific method for response time
	public String find_config_best_responsetime(String current_env, String current_config) {

		
		FindIterable<Document> best = db.getCollection("rewards").find(new Document("environment",current_env)).sort(new Document("response_time",1));
		
		if (best.first()!=null)
			return best.first().getString("configuration");

		return "None";
	}

	public void store_accumu_num_req_violation(int num_req_violation) {

		Document req_vio_doc = new Document()
				.append("accumulative_req_violation", num_req_violation);
		
		db.getCollection("req_violation").insertOne(req_vio_doc);
		
	}
	

	public void store_situation_multi_req(String current_env, String current_config,
			Hashtable<String, Double> current_req_values) {

		Document situation_doc = new Document()
				.append("environment", current_env)
				.append("configuration", current_config);
		
		ArrayList<Document> req_doc_array = new ArrayList<>();
		
		for (Iterator<String> iterator = current_req_values.keySet().iterator(); iterator.hasNext();) {
			String  req_name = (String) iterator.next();
			req_doc_array.add(new Document().append("req_name", req_name).append("value", current_req_values.get(req_name)));			
		}
		
		situation_doc.append("requirments", req_doc_array);
		
		db.getCollection("situation").insertOne(situation_doc);
	}

	public void store_situation_one_req(String current_env, String current_config,
			Hashtable<String, Double> req_values) {
		
		String req_name = req_values.keySet().iterator().next();
		double req_value = req_values.get(req_name);
		
		Document situation_doc = new Document()
				.append("environment", current_env)
				.append("configuration", current_config)
				.append(req_name, req_value);
				
		db.getCollection("situation").insertOne(situation_doc);
	}	
	
}
