package com.mixislink.logging;

import org.apache.ibatis.io.Resources;

import java.lang.reflect.Constructor;

/**
 * 
 * <B>描述：</B>日志工厂类<br/>
 * <B>版本：</B>v2.0<br/>
 * <B>创建时间：</B>2012-10-10<br/>
 * <B>版权：</B>flying团队<br/>
 * 
 * @author zdf
 *
 */
public class LogFactory {
	private static Constructor logConstructor;

	static {
//		tryImplementation("org.apache.commons.logging.LogFactory",
//				"com.mixislink.logging.jakarta.JakartaLoggingImpl");
		tryImplementation("org.apache.log4j.Logger",
				"com.mixislink.logging.log4j.Log4jImpl");
//		tryImplementation("java.util.logging.Logger",
//				"com.mixislink.logging.jdk14.Jdk14LoggingImpl");
//		tryImplementation("java.lang.Object",
//				"com.mixislink.logging.nologging.NoLoggingImpl");
	}

	private static void tryImplementation(String testClassName,
			String implClassName) {
		if (logConstructor == null) {
			try {
				Resources.classForName(testClassName);
				Class implClass = Resources.classForName(implClassName);
				logConstructor = implClass
						.getConstructor(new Class[] { Class.class });
			} catch (Throwable t) {

			}
		}
	}
	
	/**
	 * 获取日志实例
	 * @param aClass
	 * @return 日志实例
	 */
	public static Log getLog(Class aClass) {
		try {
			return (Log) logConstructor.newInstance(new Object[] { aClass });
		} catch (Throwable t) {
			throw new RuntimeException("Error creating logger for class "
					+ aClass + ".  Cause: " + t, t);
		}
	}
	
	/**
	 * 使用log4j记录日志
	 */
	public static synchronized void selectLog4JLogging() {
		try {
			Resources.classForName("org.apache.log4j.Logger");
			Class implClass = Resources
					.classForName("com.ibatis.common.logging.log4j.Log4jImpl");
			logConstructor = implClass
					.getConstructor(new Class[] { Class.class });
		} catch (Throwable t) {
		}
	}
	/**
	 * 使用java log记录日志
	 */
	public static synchronized void selectJavaLogging() {
		try {
			Resources.classForName("java.util.logging.Logger");
			Class implClass = Resources
					.classForName("com.ibatis.common.logging.jdk14.Jdk14LoggingImpl");
			logConstructor = implClass
					.getConstructor(new Class[] { Class.class });
		} catch (Throwable t) {
		}
	}
}
