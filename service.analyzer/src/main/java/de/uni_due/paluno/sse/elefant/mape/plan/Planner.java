package de.uni_due.paluno.sse.elefant.mape.plan;

import de.uni_due.paluno.sse.elefant.knowledgebase.Knowledge;

public class Planner {

	Knowledge db;
	Environment_State_Space env_state = new Environment_State_Space();
	int valid_time_window;

	public Planner(Knowledge knowledge, int vtw)
	{
		db = knowledge;
		env_state.load_environment_definition_from_db(db);		
		valid_time_window = vtw;
	}

	public String calculateNextConfig() {
    	
		RuleBase_PES rb = new RuleBase_PES();
		rb.loadRuleBaseFromMongoDB(db);
		
		String current_env_state = env_state.get_current_state(db,valid_time_window);		
//		System.out.println("current state of the environment: " + current_env_state);
		
		String next_config = rb.plan(current_env_state);
//		System.out.println("next configuration: " + next_config);
		return next_config;
    }
	
	public String get_env_state()
	{
		return env_state.get_current_state(db,valid_time_window);
	}
	
}
