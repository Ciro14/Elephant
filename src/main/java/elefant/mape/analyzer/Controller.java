package elefant.mape.analyzer;

import java.util.Hashtable;
import elefant.evolution.RewardManager;
import elefant.evolution.RuleManager;
import elefant.knowledgebase.Knowledge;
import elefant.mape.Execution.Executor;
import elefant.mape.plan.Planner;

public class Controller {

	
	static String restapi_reconfig = "http://10.8.100.124:5004/model";
	
	static int analysis_delay = 60000; // time to delay and check again in milliseconds
	static int valid_time_window = 60000; //only consider the data in the last minute for analysis of metrics
	
	public static void main(String[] args) throws InterruptedException {
		
		Knowledge knowledge = new Knowledge();

		//clear measurements and rewards -- only for testing
		knowledge.clear_measurements();		
		knowledge.clear_rewards();
		
		//MAPE components
		SatisfactionCheck req_satisfaction = new SatisfactionCheck(knowledge,valid_time_window);
		Planner planner = new Planner(valid_time_window);
		Executor executor = new Executor(restapi_reconfig);
		
		//Evolution components
		RuleManager rule_manager = new	RuleManager(knowledge);
		RewardManager reward_manager = new RewardManager(knowledge);
		
		String current_env = "";
		String current_config = "";
//		String current_config = monitor.get_current_config();
		
		while(true)
		{
			
			Thread.sleep(analysis_delay);
			
			// store the current reward in knowledge base
			current_env = planner.get_env_state();
			Hashtable<String, Double> current_req_values = req_satisfaction.get_req_current_value();
			reward_manager.store(current_env, current_config, current_req_values);
			
			if(req_satisfaction.is_req_violated(current_req_values))
			{
				String next_config = planner.calculateNextConfig();
				System.out.println("output of planner: "+ next_config);

				if (next_config!=null){
					if (next_config.compareTo(current_config)==0) {
						System.out.println("ineffective rule: the proposed config is the same as the current config leading to violation");
						if (rule_manager.replace_rule(current_env,current_config))
							System.out.println("ineffective rule is replaced");
						else System.out.println("Warning: no rule replacement despite ineffectiveness!");
						
					}else
					{
						executor.reconfigure(next_config);
						current_config = next_config;
					}
				}else
				{
					//we need a method here to add rules for unexpected environemnt
					System.out.println("Unexpect Env: there is no rule for such environment");
				}
			}
		}
	}

}