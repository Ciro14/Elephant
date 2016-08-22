package elefant.evolution;

import java.util.Hashtable;

import elefant.knowledgebase.Knowledge;

public class RewardManager {

	private Knowledge monitor;
	
	public RewardManager(Knowledge mtr)
	{
		monitor = mtr;
	}

	public void store(String current_env, String current_config, Hashtable<String, Double> current_req_values) 
	{

		monitor.store_reward(current_env, current_config, current_req_values);
		
	}
	
}
