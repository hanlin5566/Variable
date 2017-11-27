package com.hzcf.variable.model;

import java.util.Map;

/**
 * Create by hanlin on 2017年11月21日
 * 返回给规则引擎的model
 **/
public class Variable {
	private boolean success;
	private String message;
	private Map<String,String> value;
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Map<String, String> getValue() {
		return value;
	}
	public void setValue(Map<String, String> value) {
		this.value = value;
	}
}
