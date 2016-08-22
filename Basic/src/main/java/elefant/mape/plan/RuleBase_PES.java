package elefant.mape.plan;
import java.util.Hashtable;

import org.bson.Document;
import org.json.*;

import com.mongodb.client.FindIterable;

import elefant.knowledgebase.Configuration;
import elefant.knowledgebase.Knowledge;

public class RuleBase_PES {

	// <environment_state,configuration>
	Hashtable<String, String> rule_set = new Hashtable<>();
	
	public RuleBase_PES()
	{

	}
	
	public boolean loadRuleBaseFromMongoDB(Knowledge db)
	{
		// clean the rule set
		rule_set.clear();

		try {
				FindIterable<Document> doc_rules= db.get_rulebase();
				for (Document doc : doc_rules) {			
					JSONObject jObject_rule = new JSONObject(doc.toJson());
					JSONArray jArray_metric = (JSONArray) new JSONTokener(jObject_rule.getString("environment_state")).nextValue();				
					String environment_state = "";
					
					for (int j = 0; j < jArray_metric.length(); j++) {
						JSONObject jObject_metric = jArray_metric.getJSONObject(j);
						environment_state += jObject_metric.getString("metric_id") + "=" + jObject_metric.getString("metric_value") + "&";
					}
					Configuration config = new Configuration(jObject_rule.getString("configuration"));
					rule_set.put(environment_state, config.compact_sorted_string());			
				}
		} catch (Exception e) {
			System.out.println(e.toString());
			return false;
		}
		
		return true;
	}
	
	public boolean loadRuleBaseFromJsonFile(String fileName)
	{
		// clean the rule set
		rule_set.clear();

		try {
			String json_string = new Utility().readFile(fileName);
			System.out.println(json_string);

			JSONArray jArray_rule_set = (JSONArray) new JSONTokener(json_string).nextValue();
			
			for (int i = 0; i < jArray_rule_set.length(); i++) {

				JSONObject jObject_rule = jArray_rule_set.getJSONObject(i);
				JSONArray jArray_metric = (JSONArray) new JSONTokener(jObject_rule.getString("environment_state")).nextValue();				
				String environment_state = "";
				
				for (int j = 0; j < jArray_metric.length(); j++) {
					JSONObject jObject_metric = jArray_metric.getJSONObject(j);
					environment_state += jObject_metric.getString("metric_id") + "=" + jObject_metric.getString("metric_value") + "&";
				}
				String configuration = jObject_rule.getString("configuration");
				rule_set.put(environment_state, configuration);
				
			}
			
			
		} catch (Exception e) {
			System.out.println(e.toString());
			return false;
		}
		
		return true;
	}

	public String plan(String environment_state)
	{
		return rule_set.get(environment_state);
	}
	
}