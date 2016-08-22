package elefant.mape.plan;

public class Metric {

	String metric_id;
	String sensor_id;
	String aggregation_operator;
	Integer time_window;
	
	public Metric(String mid, String sid, String ao, Integer tw)
	{
		metric_id = mid;
		sensor_id = sid;
		aggregation_operator = ao;
		time_window = tw;
	}
	
}
