package elefant.mape.plan;

import elefant.knowledgebase.Knowledge;

public class Planner {

	Knowledge db = new Knowledge();
	Environment_State_Space env_state = new Environment_State_Space();
	int valid_time_window;

	public Planner(int vtw)
	{
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
