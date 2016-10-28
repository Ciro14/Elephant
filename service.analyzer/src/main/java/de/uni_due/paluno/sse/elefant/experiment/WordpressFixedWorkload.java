package de.uni_due.paluno.sse.elefant.experiment;

import java.io.IOException;

import de.uni_due.paluno.sse.elefant.mape.analyzer.Controller;

public class WordpressFixedWorkload {

	static String initial_db = "initial_db";
	static String temporal_db_name = "test";
	static String permanent_db_name = "experiment_Workload_";
	
	static int analysis_delay = 30000; // time to delay and check again in milliseconds
	static int valid_time_window = 30000; //only consider the data in the last minute for analysis of metrics

	// number of analysis control
	static int num_analysis = 100;
	
	static String jMeter_Plan = "Workload-";
	static int monitor_delay = 10000;

	static int exploitation_percent = 20;
	
	public static void main(String[] args){
		
		long exp_length = analysis_delay * num_analysis ; // one hour

		int workload = 30;

		// run JMeter
		jMeter_Plan += workload;
		jMeter_Plan += ".jmx";
		
		// start JMeter
		try {
			String jmeter_start_command = "java -jar C:/Users/zakhar/Desktop/apache-jmeter-3.0.tgz/apache-jmeter-3.0/bin/ApacheJMeter.jar -n -t C:/Users/zakhar/Desktop/apache-jmeter-3.0.tgz/apache-jmeter-3.0/bin/" + jMeter_Plan;			
			Runtime.getRuntime().exec(jmeter_start_command);
			System.out.println("JMeter started...");
		} catch (IOException e) {
			System.out.println("JMeter failed to start...");
			System.out.println(e.getMessage());
		}
			
/*	
		String monitor_start_command  = "java -jar C:/Users/zakhar/Desktop/MonitorWorkload.jar " + workload + "  " + monitor_delay + " " + temporal_db_name;
		Runtime rt = Runtime.getRuntime();
		Process p = null;
		// start Monitor program to push the workload into MongoDB
		try {
			rt = Runtime.getRuntime();
			p = rt.exec(monitor_start_command);
			System.out.println("Monitor started...");
		} catch (Exception e) {
			System.out.println("Monitor failed to start...");
			System.out.println(e.getMessage());
		}
*/	
		
		// start the controller
		Controller controller = new Controller(initial_db, temporal_db_name, valid_time_window,exploitation_percent);
		// set the initial application configuration
		String initial_config = "[" + '"' + "vm1" + '"' + "]";
		controller.set_application_configuration(initial_config);
		
		System.out.println("Req control started...");
		controller.analyze_and_evolve(initial_config,analysis_delay,num_analysis);
		System.out.println("Req control is stopped...");
		
		//store the results in permanent MongoDB
		permanent_db_name += workload + "_" +  System.currentTimeMillis() + "_" + exploitation_percent;
		controller.store_permanent(permanent_db_name);
			
		//remove temporal db from MongoDB
		controller.drop_temporal_db();
					
	
		// stop JMeter
		try {
			String jmeter_stop_command = "C:/Users/zakhar/Desktop/apache-jmeter-3.0.tgz/apache-jmeter-3.0/bin/shutdown.cmd";
			ProcessBuilder pb = new ProcessBuilder(jmeter_stop_command);
			pb.start();
			System.out.println("JMeter is stopped...");
		} catch (IOException e) {
			System.out.println("JMeter failed to stop...");
			System.out.println(e.toString());
		}
		
//		if (p!=null)
//			p.destroy();
//		System.out.println("Monitor is stopped...");
		
		
	}	
	
}
