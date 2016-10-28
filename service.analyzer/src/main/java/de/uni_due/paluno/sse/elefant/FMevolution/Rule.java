package de.uni_due.paluno.sse.elefant.FMevolution;

import java.util.List;

public class Rule {

	private int rule_id;
	private List<String> configuration;
	private String metric_id;
	private String operator;
	private int metric_value;
	
	public Rule(int rule_id, String metric_id, String operator, int metric_value, List<String> config) {
		this.rule_id = rule_id;
		this.configuration = config;
		this.metric_id = metric_id;
		this.operator  = operator;
		this.metric_value = metric_value;
	}

	public int getRule_id() {
		return rule_id;
	}

	public void setRule_id(int rule_id) {
		this.rule_id = rule_id;
	}

	public List<String> getConfiguration() {
		return configuration;
	}

	public void setConfiguration(List<String> configuration) {
		this.configuration = configuration;
	}

	public String getMetric_id() {
		return metric_id;
	}

	public void setMetric_id(String metric_id) {
		this.metric_id = metric_id;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public int getMetric_value() {
		return metric_value;
	}

	public void setMetric_value(int metric_value) {
		this.metric_value = metric_value;
	}
}
