package elefant.FMevolution;

import static java.util.Arrays.asList;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import es.us.isa.FAMA.Reasoner.Question;
import es.us.isa.FAMA.Reasoner.QuestionTrader;
import es.us.isa.FAMA.Reasoner.questions.ProductsQuestion;
import es.us.isa.FAMA.Reasoner.questions.ValidProductQuestion;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.featureModel.GenericFeatureModel;
import es.us.isa.FAMA.models.featureModel.Product;
import es.us.isa.FAMA.models.variabilityModel.GenericProduct;


public class MongoManager {
	
	private final String DB_NAME = "icseXP";
	private MongoDatabase db;
	private MongoDatabase evo_db;
	private MongoDatabase mutable_db;
	private MongoClient mongoClient;
	
	private final String RULES = "rules";
	private final String METRICS = "metrics_mode";
	private final String REWARDS = "rewards";	
	private final String RETIRED_RULES = "retired_rules";
	private final String RETIRED_METRICS = "retired_metrics_mode";
	
	public MongoManager() {
		mongoClient = new MongoClient();
		
		db = mongoClient.getDatabase(DB_NAME);
		evo_db = mongoClient.getDatabase(DB_NAME + "_evo");
		
		mutable_db = evo_db;
		
//		copyDatabase(db, evo_db);
	}
	
	private FindIterable<Document> getAllElements(String tableName) {
		FindIterable<Document> iterable = mutable_db.getCollection(tableName).find();
		return iterable;
	}
		
	// Move a rule from the rules table to the retired_rules one
	// And move the related metric from the metrics_mode table to the retired_metrics_mode table 
	// OK
	private void retireRuleAndMetrics(double rule_id){
		FindIterable<Document> rule = mutable_db.getCollection(RULES).find(new Document("rule_id", rule_id));
		Document env_state = (Document) ((List<Object>) rule.first().get("environment_state")).get(0);
		double metric_id = (double) env_state.get("metric_value");
		
		FindIterable<Document> metric = mutable_db.getCollection(METRICS).find(new Document("mode", metric_id));
				
		MongoCollection<Document> ruleCollection = mutable_db.getCollection(RETIRED_RULES);
		ruleCollection.insertOne(rule.first());
		
		MongoCollection<Document> metricCollection = mutable_db.getCollection(RETIRED_METRICS);
		metricCollection.insertOne(metric.first());
		
		mutable_db.getCollection(RULES).deleteOne(new Document("rule_id", rule_id));
		mutable_db.getCollection(METRICS).deleteOne(new Document("mode", metric_id));
	}
	
	private void deleteRuleAndMetrics(List<String> config){
		final String invalidConfiguration = ConfigurationValidator.formatConfiguration(config);
		
		final List<ObjectId> rulesToDelete = new ArrayList<ObjectId>();
		final List<Integer> metricsToDelete = new ArrayList<Integer>();
		FindIterable<Document> iterable = getAllElements(RULES);
		iterable.forEach(new Block<Document>() {
		    @Override
		    public void apply(final Document document) {
		        List<String> configuration = (List<String>) document.get("configuration");
		        String formattedConfigurationFromDB = ConfigurationValidator.formatConfiguration(configuration);
		        if (formattedConfigurationFromDB.equalsIgnoreCase(invalidConfiguration)) {
		        	ObjectId _id = (ObjectId) document.get("_id");
					rulesToDelete.add(_id);
					Document env_state = (Document) ((List<Object>) document.get("environment_state")).get(0);
					int metric_id = (int) env_state.get("metric_value");
					metricsToDelete.add(metric_id);
				}
		    }
		});		
		for (ObjectId _id : rulesToDelete) {
			mutable_db.getCollection(RULES).deleteOne((new Document("_id", _id)));
		}
		for (double metric_id : metricsToDelete) {
			mutable_db.getCollection(METRICS).deleteOne((new Document("mode", metric_id)));
		}
	}
	
	// Add a rule in the rules table
	// OK!
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
	
	// Add a new reward value
	// OK!
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
	
	// Delete a reward tuple related to the given configuration
	// OK!
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
	// OK!
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
			deleteRuleAndMetrics(config);
		}
		
		
		
//		deleteRewardContainingFeature("vm5");		
	}
}

