package com.hzcf.variable.engine.algorithms;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * Create by hanlin on 2017年11月24日
 * 申请人身份证到期日距离申请日期
 * 2001.11.12-2020.11.12
 * TODO:
 **/
public class X_APP_A01 extends AbstractAlgorithms{
	@Override
	public String execute(String param) throws Exception {
		JSONObject json = JSON.parseObject(param);
		Map<String, Object> var = this.getVar();
		String rec_name = var.get("var_rec_name").toString();
		String idCardValidDate = json.get(rec_name).toString();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
		Date sDate = sdf.parse(idCardValidDate.split("-")[0]);
		Date eDate = sdf.parse(idCardValidDate.split("-")[1]);
		Calendar cal = Calendar.getInstance();
		cal.setTime(sDate);
		int yearStart = cal.get(Calendar.YEAR);
		cal.setTime(eDate);
		int yearEnd = cal.get(Calendar.YEAR);

		int availabilityYear = yearEnd - yearStart;

		
		return String.valueOf(availabilityYear);
	}

}
