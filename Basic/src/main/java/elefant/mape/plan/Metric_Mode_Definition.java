package elefant.mape.plan;
import java.util.ArrayList;

public class Metric_Mode_Definition {

	private String metricId;
	private ArrayList<Mode> mode = new ArrayList<Mode>();


	public String getMetricId() {
		return metricId;
	}

	public void setMetricId(String metricId) {
		this.metricId = metricId;
	}

	public ArrayList<Mode> getMode() {
		return mode;
	}

	public void setMode(ArrayList<Mode> mode) {
		this.mode = mode;
	}

}