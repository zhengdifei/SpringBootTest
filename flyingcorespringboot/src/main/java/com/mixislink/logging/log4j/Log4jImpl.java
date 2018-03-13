package com.mixislink.logging.log4j;

import com.mixislink.init.StaticVariable;
import com.mixislink.logging.Log;
import com.mixislink.logging.LogCache;
import org.apache.log4j.Logger;

/**
 * 
 * <B>描述：</B>自定义的log4j实现类<br/>
 * <B>版本：</B>v3.0<br/>
 * <B>创建时间：</B>2018-03-18<br/>
 * <B>版权：</B>flying团队<br/>
 * 
 * @author zdf
 *
 */
public class Log4jImpl implements Log {
	
	private Logger log;
	
	private String className;
	
	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public Log4jImpl(Class clazz){
		setClassName(clazz.getName());
		
		log = Logger.getLogger(clazz);
	}
	
	@Override
	public boolean isDebugEnabled() {
		return log.isDebugEnabled();
	}

	@Override
	public void debug(String s) {
		log.debug("类："+className+"	-- "+s);
	}

	@Override
	public void info(String s) {
		log.info("类："+className+"	-- "+s);
	}

	@Override
	public void warn(String s) {
		log.warn("类："+className+"	-- "+s);
	}

	@Override
	public void error(String s) {
		log.error("类："+className+"	-- "+s);
	}

	@Override
	public void error(String s, Throwable e) {
		log.error("类："+className+"	-- "+s, e);
	}

	@Override
	public void log(String threadId,String command,String commandType,String commandParams) {
		if(StaticVariable.LOG){
			LogCache.addLog(threadId,command,commandType,commandParams);
		}
	}
}
