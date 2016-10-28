package de.uni_due.paluno.sse.elefant;

import de.uni_due.paluno.sse.elefant.mape.analyzer.Controller;


public class Main {

    static String initial_db = "elephant";
    static String temporal_db_name = "elephant_temp";

    static int analysis_delay = 15000; // time to delay and check again in milliseconds
    static int valid_time_window = 15000; //only consider the data in the last minute for analysis of metrics

    // number of analysis control
    static int num_analysis = 100;


    static int exploitation_percent = 20;

    public static void main(String[] args){

        //Configure
        Controller.restapi_reconfig=args[0];

        //Controller controller = new Controller(initial_db, temporal_db_name, valid_time_window,exploitation_percent);
        Controller controller = new Controller(initial_db, initial_db, valid_time_window,exploitation_percent);
        String initial_config = args[1];
        controller.set_application_configuration(initial_config);

        System.out.println("Req control started...");
        controller.analyze_and_evolve(initial_config,analysis_delay,num_analysis);
        System.out.println("Req control is stopped...");
    }
}
