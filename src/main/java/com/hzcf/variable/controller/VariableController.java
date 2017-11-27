package com.hzcf.variable.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.hzcf.variable.misc.IPUtils;
import com.hzcf.variable.model.Receive;
import com.hzcf.variable.model.Variable;
import com.hzcf.variable.service.VariableService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * Create by hanlin on 2017年11月21日
 **/
@RestController
@RequestMapping(value = "/var")
@Api("衍生变量引擎")
public class VariableController {
	
	@Autowired VariableService varService;
	
	@ApiOperation(value = "执行衍生变量", notes = "根据传入的衍生接口名称，解析参数，返回衍生变量")
	@RequestMapping(value = { "" }, method = RequestMethod.POST)
	public Variable getDerivedVariableList(@RequestBody Receive rec,HttpServletRequest request) {
		try {
			rec.setRequestIP(IPUtils.getIpAddress(request));
		} catch (Exception e) {
		}
		return varService.execute(rec);
	}
}
