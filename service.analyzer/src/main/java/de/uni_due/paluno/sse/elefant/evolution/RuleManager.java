package de.uni_due.paluno.sse.elefant.evolution;

import java.util.Random;

import de.uni_due.paluno.sse.elefant.knowledgebase.Knowledge;

public class RuleManager {

	Knowledge knowledge;
	Random random_generator = new Random();
	double exploitation_percent;

	
	public RuleManager(Knowledge kng, double exploit_percent)
	{
		knowledge = kng;
		exploitation_percent = exploit_percent;
	}

	public boolean replace_rule(String current_env, String current_config) {
		
		String proposed_config;
		
		// probabilistically and randomly choose between exploitation and exploration
		if (random_generator.nextInt(100)<exploitation_percent) {
			//exploit
			proposed_config = knowledge.find_config_best_responsetime(current_env,current_config);
			System.out.println("exploitation is activated... proposed configuration for this environment is: " + proposed_config);
		}else
		{
			//explore
//			String proposed_config = knowledge.find_config_never_examined(current_env);
			proposed_config = knowledge.pick_random_config();
			System.out.println("exploration is activated... proposed configuration for this environment is: " + proposed_config);
		}
					
		if (proposed_config.compareTo("None")!=0) {
			knowledge.replace_rule(current_env, current_config, proposed_config);
			return true;
		}
		
		// no rule has been updated
		return false;
	}


	
}
