package elefant.mape.plan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.bson.Document;

public class Environment_State {

	HashMap<String, Integer> metric_mode = new HashMap<>();
	
	public Environment_State(String current_env) {
		
		StringTokenizer multiple_metric_tokenizer = new StringTokenizer(current_env, "&");

		while (multiple_metric_tokenizer.hasMoreTokens()) {
			String element = multiple_metric_tokenizer.nextToken();
			StringTokenizer equality_tokenizer = new StringTokenizer(element, "=");
			String metric_name=equality_tokenizer.nextToken();
			int mode=Integer.parseInt(equality_tokenizer.nextToken());
			metric_mode.put(metric_name, mode);
		}
	}

	public ArrayList<Document> toJsonArray()
	{
		ArrayList<Document> jsonArray = new ArrayList<>();

		for (Iterator<String> iterator = metric_mode.keySet().iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			Document doc = new Document().append("metric_id", key).append("operator", "=").append("metric_value", metric_mode.get(key));
			jsonArray.add(doc);
		}
		return jsonArray;
	}
	
}
