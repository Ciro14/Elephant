package elefant.mape.plan;
public class Mode {

	public int name;
	private double min_value;
	private double max_value;
	
	public Mode(int mode_name, double min, double max)
	{
		name = mode_name;
		min_value = min;
		max_value = max;
	}

	public boolean isInRange(double value) {
		
		if (min_value<=value&&value<=max_value)return true;

		return false;
	}
	
}