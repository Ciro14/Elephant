package elefant.mape.analyzer;

public class Requirement {
	
	public String req_name;
	Double expected_value_min;
	Double expected_value_max;
	public String metric_id;
	
	public Requirement(String name, Double expected_value_low, Double expected_value_up, String id)
	{
		req_name = name;
		expected_value_min = expected_value_low;
		expected_value_max = expected_value_up;
		metric_id = id;
	}
	
	public boolean isInRange(Double value)
	{
		if(expected_value_min <= value && value <= expected_value_max) return true;
		
		return false;
	}
	
}