package elefant.mape.Execution;

import elefant.mape.analyzer.RestAPIExecutor;

public class Executor {


	RestAPIExecutor rest_executor = new RestAPIExecutor();
	String restapi_reconfig;
	
	public Executor(String restapi)
	{
		restapi_reconfig = restapi;
	}
	
	public void reconfigure(String next_config)
	{
		rest_executor.post_restapi_json(restapi_reconfig, next_config);
	}
}
