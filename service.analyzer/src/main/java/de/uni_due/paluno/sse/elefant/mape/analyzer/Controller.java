package de.uni_due.paluno.sse.elefant.mape.analyzer;

import java.util.Hashtable;
import de.uni_due.paluno.sse.elefant.evolution.RewardManager;
import de.uni_due.paluno.sse.elefant.evolution.RuleManager;
import de.uni_due.paluno.sse.elefant.mape.Execution.Executor;
import de.uni_due.paluno.sse.elefant.knowledgebase.Knowledge;
import de.uni_due.paluno.sse.elefant.mape.plan.Planner;

public class Controller {

	
	public static String restapi_reconfig = "http://localhost:8082/model";
	
	Knowledge knowledge;
	SatisfactionCheck req_satisfaction;
	Planner planner;
	Executor executor;
	RuleManager rule_manager;
	RewardManager reward_manager;
	
	public Controller(String initial_db_name, String db_name, int valid_time_window, int exploitation_percent)
	{
		knowledge = new Knowledge(initial_db_name, db_name);
		
		//clear measurements and rewards -- only for testing
		knowledge.clear_measurements();
		knowledge.clear_rewards();
		
		//MAPE components
		req_satisfaction = new SatisfactionCheck(knowledge,valid_time_window);
		planner = new Planner(knowledge, valid_time_window);
		executor = new Executor(restapi_reconfig);
		
		//Evolution components
		rule_manager = new	RuleManager(knowledge,exploitation_percent);
		reward_manager = new RewardManager(knowledge);
	}
	
	public void store_permanent(String permanent_db_name)
	{
		knowledge.store_permanent_db(permanent_db_name);
	}
	
	public void drop_temporal_db(){
		knowledge.drop_db();
	}
	
//	public static void main(String[] args) throws InterruptedException {	}
	
	public void analyze_and_evolve(String initial_config, int analysis_delay, int num_analysis){
		
		String current_env = "";
		String current_config = initial_config;
		
		int num_req_violation = 0;
		
		
		int num_analysis_done = 0;
		
		while( num_analysis_done < num_analysis )
		{
			
			try {
				Thread.sleep(analysis_delay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			// observe the current situation
			current_env = planner.get_env_state();
			Hashtable<String, Double> current_req_values = req_satisfaction.get_req_current_value();

			// log the situation + configuration
			knowledge.store_situation_one_req(current_env, current_config, current_req_values);
			
			// store the current reward in knowledge base			
			reward_manager.update(current_env, current_config, current_req_values);
			
			if(req_satisfaction.is_req_violated(current_req_values))
			{
				num_req_violation++;
				
				// make a reconfiguration plan
				String next_config = planner.calculateNextConfig();
				System.out.println("output of planner: "+ next_config);

				// check if any plan is generated
				if (next_config!=null){
					
					// activate rule evolution if reconfiguration plan is the current plan
					if (next_config.compareTo(current_config)==0) {
						System.out.println("ineffective rule: the proposed config is the same as the current config leading to violation");
						if (rule_manager.replace_rule(current_env,current_config))
							System.out.println("ineffective rule is replaced");
						else System.out.println("Warning: no rule replacement despite ineffectiveness!");
						
					}else
					// execute the plan
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
			
			// log the accumulative number of requirements violations from beginning to now - only for experiment/reporting purposes
			knowledge.store_accumu_num_req_violation(num_req_violation);
			
			num_analysis_done++;
			
		}
		
	} 
/*	
	public void analyze_classic_mape(String initial_config, long exp_duration){
		
		String current_env = "";
		String current_config = initial_config;
//		String current_config = monitor.get_current_config();
		
		long initial_time = System.currentTimeMillis();
		
		while( System.currentTimeMillis() < (initial_time+exp_duration) )
		{
			
			try {
				Thread.sleep(analysis_delay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			// store the current reward in knowledge base
			current_env = planner.get_env_state();
			Hashtable<String, Double> current_req_values = req_satisfaction.get_req_current_value();
			reward_manager.update(current_env, current_config, current_req_values);
			
			if(req_satisfaction.is_req_violated(current_req_values))
			{
				String next_config = planner.calculateNextConfig();
				System.out.println("output of planner: "+ next_config);

				if (next_config!=null){
					executor.reconfigure(next_config);
					current_config = next_config;
				}else
				{
					//we need a method here to add rules for unexpected environment
					System.out.println("Unexpect Env: there is no rule for such environment");
				}
			}
		}
	}
*/
	public void set_application_configuration(String initial_config) {
		executor.reconfigure(initial_config);		
	} 

}