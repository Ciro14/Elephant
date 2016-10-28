package de.uni_due.paluno.sse.elefant.mape.Execution;

import de.uni_due.paluno.sse.elefant.mape.analyzer.RestAPIExecutor;

public class Executor {


	RestAPIExecutor rest_executor = new RestAPIExecutor();
	String restapi_reconfig;
	
	public Executor(String restapi)
	{
		restapi_reconfig = restapi;
	}
	
	public void reconfigure(String next_config)
	{
		rest_executor.put_restapi_json(restapi_reconfig, next_config);
	}
}
