package elefant.FMevolution;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;


public class MongoManager {
	
	private final String DB_NAME = "icseXP";
	private MongoDatabase db;
	private MongoDatabase evo_db;
	private MongoDatabase mutable_db;
	private MongoClient mongoClient;
	
	private final String RULES = "rules";
	private final String REWARDS = "rewards";	
	
	public MongoManager() {
		mongoClient = new MongoClient();
		
		db = mongoClient.getDatabase(DB_NAME);
		evo_db = mongoClient.getDatabase(DB_NAME + "_evo");
		
		mutable_db = db;
		
		copyDatabase(db, evo_db);
	}
	
	private FindIterable<Document> getAllElements(String tableName) {
		FindIterable<Document> iterable = mutable_db.getCollection(tableName).find();
		return iterable;
	}
	
	private void deleteConfigInRule(List<String> config){
		final String invalidConfiguration = ConfigurationValidator.formatConfiguration(config);
		
		FindIterable<Document> iterable = getAllElements(RULES);
		iterable.forEach(new Block<Document>() {
		    @Override
		    public void apply(final Document document) {
		        List<String> configuration = (List<String>) document.get("configuration");
		        String formattedConfigurationFromDB = ConfigurationValidator.formatConfiguration(configuration);
		        if (formattedConfigurationFromDB.equalsIgnoreCase(invalidConfiguration)) {
		        	ObjectId _id = (ObjectId) document.get("_id");
		        	mutable_db.getCollection(RULES).updateOne(new Document("_id", _id),
		        	        new Document("$set", new Document("configuration", null)));
				}
		    }
		});		
	}
	
	// Not used yet
	private void addRule(Rule rule) {
		mutable_db.getCollection(RULES).insertOne(
				new Document()
						.append("rule_id", rule.getRule_id())
						.append("environment_state", asList(
								new Document()
										.append("metric_id", rule.getMetric_id())
										.append("operator", rule.getOperator())
										.append("metric_value", rule.getMetric_value())
								))
						.append("configuration", rule.getConfiguration())
				);
	}
	
	// Not used yet
	private void addReward(String environment, String config, String req_name, double req_value) {
		mutable_db.getCollection(REWARDS).insertOne(
				new Document()
						.append("environment", environment)
						.append("configuration", ConfigurationValidator.formatConfiguration(config))
						.append("requirments", asList(
								new Document()
										.append("req_name", req_name)
										.append("value", req_value)
								))
				);
	}
	
	/**
	 *  Delete a reward tuple related to the given @param invalidConfiguration
	 * @param invalidConfiguration
	 */
	private void deleteRewardWhereConfigIs(String invalidConfiguration){
		final String formattedInvalidConfig = ConfigurationValidator.formatConfiguration(invalidConfiguration);
		
		final List<ObjectId> tuplesTodelete = new ArrayList<ObjectId>();
		FindIterable<Document> iterable = getAllElements(REWARDS);
		iterable.forEach(new Block<Document>() {
		    @Override
		    public void apply(final Document document) {
		        String configuration = (String) document.get("configuration");
		        if (ConfigurationValidator.formatConfiguration(configuration).equalsIgnoreCase(formattedInvalidConfig)) {
		        	ObjectId _id = (ObjectId) document.get("_id");
					tuplesTodelete.add(_id);
				}
		    }
		});
		
		for (ObjectId _id : tuplesTodelete) {
			mutable_db.getCollection(REWARDS).deleteOne((new Document("_id", _id)));
		}
	}
	
	// Delete a reward tuple related to the given feature
	// Not used yet
	private void deleteRewardContainingFeature(final String removedFeature){
		final List<ObjectId> tuplesTodelete = new ArrayList<ObjectId>();
		FindIterable<Document> iterable = getAllElements(REWARDS);
		iterable.forEach(new Block<Document>() {
			@Override
			public void apply(final Document document) {
				List<String> configuration = Arrays.asList(((String) document.get("configuration")).split(","));
				if (configuration.contains(removedFeature)) {
					ObjectId _id = (ObjectId) document.get("_id");
					tuplesTodelete.add(_id);
				}
			}
		});
		
		for (ObjectId _id : tuplesTodelete) {
			mutable_db.getCollection(REWARDS).deleteOne((new Document("_id", _id)));
		}
	}
	
	private List<Document> getCollectionAsList(String collectionName){
		List<Document> collectionAsList = new ArrayList<Document>();
		
		FindIterable<Document> iterable = db.getCollection(collectionName).find();
		for (Document document : iterable) {
			collectionAsList.add(document);
		}
		return collectionAsList;
	}
	
	private void copyDatabase(MongoDatabase fromDB, MongoDatabase toDB) {
		for (String collectionName : fromDB.listCollectionNames()) {
			List<Document> collectionToCopy = getCollectionAsList(collectionName);			
			toDB.getCollection(collectionName).insertMany(collectionToCopy);
		}
	}
	
	public void pruneKnowledge(List<List<String>> invalidConfigurations) {
//		Rule rule = new Rule(6, "sessions", "=", 5, config);
//		addRule(rule);
		
//		double req_value = 5.0;
//		addReward("sessions=5", "[vm2,vm5]", "response_time", req_value);
//		addReward("sessions=5", "[vm6,vm5]", "response_time", req_value);

		
//		retireRuleAndMetrics(1.0);
		
//		deleteRewardWhereConfigIs("vm2,vm5");
		
		for (List<String> config : invalidConfigurations) {
			String listAsString = ConfigurationValidator.formatConfiguration(config);
			deleteRewardWhereConfigIs(listAsString);
			deleteConfigInRule(config);
		}
		
		
		
//		deleteRewardContainingFeature("vm5");		
	}
}

