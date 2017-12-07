package com.hzcf.variable.controller;

import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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
	public Variable getDerivedVariableList(HttpServletRequest request) {
		InputStream inputStream = null;
		Receive rec = new Receive();
        try {
            inputStream = request.getInputStream();
            String json = IOUtils.toString(inputStream, "UTF-8");
            IOUtils.closeQuietly(inputStream);
            JSONObject recJson = JSON.parseObject(json);
            rec.setRuleId(recJson.getString("ruleId"));
            rec.setService(recJson.getString("service"));
            rec.setTaskId(recJson.getString("taskId"));
            String param = recJson.getString("param");
			if(param.startsWith("[")){
				rec.setJSONParam(JSON.parseArray(param));
			}else{
				rec.setJSONParam(JSON.parseObject(param));
			}
			rec.setRequestIP(IPUtils.getIpAddress(request));
		} catch (Exception e) {
			Variable ret = new Variable();
			ret.setMessage("接收参数出现异常："+e.getMessage());
			ret.setSuccess(false);
			return ret;
		}
		return varService.execute(rec);
	}
}
