package com.hzcf.variable.model;

import com.alibaba.fastjson.JSON;

/**
 * Create by hanlin on 2017年11月21日
 **/
public class Receive {
	private String service;
	private JSON jsonParam;
	private String ruleId;
	private String taskId;
	private String requestIP;
	public String getRequestIP() {
		return requestIP;
	}
	public void setRequestIP(String requestIP) {
		this.requestIP = requestIP;
	}
	public String getRuleId() {
		return ruleId;
	}
	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public String getService() {
		return service;
	}
	public void setService(String service) {
		this.service = service;
	}
	public JSON getJSONParam() {
		return jsonParam;
	}
	
	public void setJSONParam(JSON jsonParam) {
		this.jsonParam = jsonParam;
	}
}
