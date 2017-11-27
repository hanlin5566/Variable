package com.hzcf.variable.engine.algorithms;

import java.util.Map;

import com.alibaba.fastjson.JSON;
/**
 * Create by hanlin on 2017年11月22日
 * 直接变量的处理算法。
 * 只有一层JSON，按照变量名取得相应的值。
 * 
 * TODO:日志需要统一由mongo处理，返回值由外部统一处理，如果为null或者为""，则取数据库中的默认值。
 **/
public class DirectVariableAlgorithms extends AbstractAlgorithms{
	@Override
	public String execute(String param) throws Exception {
		String varRecName = getVar().get("var_rec_name").toString();
		Object obj = JSON.parse(param);
		if(obj instanceof Map){
			Map<?,?> map = (Map<?,?>)obj;
			return map.get(varRecName).toString();
		}else{
			throw new Exception("此变量不支持由直接变量算法处理");
		}
		
	}
	
}
