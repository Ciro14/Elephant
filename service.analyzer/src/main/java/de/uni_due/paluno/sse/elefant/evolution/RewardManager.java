package de.uni_due.paluno.sse.elefant.evolution;

import java.util.Hashtable;

import de.uni_due.paluno.sse.elefant.knowledgebase.Knowledge;

public class RewardManager {

	private Knowledge monitor;
	
	public RewardManager(Knowledge mtr)
	{
		monitor = mtr;
	}

	public void update(String current_env, String current_config, Hashtable<String, Double> current_req_values) 
	{
		monitor.update_reward_one_requirement(current_env, current_config, current_req_values);
	}
	
}
