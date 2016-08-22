package elefant.mape.plan;

import java.util.ArrayList;
import java.util.Hashtable;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.mongodb.client.FindIterable;

import elefant.knowledgebase.Knowledge;

public class Environment_State_Space {

//	Hashtable<String, Integer> metric_values = new Hashtable<>();
	
	private ArrayList<String> env_metric_list;
	private Hashtable<String , ArrayList<Mode>> metrics_modes_def;

	public Environment_State_Space() {
		env_metric_list = new ArrayList<String>();
		metrics_modes_def = new Hashtable<String , ArrayList<Mode>>();
	}

	public boolean load_metric_definition_from_file(String fileName)
	{
		env_metric_list.clear();
		try {
			String json_string = new Utility().readFile(fileName);

			JSONArray jArray_metric_set = (JSONArray) new JSONTokener(json_string).nextValue();
			
			for (int i = 0; i < jArray_metric_set.length(); i++) 
			{
				JSONObject jObject_metric = jArray_metric_set.getJSONObject(i);
				env_metric_list.add(jObject_metric.getString("metricid"));
			}
			
		} catch (Exception e) {
			System.out.println(e.toString());
			return false;
		}
		return true;
	}
	
	
	public boolean load_environment_definition_from_db(Knowledge db)
	{
		env_metric_list.clear();
		try {
			
			FindIterable<Document> doc_env_def = db.get_environment_definition();
			for (Document doc : doc_env_def) {
				JSONObject jObject_metric = new JSONObject(doc.toJson());
				env_metric_list.add(jObject_metric.getString("metricid"));
			}
			
			FindIterable<Document> doc_modes = db.get_metrics_modes_definition();
			for (Document doc : doc_modes) {
				JSONObject jObject_mode = new JSONObject(doc.toJson());
				String metric_id = jObject_mode.getString("metric_id");
				if (metrics_modes_def.get(metric_id)==null) metrics_modes_def.put(metric_id, new ArrayList<Mode>());
				Mode mode = new Mode(jObject_mode.getInt("mode"),jObject_mode.getDouble("min_value"),jObject_mode.getDouble("max_value"));
				metrics_modes_def.get(metric_id).add(mode);
			}

		} catch (Exception e) {
			System.out.println(e.toString());
			return false;
		}
		return true;
	}

	public String get_current_state(Knowledge db, int valid_time_window) {
		
		String current_state = "";
		Hashtable<String, Double> metrics_value = db.get_metrics_value(env_metric_list, valid_time_window);

		for (String metric_id : metrics_value.keySet()) {
			double value = metrics_value.get(metric_id);
			System.out.println("average value of " + metric_id + " is " +  value);
			Mode mode = find_mode(metric_id, value);
			if (mode!=null)	current_state+=metric_id+"="+mode.name+"&";
			else current_state+=metric_id+"=?&";		
		}
		
		return current_state;
	}

	private Mode find_mode(String metric_id, double value) {

		ArrayList<Mode> array_mode = metrics_modes_def.get(metric_id);
		
		for (int i = 0; i < array_mode.size(); i++)
			if (array_mode.get(i).isInRange(value)) return array_mode.get(i);
		
		return null;
	}
	
	

}
