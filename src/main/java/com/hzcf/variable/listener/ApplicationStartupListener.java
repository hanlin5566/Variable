package com.hzcf.variable.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.hzcf.variable.engine.AlgorithmPool;
import com.hzcf.variable.misc.Constant;

/**
 * Create by hanlin on 2017年11月21日 上下文创建完成后执行的事件监听器，在此处读取数据库，并缓存起来。
 * TODO:启动日志不需要存入mongo
 **/
@Component("BeanDefineConfigue")
public class ApplicationStartupListener implements ApplicationListener<ContextRefreshedEvent> {
	@Value("${init.failed.exit}")
	private boolean initFailedExit;
	
	@Autowired
	private AlgorithmPool algorithmPool;
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		boolean success = Constant.SUCCESS.equals(algorithmPool.readVariable());
		// application.properties配置如果发生异常退出
		if (!success && initFailedExit) {
			System.exit(0);
		}
	}
}
