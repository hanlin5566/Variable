package com.hzcf.variable.engine;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.hzcf.variable.loader.DynamicClassLoader;
import com.hzcf.variable.misc.Constant;
import com.hzcf.variable.misc.GlobalLock;

/**
 * Create by hanlin on 2017年11月21日
 * 衍生变量算法池
 **/
@Component
public class AlgorithmPool {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private JdbcTemplate jdbcTemplate;
	/**
	 * 由于存在读写操作，所以采用，线程安全的map。
	 */
	private Map<String,Map<String,DerivedAlgorithms>> algorithmMap = new ConcurrentHashMap<String,Map<String,DerivedAlgorithms>>();
	
	/**
	 * 由于使用了全局锁统一管理算法池，所以单独put算法过期，可能会导致线程不安全。
	 * @param service
	 * @param varName
	 * @param algorithms
	 */
	@Deprecated()
	public void putAlgorithm(String service,String varName,DerivedAlgorithms algorithms){
		if(algorithmMap.containsKey(service)){
			Map<String, DerivedAlgorithms> map = algorithmMap.get(service);
			map.put(varName, algorithms);
		}else{
			Map<String, DerivedAlgorithms> map = new HashMap<String, DerivedAlgorithms>();
			map.put(varName, algorithms);
			algorithmMap.put(service, map);
		}
	}
	
	private synchronized void setAlgorithms(Map<String,Map<String,DerivedAlgorithms>> algorithmMap){
		this.algorithmMap = algorithmMap;
	}
	
	public Map<String,DerivedAlgorithms> getAlgorithms(String service) {
		return algorithmMap.get(service);
	}
	public DerivedAlgorithms getAlgorithm(String service,String varName) {
		if(!algorithmMap.containsKey(service)){
			return null;
		}
		return algorithmMap.get(service).get(varName);
	}
	
	/**
	 * 从持久层中加载衍生变量算法
	 */
	public String readVariable(){
		String info = Constant.SUCCESS;
		//临时算法池
		Map<String,Map<String,DerivedAlgorithms>> templateAlgorithmPool = new ConcurrentHashMap<String,Map<String,DerivedAlgorithms>>();
		// 加载类加载器
		ClassLoader classLoader = this.getClass().getClassLoader();
		DynamicClassLoader dynamicClassLoader = new DynamicClassLoader(classLoader);
		// 查询未删除已发布的数据
		String sql = "select g.query_iface,v.* from derived_var v,derived_var_group g where v.var_group_id = g.var_group_id and  v.data_status = 1  and g.data_status = 1 and v.state = 3";
		List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(sql);
		// 使用hashtable的值非空特性，保证class必定有值。
		logger.info(String.format("初始化[%s]个算法", queryForList.size()));
		Map<String, Class<?>> classNameMap = new Hashtable<String, Class<?>>();
		for (Map<String, Object> var : queryForList) {
			String service = var.get("query_iface").toString();
			String varName = var.get("var_ret_name").toString();//返回name系统内的唯一标识
			int varType = Integer.parseInt(var.get("var_type").toString());//衍生变量类型，1为直接变量，2为衍生变量。
			//如果为直接变量，则添加默认的直接变量处理类。
			if(var.get("clazz_name") == null && varType == 1){
				var.put("clazz_name", Constant.DIRECT_VARIABLE_ALGORITHMS);
			}
			String className = var.get("clazz_name").toString();
			try {
				// 同一个class，在同一个classLoader只存在一个。
				if (!classNameMap.containsKey(className)) {
					if(varType == 1){
						classNameMap.put(className, Class.forName(Constant.DIRECT_VARIABLE_ALGORITHMS));
					}else{
						byte[] classFile = (byte[]) var.get("class_file");
						//衍生变量需要读取文件动态加载class
						Class<?> generateClass = dynamicClassLoader.generateClass(className, classFile);
						classNameMap.put(className, generateClass);
					}
				}
				DerivedAlgorithms algorithms = (DerivedAlgorithms) classNameMap.get(className).newInstance();
				algorithms.setVarName(varName);
				algorithms.setVar(var);
				putAlgorithm(templateAlgorithmPool,service, varName, algorithms);
				logger.info(String.format("初始化接口:[%s],变量名:[%s],算法:[%s]", service, varName, algorithms.getClass().getName()));
			} catch (ClassNotFoundException notFound) {
				info = "加载算法["+varName+"]失败,无法找到类:" + className;
				logger.error(info,notFound);
			} catch (ClassCastException classCast) {
				info = "加载算法["+varName+"]失败,类未实现DerivedAlgorithms接口";
				logger.error(info, classCast);
			} catch (NullPointerException nullPointerException) {
				info = "加载算法["+varName+"]失败,实例化算法为空";
				logger.error(info, nullPointerException);
			} catch (Exception e) {
				info = "加载算法["+varName+"]失败," + e.getCause();
				logger.error(info, e.fillInStackTrace());
			}
		}
		//写入算法池，获取全局写锁。
		GlobalLock.writeLock.lock();
		setAlgorithms(templateAlgorithmPool);
		GlobalLock.writeLock.unlock();
		return info;
	}
	
	private void putAlgorithm(Map<String,Map<String,DerivedAlgorithms>> algorithmMap,String service,String varName,DerivedAlgorithms algorithms){
		if(algorithmMap.containsKey(service)){
			Map<String, DerivedAlgorithms> map = algorithmMap.get(service);
			map.put(varName, algorithms);
		}else{
			Map<String, DerivedAlgorithms> map = new HashMap<String, DerivedAlgorithms>();
			map.put(varName, algorithms);
			algorithmMap.put(service, map);
		}
	}
}
