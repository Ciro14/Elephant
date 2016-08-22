package elefant.evolution;

import elefant.knowledgebase.Knowledge;

public class RuleManager {

	Knowledge knowledge = new Knowledge();

	public RuleManager(Knowledge kng)
	{
		knowledge = kng;
	}

	public boolean replace_rule(String current_env, String current_config) {
		
		//exploit
//		String proposed_config = knowledge.find_config_best_reward_except_current(current_env,current_config);
	
		//explore
		String proposed_config = knowledge.find_config_never_examined(current_env);

//		String proposed_config = knowledge.get_random_config(current_env,current_config);
		
		if (proposed_config.compareTo("None")!=0) {
			knowledge.replace_rule(current_env, current_config, proposed_config);
			return true;
		}
		
		// no rule has been updated
		return false;
	}


	
}
