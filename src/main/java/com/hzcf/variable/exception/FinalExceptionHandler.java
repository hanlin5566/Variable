package com.hzcf.variable.exception;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.hzcf.variable.model.Variable;

/**
 * Create by hanlin on 2017年12月4日
 **/
@RestController
public class FinalExceptionHandler implements ErrorController {
	private static final String PATH = "/error";

	@Autowired
	private ErrorAttributes errorAttributes;

	@RequestMapping(value = PATH, produces = { MediaType.APPLICATION_JSON_VALUE })
	public Variable error(HttpServletResponse resp, HttpServletRequest request) {
		// 错误处理逻辑
		Variable ret = buildBody(request,false);
		resp.setStatus(200);
		return ret;
	}


	private Variable buildBody(HttpServletRequest request,Boolean includeStackTrace) {
		Variable ret = new Variable();
		RequestAttributes requestAttributes = new ServletRequestAttributes(request);
		Map<String, Object> attr = errorAttributes.getErrorAttributes(requestAttributes, includeStackTrace);
		Integer status = (Integer) attr.get("status");
		String path = (String) attr.get("path");
		String messageFound = (String) attr.get("message");
		String message = "";
		String trace = "";
		if (!StringUtils.isEmpty(path)) {
			message = String.format("Requested path %s with result %s", path, messageFound);
		}
		if(includeStackTrace){
			trace = (String) attr.get("trace");
			if (!StringUtils.isEmpty(trace)) {
				message += String.format(" and trace %s status:", trace,status);
			}
		}
		attr.put("status",200);
		ret.setMessage(message);
		ret.setSuccess(false);
		return ret;
	}


	@Override
	public String getErrorPath() {
		return PATH;
	}
}