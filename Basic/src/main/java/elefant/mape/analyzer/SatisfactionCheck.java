package elefant.mape.analyzer;


import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import elefant.knowledgebase.Knowledge;

public class SatisfactionCheck {

	Knowledge monitor;
	RestAPIExecutor rest_executor = new RestAPIExecutor();
	ArrayList<Requirement> req_Def_Array;
	int valid_time_window;

	
	public SatisfactionCheck(Knowledge mntr,int vtw)
	{
		monitor = mntr;
		req_Def_Array = monitor.get_req_definition();
		valid_time_window = vtw;
	}

	// Error to be fixed: monitored_value will be "null" when the monitored data does not contain a certain req_name... so there will be an error in "isInRange" method
	public boolean is_req_violated(Hashtable<String, Double> req_Monitored_Array)
	{
		for (int i = 0; i < req_Def_Array.size(); i++) {
			Requirement req = req_Def_Array.get(i);
			Double monitored_value = req_Monitored_Array.get(req.req_name);
			System.out.println(req.req_name + " = " + monitored_value);
			if (!req.isInRange(monitored_value)) return true;
		}
		
		return false;
	}

	public Hashtable<String, Double> get_req_current_value() {
		return monitor.get_req_current_value(req_Def_Array,valid_time_window);
	}

	
	/*public void is_req_violated()
	{
		RestAPIExecutor rest_executor = new RestAPIExecutor();
		String restapi_metrics = "http://localhost:8080/dbwrapper/webapi/metrics";
		String result_json = rest_executor.get_restapi_json(restapi_metrics);
		System.out.println(result_json);

	}*/
	
}
